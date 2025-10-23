package com.schoolmanagement.repository;

import com.schoolmanagement.entity.DormitoryRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DormitoryRoomRepository extends JpaRepository<DormitoryRoom, Long> {

    List<DormitoryRoom> findByDormitoryIdAndIsActiveTrue(Long dormitoryId);

    List<DormitoryRoom> findByIsAvailableTrueAndIsActiveTrue();

    Optional<DormitoryRoom> findByDormitoryIdAndRoomNumber(Long dormitoryId, String roomNumber);

    List<DormitoryRoom> findByDormitoryIdAndCapacityGreaterThanEqual(Long dormitoryId, Integer capacity);
}

