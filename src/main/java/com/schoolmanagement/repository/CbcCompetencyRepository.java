package com.schoolmanagement.repository;

import com.schoolmanagement.entity.CbcCompetency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CbcCompetencyRepository extends JpaRepository<CbcCompetency, Long> {

    List<CbcCompetency> findByGradeLevelAndIsActiveTrue(CbcCompetency.GradeLevel gradeLevel);

    List<CbcCompetency> findByLearningAreaAndIsActiveTrue(CbcCompetency.LearningArea learningArea);

    List<CbcCompetency> findByStrandAndIsActiveTrue(CbcCompetency.Strand strand);

    List<CbcCompetency> findBySubStrandAndIsActiveTrue(CbcCompetency.SubStrand subStrand);

    List<CbcCompetency> findByIsCoreCompetencyTrueAndIsActiveTrue();

    @Query("SELECT c FROM CbcCompetency c WHERE c.gradeLevel = :gradeLevel AND c.learningArea = :learningArea AND c.isActive = true ORDER BY c.sequence")
    List<CbcCompetency> findByGradeLevelAndLearningArea(CbcCompetency.GradeLevel gradeLevel, CbcCompetency.LearningArea learningArea);

    @Query("SELECT c FROM CbcCompetency c WHERE c.gradeLevel = :gradeLevel AND c.learningArea = :learningArea AND c.strand = :strand AND c.isActive = true ORDER BY c.sequence")
    List<CbcCompetency> findByGradeLevelAndLearningAreaAndStrand(CbcCompetency.GradeLevel gradeLevel, CbcCompetency.LearningArea learningArea, CbcCompetency.Strand strand);

    @Query("SELECT DISTINCT c.gradeLevel FROM CbcCompetency c WHERE c.isActive = true ORDER BY c.gradeLevel")
    List<CbcCompetency.GradeLevel> findDistinctGradeLevels();

    @Query("SELECT DISTINCT c.learningArea FROM CbcCompetency c WHERE c.isActive = true ORDER BY c.learningArea")
    List<CbcCompetency.LearningArea> findDistinctLearningAreas();

    @Query("SELECT DISTINCT c.strand FROM CbcCompetency c WHERE c.gradeLevel = :gradeLevel AND c.learningArea = :learningArea AND c.isActive = true ORDER BY c.strand")
    List<CbcCompetency.Strand> findDistinctStrandsByGradeAndLearningArea(CbcCompetency.GradeLevel gradeLevel, CbcCompetency.LearningArea learningArea);
}

