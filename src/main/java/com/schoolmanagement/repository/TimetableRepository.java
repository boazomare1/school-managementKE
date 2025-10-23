package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Timetable;
import com.schoolmanagement.entity.Teacher;
import com.schoolmanagement.entity.ClassEntity;
import com.schoolmanagement.entity.Subject;
import com.schoolmanagement.entity.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    // Find timetables by teacher
    List<Timetable> findByTeacherAndIsActiveTrue(Teacher teacher);
    
    List<Timetable> findByTeacherAndDayOfWeekAndIsActiveTrue(Teacher teacher, DayOfWeek dayOfWeek);
    
    List<Timetable> findByTeacherAndAcademicYearIdAndIsActiveTrue(Teacher teacher, Long academicYearId);

    // Find timetables by class
    List<Timetable> findByClassEntityAndIsActiveTrue(ClassEntity classEntity);
    
    List<Timetable> findByClassEntityAndDayOfWeekAndIsActiveTrue(ClassEntity classEntity, DayOfWeek dayOfWeek);
    
    List<Timetable> findByClassEntityAndAcademicYearIdAndIsActiveTrue(ClassEntity classEntity, Long academicYearId);

    // Find timetables by stream
    List<Timetable> findByStreamAndIsActiveTrue(Stream stream);
    
    List<Timetable> findByStreamAndDayOfWeekAndIsActiveTrue(Stream stream, DayOfWeek dayOfWeek);

    // Find timetables by subject
    List<Timetable> findBySubjectAndIsActiveTrue(Subject subject);
    
    List<Timetable> findBySubjectAndAcademicYearIdAndIsActiveTrue(Subject subject, Long academicYearId);

    // Find timetables by day and time
    List<Timetable> findByDayOfWeekAndStartTimeAndIsActiveTrue(DayOfWeek dayOfWeek, LocalTime startTime);
    
    List<Timetable> findByDayOfWeekAndStartTimeLessThanEqualAndEndTimeGreaterThanEqualAndIsActiveTrue(
        DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime);

    // Find timetables by room
    List<Timetable> findByRoomAndDayOfWeekAndIsActiveTrue(String room, DayOfWeek dayOfWeek);
    
    List<Timetable> findByRoomAndDayOfWeekAndStartTimeLessThanEqualAndEndTimeGreaterThanEqualAndIsActiveTrue(
        String room, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime);

    // Check for conflicts
    @Query("SELECT t FROM Timetable t WHERE t.teacher = :teacher AND t.dayOfWeek = :dayOfWeek " +
           "AND t.isActive = true AND " +
           "((t.startTime <= :startTime AND t.endTime > :startTime) OR " +
           "(t.startTime < :endTime AND t.endTime >= :endTime) OR " +
           "(t.startTime >= :startTime AND t.endTime <= :endTime))")
    List<Timetable> findConflictingTeacherTimetables(
        @Param("teacher") Teacher teacher,
        @Param("dayOfWeek") DayOfWeek dayOfWeek,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );

    @Query("SELECT t FROM Timetable t WHERE t.classEntity = :classEntity AND t.dayOfWeek = :dayOfWeek " +
           "AND t.isActive = true AND " +
           "((t.startTime <= :startTime AND t.endTime > :startTime) OR " +
           "(t.startTime < :endTime AND t.endTime >= :endTime) OR " +
           "(t.startTime >= :startTime AND t.endTime <= :endTime))")
    List<Timetable> findConflictingClassTimetables(
        @Param("classEntity") ClassEntity classEntity,
        @Param("dayOfWeek") DayOfWeek dayOfWeek,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );

    @Query("SELECT t FROM Timetable t WHERE t.room = :room AND t.dayOfWeek = :dayOfWeek " +
           "AND t.isActive = true AND " +
           "((t.startTime <= :startTime AND t.endTime > :startTime) OR " +
           "(t.startTime < :endTime AND t.endTime >= :endTime) OR " +
           "(t.startTime >= :startTime AND t.endTime <= :endTime))")
    List<Timetable> findConflictingRoomTimetables(
        @Param("room") String room,
        @Param("dayOfWeek") DayOfWeek dayOfWeek,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );

    // Count lessons for workload calculation
    @Query("SELECT COUNT(t) FROM Timetable t WHERE t.teacher = :teacher AND t.isActive = true")
    Long countLessonsByTeacher(@Param("teacher") Teacher teacher);

    @Query("SELECT COUNT(t) FROM Timetable t WHERE t.teacher = :teacher AND t.dayOfWeek = :dayOfWeek AND t.isActive = true")
    Long countLessonsByTeacherAndDay(@Param("teacher") Teacher teacher, @Param("dayOfWeek") DayOfWeek dayOfWeek);

    @Query("SELECT SUM(t.duration) FROM Timetable t WHERE t.teacher = :teacher AND t.isActive = true")
    Integer sumMinutesByTeacher(@Param("teacher") Teacher teacher);

    @Query("SELECT SUM(t.duration) FROM Timetable t WHERE t.teacher = :teacher AND t.dayOfWeek = :dayOfWeek AND t.isActive = true")
    Integer sumMinutesByTeacherAndDay(@Param("teacher") Teacher teacher, @Param("dayOfWeek") DayOfWeek dayOfWeek);

    // Find timetables by academic year and term
    List<Timetable> findByAcademicYearIdAndTermIdAndIsActiveTrue(Long academicYearId, Long termId);
    
    List<Timetable> findByAcademicYearIdAndIsActiveTrue(Long academicYearId);

    // Find timetables by lesson type
    List<Timetable> findByLessonTypeAndIsActiveTrue(Timetable.LessonType lessonType);
    
    List<Timetable> findByTeacherAndLessonTypeAndIsActiveTrue(Teacher teacher, Timetable.LessonType lessonType);

    // Find timetables within time range
    @Query("SELECT t FROM Timetable t WHERE t.dayOfWeek = :dayOfWeek AND t.isActive = true " +
           "AND t.startTime >= :startTime AND t.endTime <= :endTime")
    List<Timetable> findTimetablesWithinTimeRange(
        @Param("dayOfWeek") DayOfWeek dayOfWeek,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );

    // Find upcoming classes for reminders
    @Query("SELECT t FROM Timetable t WHERE t.isActive = true " +
           "AND t.dayOfWeek = :dayOfWeek AND t.startTime >= :startTime " +
           "AND t.startTime <= :endTime")
    List<Timetable> findUpcomingClasses(
        @Param("dayOfWeek") DayOfWeek dayOfWeek,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );

    // Find upcoming classes for a specific time
    @Query("SELECT t FROM Timetable t WHERE t.isActive = true " +
           "AND t.dayOfWeek = :dayOfWeek AND t.startTime = :startTime")
    List<Timetable> findUpcomingClasses(
        @Param("dayOfWeek") DayOfWeek dayOfWeek,
        @Param("startTime") LocalTime startTime
    );
}
