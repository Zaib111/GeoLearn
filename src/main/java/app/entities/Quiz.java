package app.entities;

import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a single quiz session for a user.
 * Stores the quiz type, list of questions, progress, score, streaks,
 * and timing information. The Quiz entity contains the core logic
 * for answering questions and tracking overall quiz status.
 */
@Getter
public final class Quiz {

    /** Unique identifier for this quiz session. */
    private final UUID quizID;

    /** The category of this quiz (e.g., capitals, flags, languages). */
    private final QuizType quizType;

    /** The list of questions in this quiz. */
    private final List<Question> questions;

    /** Number of correct answers so far. */
    private int score;

    /** Total duration played, computed when quiz ends (in seconds). */
    private int durationPlayed;

    /** Current consecutive correct-answer streak. */
    private int currentStreak;

    /** Highest streak reached at any point in the quiz. */
    private int highestStreak;

    /** Index of the current question. */
    private int currentIndex;

    /** Start and end timestamps for computing quiz duration. */
    private final LocalDateTime startTime;
    private LocalDateTime endTime;

    /**
     * Creates a new quiz with a given type and set of questions.
     *
     * @param quizType  the category of quiz
     * @param questions the list of questions to be asked
     */
    public Quiz(QuizType quizType, List<Question> questions) {
        this.quizID = UUID.randomUUID();
        this.quizType = Objects.requireNonNull(quizType);
        this.questions = new ArrayList<>(Objects.requireNonNull(questions));
        this.score = 0;
        this.durationPlayed = 0;
        this.currentStreak = 0;
        this.highestStreak = 0;
        this.currentIndex = 0;
        this.startTime = LocalDateTime.now();
    }

    /**
     * @return the currently active question, or null if past the end.
     */
    public Question getCurrentQuestion() {
        if (currentIndex < questions.size())  {
            return questions.get(currentIndex);
        }
        return null;
    }

    /** Moves to the next question, if available. */
    public void nextQuestion() {
        if (currentIndex < questions.size()) {
            currentIndex++;
        }
    }

    /**
     * Checks the user’s answer for the current question and updates score/streak.
     *
     * @param userAnswer the answer provided by the user
     * @return true if correct, false otherwise
     */
    public boolean answerCurrentQuestion(String userAnswer) {
        Question q = getCurrentQuestion();
        if (q == null) {
            return false;
        }

        boolean answerCorrectness = AnswerChecker.isCorrect(userAnswer, q);
        if (answerCorrectness) {
            score++;
            currentStreak++;
            highestStreak = Math.max(highestStreak, currentStreak);
        } else {
            currentStreak = 0;
        }
        return answerCorrectness;
    }

    /**
     * Marks the quiz as finished and records the total duration.
     */
    public void finish() {
        this.endTime = LocalDateTime.now();
        this.durationPlayed = (int) Duration.between(startTime, endTime).toSeconds();
    }

    /**
     * @return an unmodifiable view of the question list.
     */
    public List<Question> getQuestions() {
        return Collections.unmodifiableList(questions);
    }

    /** @return total number of questions in this quiz. */
    public int getTotalQuestions() {
        return questions.size();
    }

    /** @return true if all questions have been answered. */
    public boolean isFinished() {
        return currentIndex >= questions.size();
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + quizID +
                ", type=" + quizType +
                ", score=" + score + "/" + questions.size() +
                ", longestStreak=" + highestStreak +
                ", duration =" + durationPlayed + "sec" +
                "}";
    }
}