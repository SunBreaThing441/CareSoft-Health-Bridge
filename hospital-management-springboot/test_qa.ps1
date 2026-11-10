$base = "http://localhost:8080"

function Normalize-Url {
    param($url)
    if ($url.StartsWith("http")) { return $url }
    return "$base$url"
}

function Test-Account {
    param($email, $password, $expectedRedirect)
    $session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
    
    # 1. GET /login to extract CSRF token and cookie
    $loginPage = Invoke-WebRequest -Uri "$base/login" -WebSession $session -UseBasicParsing
    $csrf = ""
    if ($loginPage.Content -match 'name="_csrf"\s+value="([^"]+)"') {
        $csrf = $matches[1]
    } else {
        Write-Host "[$email] ERROR: Could not find CSRF token on /login" -ForegroundColor Red
        return $null
    }

    # 2. POST /login
    $formBody = @{
        username = $email
        password = $password
        _csrf = $csrf
    }
    
    $loginResp = Invoke-WebRequest -Uri "$base/login" -Method POST -Body $formBody -WebSession $session -MaximumRedirection 0 -ErrorAction SilentlyContinue -UseBasicParsing
    $redirectLoc = Normalize-Url $loginResp.Headers['Location']
    Write-Host "[$email] POST /login -> Status: $($loginResp.StatusCode), Redirect: $redirectLoc"
    
    # 3. Follow redirect to /dashboard
    $dashResp = Invoke-WebRequest -Uri $redirectLoc -WebSession $session -MaximumRedirection 0 -ErrorAction SilentlyContinue -UseBasicParsing
    $dashTarget = $dashResp.Headers['Location']
    $passRoute = ($dashTarget -eq $expectedRedirect -or $dashTarget -eq "$base$expectedRedirect")
    Write-Host "[$email] /dashboard Router -> $dashTarget (Expected: $expectedRedirect) [PASS: $passRoute]"
    
    # 4. Fetch the target dashboard page
    $targetUrl = Normalize-Url $dashTarget
    $targetPage = Invoke-WebRequest -Uri $targetUrl -WebSession $session -UseBasicParsing
    $hasBrand = $targetPage.Content.Contains("CareSoft Health Bridge: UNESCO Global Health Logistics")
    Write-Host "[$email] Dashboard Status: $($targetPage.StatusCode), CareSoft Brand Verified: $hasBrand"
    
    return $session
}

Write-Host "=== TEST 1: PRE-CONFIGURED ACCOUNTS AUTH & ROUTING ==="
$adminSession = Test-Account "admin@hospital.com" "password" "/admin/dashboard"
$docSession   = Test-Account "sarah@hospital.com" "password" "/doctor/dashboard"
$patSession   = Test-Account "patient@hospital.com" "password" "/patient/dashboard"

Write-Host "`n=== TEST 2: DECOUPLED JSON CALENDAR FEEDS ==="
# Patient events
$patEvents = Invoke-RestMethod -Uri "$base/appointment/events" -WebSession $patSession -UseBasicParsing
Write-Host "Patient /appointment/events count: $($patEvents.Count)"
if ($patEvents.Count -gt 0) {
    Write-Host "Sample Patient Event: ID=$($patEvents[0].id), Title='$($patEvents[0].title)', Start='$($patEvents[0].start)', Status='$($patEvents[0].status)'"
}

# Doctor events
$docEvents = Invoke-RestMethod -Uri "$base/appointment/doctor/events" -WebSession $docSession -UseBasicParsing
Write-Host "Doctor /appointment/doctor/events count: $($docEvents.Count)"
if ($docEvents.Count -gt 0) {
    Write-Host "Sample Doctor Event: ID=$($docEvents[0].id), Title='$($docEvents[0].title)', Patient='$($docEvents[0].patientName)', Status='$($docEvents[0].status)'"
}

Write-Host "`n=== TEST 3: ALL PUBLIC & PROTECTED ROUTES AUDIT (ACTIVE SESSIONS) ==="
$routes = @(
    @{ Path = "/login"; Session = $null },
    @{ Path = "/register"; Session = $null },
    @{ Path = "/admin/users"; Session = $adminSession },
    @{ Path = "/admin/appointments"; Session = $adminSession },
    @{ Path = "/appointment/book"; Session = $patSession },
    @{ Path = "/appointment/calendar"; Session = $patSession }
)

foreach ($item in $routes) {
    $r = $item.Path
    $sess = $item.Session
    try {
        if ($sess) {
            $res = Invoke-WebRequest -Uri "$base$r" -WebSession $sess -UseBasicParsing
        } else {
            $res = Invoke-WebRequest -Uri "$base$r" -UseBasicParsing
        }
        $brandCheck = $res.Content.Contains("CareSoft Health Bridge: UNESCO Global Health Logistics")
        Write-Host "Route $r -> HTTP $($res.StatusCode), CareSoft Branding: $brandCheck"
    } catch {
        Write-Host "Route $r -> FAILED with $($_.Exception.Message)"
    }
}

Write-Host "`n=== TEST 4: CSRF GUARD & LOGOUT TERMINATION ==="
# Extract post-login CSRF token from patient dashboard
$dashPage = Invoke-WebRequest -Uri "$base/patient/dashboard" -WebSession $patSession -UseBasicParsing
if ($dashPage.Content -match 'name="_csrf"\s+content="([^"]+)"') {
    $authCsrf = $matches[1]
    Write-Host "Post-Auth CSRF Token Extracted: $authCsrf"
    
    # Execute Logout via POST /logout with X-CSRF-TOKEN
    $logoutResp = Invoke-WebRequest -Uri "$base/logout" -Method POST -Headers @{ "X-CSRF-TOKEN" = $authCsrf } -WebSession $patSession -MaximumRedirection 0 -ErrorAction SilentlyContinue -UseBasicParsing
    Write-Host "POST /logout with X-CSRF-TOKEN -> Status: $($logoutResp.StatusCode), Redirect: $($logoutResp.Headers['Location'])"
    
    # Verify session is destroyed
    $postLogoutResp = Invoke-WebRequest -Uri "$base/patient/dashboard" -WebSession $patSession -MaximumRedirection 0 -ErrorAction SilentlyContinue -UseBasicParsing
    $isRedirectedToLogin = ($postLogoutResp.Headers['Location'] -match "/login")
    Write-Host "Access /patient/dashboard after logout -> Redirected to Login: $isRedirectedToLogin"
}
