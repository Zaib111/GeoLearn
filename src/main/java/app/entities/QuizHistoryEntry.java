package app.entities;

import java.time.LocalDateTime;

/**
 * Represents a single completed quiz attempt for history tracking.
 */
public class QuizHistoryEntry {

    private final QuizType quizType;
    private final QuestionType questionType;
    private final int numQuestions;
    private final int score;
    private final int durationSeconds;
    private final int highestStreak;
    private final LocalDateTime completedAt;

    public QuizHistoryEntry(QuizType quizType,
                            QuestionType questionType,
                            int numQuestions,
                            int score,
                            int durationSeconds,
                            int highestStreak,
                            LocalDateTime completedAt) {
        this.quizType = quizType;
        this.questionType = questionType;
        this.numQuestions = numQuestions;
        this.score = score;
        this.durationSeconds = durationSeconds;
        this.highestStreak = highestStreak;
        this.completedAt = completedAt;
    }

    public QuizType getQuizType() {
        return quizType;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public int getNumQuestions() {
        return numQuestions;
    }

    public int getScore() {
        return score;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public int getHighestStreak() {
        return highestStreak;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}
