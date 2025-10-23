package com.schoolmanagement.repository;

import com.schoolmanagement.entity.StudentCompetency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentCompetencyRepository extends JpaRepository<StudentCompetency, Long> {

    List<StudentCompetency> findByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(Long studentId);

    List<StudentCompetency> findByCompetencyIdAndIsActiveTrueOrderByCreatedAtDesc(Long competencyId);

    List<StudentCompetency> findByTeacherIdAndIsActiveTrueOrderByCreatedAtDesc(Long teacherId);

    List<StudentCompetency> findByAcademicYearIdAndIsActiveTrueOrderByCreatedAtDesc(Long academicYearId);

    List<StudentCompetency> findByTermIdAndIsActiveTrueOrderByCreatedAtDesc(Long termId);

    List<StudentCompetency> findByLevelAndIsActiveTrue(StudentCompetency.CompetencyLevel level);

    List<StudentCompetency> findByAssessmentTypeAndIsActiveTrue(StudentCompetency.AssessmentType assessmentType);

    @Query("SELECT sc FROM StudentCompetency sc WHERE sc.student.id = :studentId AND sc.competency.gradeLevel = :gradeLevel AND sc.isActive = true ORDER BY sc.createdAt DESC")
    List<StudentCompetency> findByStudentAndGradeLevel(Long studentId, String gradeLevel);

    @Query("SELECT sc FROM StudentCompetency sc WHERE sc.student.id = :studentId AND sc.competency.learningArea = :learningArea AND sc.isActive = true ORDER BY sc.createdAt DESC")
    List<StudentCompetency> findByStudentAndLearningArea(Long studentId, String learningArea);

    @Query("SELECT sc FROM StudentCompetency sc WHERE sc.student.id = :studentId AND sc.competency.strand = :strand AND sc.isActive = true ORDER BY sc.createdAt DESC")
    List<StudentCompetency> findByStudentAndStrand(Long studentId, String strand);

    @Query("SELECT sc FROM StudentCompetency sc WHERE sc.student.id = :studentId AND sc.competency.id = :competencyId AND sc.isActive = true ORDER BY sc.createdAt DESC")
    List<StudentCompetency> findByStudentAndCompetency(Long studentId, Long competencyId);

    @Query("SELECT sc FROM StudentCompetency sc JOIN StudentEnrollment se ON sc.student.id = se.student.id WHERE se.classEntity.school.id = :schoolId AND sc.isActive = true ORDER BY sc.createdAt DESC")
    List<StudentCompetency> findBySchoolId(Long schoolId);

    @Query("SELECT COUNT(sc) FROM StudentCompetency sc JOIN StudentEnrollment se ON sc.student.id = se.student.id WHERE se.classEntity.school.id = :schoolId AND sc.isActive = true")
    Long countBySchoolId(Long schoolId);

    @Query("SELECT sc.level, COUNT(sc) FROM StudentCompetency sc JOIN StudentEnrollment se ON sc.student.id = se.student.id WHERE se.classEntity.school.id = :schoolId AND sc.isActive = true GROUP BY sc.level")
    List<Object[]> getCompetencyLevelDistributionBySchool(Long schoolId);

    @Query("SELECT sc.competency.learningArea, AVG(CASE WHEN sc.level = 'EXCEEDS' THEN 4.0 WHEN sc.level = 'MEETS' THEN 3.0 WHEN sc.level = 'APPROACHING' THEN 2.0 ELSE 1.0 END) FROM StudentCompetency sc JOIN StudentEnrollment se ON sc.student.id = se.student.id WHERE se.classEntity.school.id = :schoolId AND sc.isActive = true GROUP BY sc.competency.learningArea")
    List<Object[]> getAverageCompetencyByLearningAreaAndSchool(Long schoolId);
}
