package com.schoolmanagement.repository;

import com.schoolmanagement.entity.DormitoryReservation;
import com.schoolmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DormitoryReservationRepository extends JpaRepository<DormitoryReservation, Long> {

    List<DormitoryReservation> findByStudent(User student);

    List<DormitoryReservation> findByStudentId(Long studentId);

    Optional<DormitoryReservation> findByStudentAndIsActiveTrue(User student);

    List<DormitoryReservation> findByRoomId(Long roomId);

    List<DormitoryReservation> findByIsActiveTrue();
}
