package com.schoolmanagement.repository;

import com.schoolmanagement.entity.TeacherWorkload;
import com.schoolmanagement.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherWorkloadRepository extends JpaRepository<TeacherWorkload, Long> {

    Optional<TeacherWorkload> findByTeacherAndAcademicYearIdAndTermId(Teacher teacher, Long academicYearId, Long termId);
    
    List<TeacherWorkload> findByTeacherAndAcademicYearId(Teacher teacher, Long academicYearId);
    
    List<TeacherWorkload> findByAcademicYearIdAndTermId(Long academicYearId, Long termId);
    
    List<TeacherWorkload> findByAcademicYearId(Long academicYearId);
    
    List<TeacherWorkload> findByIsOverloadedTrue();
    
    @Query("SELECT tw FROM TeacherWorkload tw WHERE tw.teacher = :teacher AND tw.academicYearId = :academicYearId AND tw.termId = :termId")
    Optional<TeacherWorkload> findCurrentWorkload(@Param("teacher") Teacher teacher, @Param("academicYearId") Long academicYearId, @Param("termId") Long termId);
    
    @Query("SELECT tw FROM TeacherWorkload tw WHERE tw.totalLessonsPerWeek >= tw.maxLessonsPerWeek OR tw.totalMinutesPerWeek >= tw.maxMinutesPerWeek")
    List<TeacherWorkload> findOverloadedTeachers();
    
    @Query("SELECT tw FROM TeacherWorkload tw WHERE tw.totalLessonsPerWeek < tw.maxLessonsPerWeek AND tw.totalMinutesPerWeek < tw.maxMinutesPerWeek")
    List<TeacherWorkload> findUnderloadedTeachers();
}

