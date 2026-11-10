
package com.hms.hospital.controller;

import com.hms.hospital.entity.*;
import com.hms.hospital.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")  // Only Admin can access
public class AdminController {

    private final UserRepository userRepo;
    private final PatientRepository patientRepo;
    private final AppointmentRepository appointmentRepo;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", userRepo.count());
        model.addAttribute("totalPatients", patientRepo.count());
        model.addAttribute("totalAppointments", appointmentRepo.count());
        model.addAttribute("totalDoctors", userRepo.countByRole(Role.DOCTOR));
        model.addAttribute("recentAppointments", appointmentRepo.findTop10ByOrderByStartTimeDesc());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userRepo.findAll());
        return "admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {

        userRepo.deleteById(id);

        ra.addFlashAttribute("msg", "User deleted successfully!");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String changeRole(@PathVariable Long id, @RequestParam Role role, RedirectAttributes ra) {
        User user = userRepo.findById(id).orElseThrow();
        user.setRole(role);
        userRepo.save(user);
        ra.addFlashAttribute("msg", "Role changed to " + role + " successfully!");
        return "redirect:/admin/users";
    }

    @GetMapping("/appointments")
    public String allAppointments(Model model) {
        model.addAttribute("appointments", appointmentRepo.findAllByOrderByStartTimeDesc());
        return "admin/appointments";
    }
    
    @PostMapping("/appointments/{id}/delete")
    public String deleteAppointment(@PathVariable Long id,
                                    RedirectAttributes ra) {

        appointmentRepo.deleteById(id);

        ra.addFlashAttribute("msg", "Appointment deleted successfully!");
        return "redirect:/admin/appointments";
    }

}