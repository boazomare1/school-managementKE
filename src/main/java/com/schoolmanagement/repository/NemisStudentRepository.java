package com.schoolmanagement.repository;

import com.schoolmanagement.entity.NemisStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NemisStudentRepository extends JpaRepository<NemisStudent, Long> {

    Optional<NemisStudent> findByNemisNumber(String nemisNumber);

    Optional<NemisStudent> findByUpiNumber(String upiNumber);

    Optional<NemisStudent> findByStudentId(Long studentId);

    List<NemisStudent> findByCountyAndIsActiveTrue(String county);

    List<NemisStudent> findBySubCountyAndIsActiveTrue(String subCounty);

    List<NemisStudent> findByDisabilityStatusAndIsActiveTrue(NemisStudent.DisabilityStatus disabilityStatus);

    List<NemisStudent> findByOrphanStatusAndIsActiveTrue(NemisStudent.OrphanStatus orphanStatus);

    List<NemisStudent> findByVulnerableStatusAndIsActiveTrue(NemisStudent.VulnerableStatus vulnerableStatus);

    List<NemisStudent> findByIsSpecialNeedsTrueAndIsActiveTrue();

    List<NemisStudent> findByIsOrphanTrueAndIsActiveTrue();

    List<NemisStudent> findByIsVulnerableTrueAndIsActiveTrue();

    List<NemisStudent> findByIsBursaryRecipientTrueAndIsActiveTrue();

    List<NemisStudent> findByIsCapitationRecipientTrueAndIsActiveTrue();

    List<NemisStudent> findByIsNemisSyncedFalseAndIsActiveTrue();

    @Query("SELECT ns FROM NemisStudent ns JOIN StudentEnrollment se ON ns.student.id = se.student.id WHERE se.classEntity.school.id = :schoolId AND ns.isActive = true")
    List<NemisStudent> findBySchoolId(Long schoolId);

    @Query("SELECT COUNT(ns) FROM NemisStudent ns JOIN StudentEnrollment se ON ns.student.id = se.student.id WHERE se.classEntity.school.id = :schoolId AND ns.isActive = true")
    Long countBySchoolId(Long schoolId);

    @Query("SELECT COUNT(ns) FROM NemisStudent ns JOIN StudentEnrollment se ON ns.student.id = se.student.id WHERE se.classEntity.school.id = :schoolId AND ns.isSpecialNeeds = true AND ns.isActive = true")
    Long countSpecialNeedsBySchoolId(Long schoolId);

    @Query("SELECT COUNT(ns) FROM NemisStudent ns JOIN StudentEnrollment se ON ns.student.id = se.student.id WHERE se.classEntity.school.id = :schoolId AND ns.isOrphan = true AND ns.isActive = true")
    Long countOrphansBySchoolId(Long schoolId);

    @Query("SELECT COUNT(ns) FROM NemisStudent ns JOIN StudentEnrollment se ON ns.student.id = se.student.id WHERE se.classEntity.school.id = :schoolId AND ns.isVulnerable = true AND ns.isActive = true")
    Long countVulnerableBySchoolId(Long schoolId);
}
