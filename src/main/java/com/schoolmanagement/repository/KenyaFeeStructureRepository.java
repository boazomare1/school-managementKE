package com.schoolmanagement.repository;

import com.schoolmanagement.entity.KenyaFeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KenyaFeeStructureRepository extends JpaRepository<KenyaFeeStructure, Long> {

    List<KenyaFeeStructure> findBySchoolIdAndIsActiveTrueOrderByCreatedAtDesc(Long schoolId);

    List<KenyaFeeStructure> findByClassEntityIdAndIsActiveTrueOrderByCreatedAtDesc(Long classId);

    List<KenyaFeeStructure> findByAcademicYearIdAndIsActiveTrueOrderByCreatedAtDesc(Long academicYearId);

    List<KenyaFeeStructure> findByFeeTypeAndIsActiveTrue(KenyaFeeStructure.FeeType feeType);

    List<KenyaFeeStructure> findByIsCapitationEligibleTrueAndIsActiveTrue();

    List<KenyaFeeStructure> findByIsBursaryEligibleTrueAndIsActiveTrue();

    List<KenyaFeeStructure> findByIsMandatoryTrueAndIsActiveTrue();

    @Query("SELECT kfs FROM KenyaFeeStructure kfs WHERE kfs.school.id = :schoolId AND kfs.classEntity.id = :classId AND kfs.academicYear.id = :academicYearId AND kfs.isActive = true ORDER BY kfs.createdAt DESC")
    List<KenyaFeeStructure> findBySchoolAndClassAndAcademicYear(Long schoolId, Long classId, Long academicYearId);

    @Query("SELECT kfs FROM KenyaFeeStructure kfs WHERE kfs.school.id = :schoolId AND kfs.feeType = :feeType AND kfs.isActive = true ORDER BY kfs.createdAt DESC")
    List<KenyaFeeStructure> findBySchoolAndFeeType(Long schoolId, KenyaFeeStructure.FeeType feeType);

    @Query("SELECT kfs FROM KenyaFeeStructure kfs WHERE kfs.school.id = :schoolId AND kfs.isCapitationEligible = true AND kfs.isActive = true ORDER BY kfs.createdAt DESC")
    List<KenyaFeeStructure> findCapitationEligibleBySchool(Long schoolId);

    @Query("SELECT kfs FROM KenyaFeeStructure kfs WHERE kfs.school.id = :schoolId AND kfs.isBursaryEligible = true AND kfs.isActive = true ORDER BY kfs.createdAt DESC")
    List<KenyaFeeStructure> findBursaryEligibleBySchool(Long schoolId);

    @Query("SELECT SUM(kfs.amount) FROM KenyaFeeStructure kfs WHERE kfs.school.id = :schoolId AND kfs.isActive = true")
    Double getTotalFeeAmountBySchool(Long schoolId);

    @Query("SELECT SUM(kfs.capitationAmount) FROM KenyaFeeStructure kfs WHERE kfs.school.id = :schoolId AND kfs.isActive = true")
    Double getTotalCapitationAmountBySchool(Long schoolId);

    @Query("SELECT SUM(kfs.parentContribution) FROM KenyaFeeStructure kfs WHERE kfs.school.id = :schoolId AND kfs.isActive = true")
    Double getTotalParentContributionBySchool(Long schoolId);
}

