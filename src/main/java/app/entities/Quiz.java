package app.entities;

import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Entity class...
 *
 */
@Getter
public final class Quiz {

    // Unique identifier for the quiz
    private final UUID quizID;

    // The type of quiz (capitals, flags, currencies, languages)
    private final QuizType quizType;

    // The list of questions that will appear in the quiz
    private final List<Question> questions;

    // The total number of questions correct in the quiz
    private int score;

    // The length of time since the quiz started
    private int durationPlayed;

    // The current longest amount of questions correct in a row and the highest count of that
    private int currentStreak;
    private int highestStreak;

    // The current question number*/
    private int currentIndex;

    // The starting time and ending time for the quiz
    private final LocalDateTime startTime;
    private LocalDateTime endTime;

    // Initialization of the quiz with its type and question list specified
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

    // Retrieves the question needed at a given index
    public Question getCurrentQuestion() {
        if (currentIndex < questions.size())  {
            return questions.get(currentIndex);
        }
        return null;
    }

    public void nextQuestion() {
        if (currentIndex < questions.size()) {
            currentIndex++;
        }
    }

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

    public void finish() {
        this.endTime = LocalDateTime.now();
        this.durationPlayed = (int) Duration.between(startTime, endTime).toSeconds();
    }

    // Custom getter for unmodifiable list
    public List<Question> getQuestions() {
        return Collections.unmodifiableList(questions);
    }

    public int getTotalQuestions() {
        return questions.size();
    }

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