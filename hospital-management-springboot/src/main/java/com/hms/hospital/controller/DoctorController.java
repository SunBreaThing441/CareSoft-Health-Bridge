package com.hms.hospital.controller;
import com.hms.hospital.entity.Appointment;
import com.hms.hospital.entity.User;
import com.hms.hospital.repository.AppointmentRepository;
import com.hms.hospital.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DoctorController {

    private final UserRepository userRepo;
    private final AppointmentRepository appointmentRepo;

    @GetMapping("/doctor/dashboard")
    public String doctorDashboard(Model model, Principal principal) {

        User doctor = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        List<Appointment> todayAppointments = appointmentRepo
                .findByDoctorIdAndStartDate(doctor.getId(), LocalDate.now());

        //  appointments
        List<Appointment> upcoming = appointmentRepo
                .findByDoctorIdAndStartTimeAfter(doctor.getId(), LocalDateTime.now());

        model.addAttribute("doctor", doctor);
        model.addAttribute("todayAppointments", todayAppointments);
        model.addAttribute("totalToday", todayAppointments.size());
        model.addAttribute("upcoming", upcoming);

        return "doctor/dashboard";
    }
}