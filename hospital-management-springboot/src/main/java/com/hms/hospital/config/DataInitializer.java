package com.hms.hospital.config;

import com.hms.hospital.entity.Appointment;
import com.hms.hospital.entity.Patient;
import com.hms.hospital.entity.Role;
import com.hms.hospital.entity.User;
import com.hms.hospital.repository.AppointmentRepository;
import com.hms.hospital.repository.PatientRepository;
import com.hms.hospital.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepo;
    private final PatientRepository patientRepo;
    private final AppointmentRepository appointmentRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Seed Admin user
        if (userRepo.findByEmail("admin@hospital.com").isEmpty()) {
            User admin = new User();
            admin.setName("Admin User");
            admin.setEmail("admin@hospital.com");
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setRole(Role.ADMIN);
            userRepo.save(admin);
        }

        // Seed Doctor user
        User doctor = userRepo.findByEmail("sarah@hospital.com").orElse(null);
        if (doctor == null) {
            doctor = new User();
            doctor.setName("Dr. Sarah Jenkins");
            doctor.setEmail("sarah@hospital.com");
            doctor.setPassword(passwordEncoder.encode("password"));
            doctor.setRole(Role.DOCTOR);
            doctor = userRepo.save(doctor);
        }

        // Seed Patient user and profile
        User patientUser = userRepo.findByEmail("patient@hospital.com").orElse(null);
        Patient patient = null;
        if (patientUser == null) {
            patientUser = new User();
            patientUser.setName("John Doe");
            patientUser.setEmail("patient@hospital.com");
            patientUser.setPassword(passwordEncoder.encode("password"));
            patientUser.setRole(Role.PATIENT);
            patientUser = userRepo.save(patientUser);

            patient = new Patient();
            patient.setPatientId("PAT-0001");
            patient.setName("John Doe");
            patient.setEmail("patient@hospital.com");
            patient.setPhone("+1-555-0199");
            patient.setGender("Male");
            patient.setDob(LocalDate.of(1990, 5, 15));
            patient.setUser(patientUser);
            patient = patientRepo.save(patient);
        } else {
            patient = patientRepo.findByUserEmail("patient@hospital.com").orElse(null);
        }

        // Seed sample appointments if none exist
        if (appointmentRepo.count() == 0 && doctor != null && patient != null) {
            LocalDateTime now = LocalDateTime.now();

            Appointment apt1 = Appointment.builder()
                    .title("General Checkup - John Doe")
                    .startTime(now.plusHours(2))
                    .endTime(now.plusHours(2).plusMinutes(30))
                    .status("SCHEDULED")
                    .doctor(doctor)
                    .patient(patient)
                    .build();
            appointmentRepo.save(apt1);

            Appointment apt2 = Appointment.builder()
                    .title("Routine Review - John Doe")
                    .startTime(now.plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0))
                    .endTime(now.plusDays(1).withHour(10).withMinute(30).withSecond(0).withNano(0))
                    .status("SCHEDULED")
                    .doctor(doctor)
                    .patient(patient)
                    .build();
            appointmentRepo.save(apt2);
        }
    }
}
