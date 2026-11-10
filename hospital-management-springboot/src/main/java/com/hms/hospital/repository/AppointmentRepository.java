package com.hms.hospital.repository;

import com.hms.hospital.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    long countByPatientIdAndStartTimeAfter(Long patientId, LocalDateTime now);
    long countByPatientIdAndStartTimeBefore(Long patientId, LocalDateTime now);

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findAllByOrderByStartTimeDesc();
    List<Appointment> findTop10ByOrderByStartTimeDesc();

    List<Appointment> findByDoctorIdAndStartTimeAfter(Long doctorId, LocalDateTime now);

    List<Appointment> findByPatientIdAndStartTimeAfter(Long patientId, LocalDateTime now);

    List<Appointment> findByDoctorIdAndStartTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end);

    default List<Appointment> findByDoctorIdAndStartDate(Long doctorId, LocalDate today) {
        return findByDoctorIdAndStartTimeBetween(doctorId, today.atStartOfDay(), today.atTime(23, 59, 59, 999999999));
    }

    List<Appointment> findByDoctorIdOrderByStartTimeAsc(Long doctorId);
    List<Appointment> findByPatientIdOrderByStartTimeAsc(Long patientId);
	List<Appointment> findByPatientUserIdOrderByStartTimeAsc(Long userId);
}