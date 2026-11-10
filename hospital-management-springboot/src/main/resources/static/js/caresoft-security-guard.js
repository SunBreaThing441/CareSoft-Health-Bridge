/**
 * CareSoft Health Bridge: UNESCO Global Health Logistics
 * Client-Side Security Micro-Engine & Navigation Guard
 * 
 * Provides:
 * 1. Automatic CSRF extraction and X-CSRF-TOKEN header injection on all mutating requests.
 * 2. Role-based client-side route guards replicating the Spring Security /dashboard smart router.
 * 3. Unified, secure session termination (logout) with CSRF protection.
 * 4. Transparent fetch() interceptor eliminating the need for hidden form tokens.
 */
(function (window, document) {
    'use strict';

    class CareSoftSecurityEngine {
        constructor() {
            this.csrfToken = null;
            this.csrfHeader = 'X-CSRF-TOKEN';
            this.session = {
                isAuthenticated: false,
                role: null,
                email: null,
                name: null
            };

            this.init();
        }

        init() {
            this.extractCsrfFromMeta();
            this.extractSessionFromDom();
            this.installFetchInterceptor();
            this.installFormInterceptors();
            this.enforceClientRouteGuard();
        }

        /**
         * Extracts CSRF token from HTML head meta tags injected by Thymeleaf
         */
        extractCsrfFromMeta() {
            const metaToken = document.querySelector('meta[name="_csrf"]');
            const metaHeader = document.querySelector('meta[name="_csrf_header"]');

            if (metaToken && metaToken.getAttribute('content')) {
                this.csrfToken = metaToken.getAttribute('content');
            }
            if (metaHeader && metaHeader.getAttribute('content')) {
                this.csrfHeader = metaHeader.getAttribute('content');
            }

            // Fallback: search for input[name="_csrf"]
            if (!this.csrfToken) {
                const inputToken = document.querySelector('input[name="_csrf"]');
                if (inputToken && inputToken.value) {
                    this.csrfToken = inputToken.value;
                }
            }
        }

        /**
         * Extracts active user session telemetry from DOM data attributes
         */
        extractSessionFromDom() {
            const body = document.body;
            if (body.dataset.userRole) {
                this.session.role = body.dataset.userRole;
                this.session.isAuthenticated = true;
            }
            if (body.dataset.userEmail) {
                this.session.email = body.dataset.userEmail;
            }
            if (body.dataset.userName) {
                this.session.name = body.dataset.userName;
            }

            // If not tagged, infer role from URL path
            const path = window.location.pathname;
            if (path.startsWith('/admin')) {
                this.session.role = 'ADMIN';
                this.session.isAuthenticated = true;
            } else if (path.startsWith('/doctor')) {
                this.session.role = 'DOCTOR';
                this.session.isAuthenticated = true;
            } else if (path.startsWith('/patient') || path.startsWith('/appointment')) {
                if (!path.includes('/doctor/events')) {
                    this.session.role = this.session.role || 'PATIENT';
                    this.session.isAuthenticated = true;
                }
            }
        }

        /**
         * Returns current CSRF token, fetching dynamically if absent
         */
        async getCsrfToken() {
            if (this.csrfToken) return this.csrfToken;

            try {
                const response = await fetch('/login', { credentials: 'include' });
                const html = await response.text();
                const match = html.match(/name="_csrf"\s+value="([^"]+)"/);
                if (match && match[1]) {
                    this.csrfToken = match[1];
                    return this.csrfToken;
                }
            } catch (err) {
                console.error('[CareSoft Security] Failed to sync CSRF token:', err);
            }
            return '';
        }

        /**
         * Global Fetch API interceptor injecting X-CSRF-TOKEN on mutating requests
         */
        installFetchInterceptor() {
            const originalFetch = window.fetch;
            const self = this;

            window.fetch = async function (resource, options = {}) {
                options = options || {};
                const method = (options.method || 'GET').toUpperCase();

                // Mutating requests require CSRF token
                if (['POST', 'PUT', 'DELETE', 'PATCH'].includes(method)) {
                    options.headers = options.headers || {};
                    const token = await self.getCsrfToken();

                    if (token) {
                        if (options.headers instanceof Headers) {
                            options.headers.set(self.csrfHeader, token);
                        } else if (Array.isArray(options.headers)) {
                            options.headers.push([self.csrfHeader, token]);
                        } else {
                            options.headers[self.csrfHeader] = token;
                        }
                    }
                    options.credentials = options.credentials || 'include';
                }

                return originalFetch(resource, options);
            };
        }

        /**
         * Intercepts traditional HTML forms to inject X-CSRF-TOKEN headers via fetch or hidden fields
         */
        installFormInterceptors() {
            const self = this;
            document.addEventListener('submit', function (e) {
                const form = e.target;
                if (!form || (form.method || '').toUpperCase() !== 'POST') return;

                // Ensure CSRF input exists as fallback
                let csrfInput = form.querySelector('input[name="_csrf"]');
                if (!csrfInput && self.csrfToken) {
                    csrfInput = document.createElement('input');
                    csrfInput.type = 'hidden';
                    csrfInput.name = '_csrf';
                    csrfInput.value = self.csrfToken;
                    form.appendChild(csrfInput);
                }
            }, true);
        }

        /**
         * Client-side Navigation Guard enforcing role boundaries
         */
        enforceClientRouteGuard() {
            const path = window.location.pathname;

            // Route clearance matrix
            if (path.startsWith('/admin') && this.session.role && this.session.role !== 'ADMIN') {
                this.handleUnauthorizedAccess('ADMINISTRATOR clearance required.');
            } else if (path.startsWith('/doctor') && this.session.role && this.session.role !== 'DOCTOR' && this.session.role !== 'ADMIN') {
                this.handleUnauthorizedAccess('MEDICAL OFFICER clearance required.');
            }
        }

        /**
         * Handles unauthorized route traversal with HUD notification and safe redirection
         */
        handleUnauthorizedAccess(message) {
            console.warn('[CareSoft Security] Access Violation: ' + message);
            this.showSecurityAlert('ACCESS VIOLATION: ' + message);

            setTimeout(() => {
                window.location.href = '/dashboard';
            }, 1200);
        }

        /**
         * Displays a futuristic glassmorphic security toast
         */
        showSecurityAlert(msg) {
            const toast = document.createElement('div');
            toast.className = 'fixed top-6 right-6 z-[9999] px-5 py-4 rounded-2xl bg-red-950/90 border border-red-500/50 text-red-200 text-xs font-mono shadow-2xl backdrop-blur-xl flex items-center gap-3 transition-all duration-300';
            toast.innerHTML = `
                <span class="w-3 h-3 rounded-full bg-red-500 animate-ping"></span>
                <div>
                    <div class="font-bold uppercase tracking-wider text-white">Security Guard Alert</div>
                    <div class="mt-0.5 text-slate-300">${msg}</div>
                </div>
            `;
            document.body.appendChild(toast);
            setTimeout(() => toast.remove(), 4000);
        }

        /**
         * Unified, secure logout terminating Spring Security session via POST with X-CSRF-TOKEN
         */
        async logout() {
            const token = await this.getCsrfToken();
            try {
                const response = await fetch('/logout', {
                    method: 'POST',
                    headers: {
                        [this.csrfHeader]: token,
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    credentials: 'include'
                });

                // Clear memory state
                this.session = { isAuthenticated: false, role: null, email: null, name: null };
                this.csrfToken = null;

                // Redirect to login with logout query parameter
                window.location.href = '/login?logout';
            } catch (err) {
                console.error('[CareSoft Security] Logout request failed:', err);
                // Fallback traditional form submission
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '/logout';
                if (token) {
                    const input = document.createElement('input');
                    input.type = 'hidden';
                    input.name = '_csrf';
                    input.value = token;
                    form.appendChild(input);
                }
                document.body.appendChild(form);
                form.submit();
            }
        }
    }

    // Expose singleton to global window scope
    window.CareSoftSecurity = new CareSoftSecurityEngine();

})(window, document);
