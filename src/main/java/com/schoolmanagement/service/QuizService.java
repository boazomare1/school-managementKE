package com.schoolmanagement.service;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.entity.*;
import com.schoolmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizOptionRepository quizOptionRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final UserRepository userRepository;

    @Transactional
    public ApiResponse<QuizSubmission> submitQuiz(Long quizId, Map<Long, String> answers, User student) {
        try {
            log.info("Submitting quiz: {} by student: {}", quizId, student.getUsername());

            // Get quiz
            Optional<Quiz> quizOpt = quizRepository.findById(quizId);
            if (quizOpt.isEmpty()) {
                return ApiResponse.error("Quiz not found");
            }

            Quiz quiz = quizOpt.get();

            // Check if quiz is still open
            if (LocalDateTime.now().isAfter(quiz.getEndDate())) {
                return ApiResponse.error("Quiz has closed");
            }

            // Create or get existing submission
            QuizSubmission submission = quizSubmissionRepository
                .findByQuizIdAndStudentIdAndIsActiveTrue(quizId, student.getId())
                .orElse(QuizSubmission.builder()
                    .quiz(quiz)
                    .student(student)
                    .startedAt(LocalDateTime.now())
                    .submittedAt(LocalDateTime.now())
                    .status(QuizSubmission.SubmissionStatus.SUBMITTED)
                    .isLate(LocalDateTime.now().isAfter(quiz.getEndDate()))
                    .attemptNumber(1)
                    .isAutoGraded(quiz.getAutoGrade())
                    .isActive(true)
                    .build());

            submission.setSubmittedAt(LocalDateTime.now());
            submission.setStatus(QuizSubmission.SubmissionStatus.SUBMITTED);

            // Save submission
            QuizSubmission savedSubmission = quizSubmissionRepository.save(submission);

            // Process answers
            BigDecimal totalScore = BigDecimal.ZERO;
            int totalQuestions = 0;

            for (Map.Entry<Long, String> entry : answers.entrySet()) {
                Long questionId = entry.getKey();
                String answerText = entry.getValue();

                Optional<QuizQuestion> questionOpt = quizQuestionRepository.findById(questionId);
                if (questionOpt.isEmpty()) continue;

                QuizQuestion question = questionOpt.get();
                totalQuestions++;

                // Create answer
                QuizAnswer answer = QuizAnswer.builder()
                    .submission(savedSubmission)
                    .question(question)
                    .answerText(answerText)
                    .isActive(true)
                    .build();

                // Auto-grade if enabled
                if (quiz.getAutoGrade() && question.getQuestionType() != QuizQuestion.QuestionType.ESSAY) {
                    BigDecimal pointsAwarded = autoGradeAnswer(question, answerText);
                    answer.setPointsAwarded(pointsAwarded);
                    answer.setIsCorrect(pointsAwarded.compareTo(question.getPoints()) == 0);
                    answer.setIsAutoGraded(true);
                    totalScore = totalScore.add(pointsAwarded);
                } else {
                    answer.setPointsAwarded(BigDecimal.ZERO);
                    answer.setIsCorrect(false);
                    answer.setIsAutoGraded(false);
                }

                quizAnswerRepository.save(answer);
            }

            // Calculate final score
            if (quiz.getAutoGrade()) {
                savedSubmission.setScore(totalScore);
                savedSubmission.setPercentage(totalScore.divide(quiz.getTotalMarks(), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)));
                savedSubmission.setGrade(calculateGrade(savedSubmission.getPercentage()));
                savedSubmission.setIsAutoGraded(true);
                savedSubmission.setStatus(QuizSubmission.SubmissionStatus.GRADED);
                savedSubmission.setGradedAt(LocalDateTime.now());
            }

            quizSubmissionRepository.save(savedSubmission);

            log.info("Quiz submitted successfully: {}", savedSubmission.getId());
            return ApiResponse.success("Quiz submitted successfully", savedSubmission);

        } catch (Exception e) {
            log.error("Error submitting quiz: {}", e.getMessage());
            return ApiResponse.error("Failed to submit quiz: " + e.getMessage());
        }
    }

    private BigDecimal autoGradeAnswer(QuizQuestion question, String answerText) {
        try {
            switch (question.getQuestionType()) {
                case MULTIPLE_CHOICE:
                    return gradeMultipleChoice(question, answerText);
                case TRUE_FALSE:
                    return gradeTrueFalse(question, answerText);
                case SHORT_ANSWER:
                    return gradeShortAnswer(question, answerText);
                default:
                    return BigDecimal.ZERO;
            }
        } catch (Exception e) {
            log.error("Error auto-grading answer: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal gradeMultipleChoice(QuizQuestion question, String answerText) {
        // Get correct options
        List<QuizOption> correctOptions = question.getOptions().stream()
            .filter(QuizOption::getIsCorrect)
            .collect(Collectors.toList());

        if (correctOptions.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Check if answer matches any correct option
        for (QuizOption option : correctOptions) {
            if (option.getOptionLetter().equalsIgnoreCase(answerText.trim()) ||
                option.getOptionText().equalsIgnoreCase(answerText.trim())) {
                return question.getPoints();
            }
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal gradeTrueFalse(QuizQuestion question, String answerText) {
        String correctAnswer = question.getCorrectAnswer();
        if (correctAnswer == null) {
            return BigDecimal.ZERO;
        }

        boolean isCorrect = correctAnswer.equalsIgnoreCase(answerText.trim());
        return isCorrect ? question.getPoints() : BigDecimal.ZERO;
    }

    private BigDecimal gradeShortAnswer(QuizQuestion question, String answerText) {
        String correctAnswer = question.getCorrectAnswer();
        if (correctAnswer == null) {
            return BigDecimal.ZERO;
        }

        // Simple text matching (can be enhanced with fuzzy matching)
        boolean isCorrect = correctAnswer.toLowerCase().trim()
            .equals(answerText.toLowerCase().trim());

        return isCorrect ? question.getPoints() : BigDecimal.ZERO;
    }

    private String calculateGrade(BigDecimal percentage) {
        if (percentage.compareTo(BigDecimal.valueOf(90)) >= 0) {
            return "A";
        } else if (percentage.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return "B";
        } else if (percentage.compareTo(BigDecimal.valueOf(70)) >= 0) {
            return "C";
        } else if (percentage.compareTo(BigDecimal.valueOf(60)) >= 0) {
            return "D";
        } else {
            return "F";
        }
    }

    @Transactional
    public ApiResponse<QuizSubmission> gradeQuiz(Long submissionId, User teacher) {
        try {
            log.info("Grading quiz submission: {} by teacher: {}", submissionId, teacher.getUsername());

            Optional<QuizSubmission> submissionOpt = quizSubmissionRepository.findById(submissionId);
            if (submissionOpt.isEmpty()) {
                return ApiResponse.error("Submission not found");
            }

            QuizSubmission submission = submissionOpt.get();

            // Check if teacher has permission to grade this quiz
            if (!submission.getQuiz().getTeacher().getId().equals(teacher.getId())) {
                return ApiResponse.error("Not authorized to grade this quiz");
            }

            // Manual grading for essay questions
            List<QuizAnswer> answers = quizAnswerRepository.findBySubmissionIdAndIsActiveTrue(submissionId);
            BigDecimal totalScore = BigDecimal.ZERO;

            for (QuizAnswer answer : answers) {
                if (!answer.getIsAutoGraded()) {
                    // Manual grading logic here
                    // For now, set to zero - teacher will need to grade manually
                    answer.setPointsAwarded(BigDecimal.ZERO);
                    answer.setIsCorrect(false);
                    answer.setFeedback("Requires manual grading");
                    quizAnswerRepository.save(answer);
                }
                totalScore = totalScore.add(answer.getPointsAwarded());
            }

            // Update submission
            submission.setScore(totalScore);
            submission.setPercentage(totalScore.divide(submission.getQuiz().getTotalMarks(), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)));
            submission.setGrade(calculateGrade(submission.getPercentage()));
            submission.setGradedBy(teacher);
            submission.setGradedAt(LocalDateTime.now());
            submission.setStatus(QuizSubmission.SubmissionStatus.GRADED);

            QuizSubmission savedSubmission = quizSubmissionRepository.save(submission);

            log.info("Quiz graded successfully: {}", savedSubmission.getId());
            return ApiResponse.success("Quiz graded successfully", savedSubmission);

        } catch (Exception e) {
            log.error("Error grading quiz: {}", e.getMessage());
            return ApiResponse.error("Failed to grade quiz: " + e.getMessage());
        }
    }

    public ApiResponse<List<QuizSubmission>> getStudentQuizResults(Long studentId, User user) {
        try {
            log.info("Getting quiz results for student: {} by user: {}", studentId, user.getUsername());

            List<QuizSubmission> submissions = quizSubmissionRepository
                .findByStudentIdAndIsActiveTrueOrderBySubmittedAtDesc(studentId);

            return ApiResponse.success("Quiz results retrieved successfully", submissions);

        } catch (Exception e) {
            log.error("Error getting quiz results: {}", e.getMessage());
            return ApiResponse.error("Failed to get quiz results: " + e.getMessage());
        }
    }
}

