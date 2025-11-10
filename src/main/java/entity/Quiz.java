package entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Entity class...
 *
 */
public final class Quiz {

    // Unique identifier for the quiz
    private final class QuizID

    // The type of quiz (capitals, flags, currencies, languages)
    private QuizType quizType;

    // The list of questions that will appear in the quiz
    private List<Question> questions;

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
        this.quizType = quizType;
        this.questions = questions;
        this.score = 0;
        this.durationPlayed = 0;
        this.currentStreak = 0;
        this.highestStreak = 0;
        this.currentIndex = 0;
        this.startTime = LocalDateTime.now();
    }

    public LocalDateTime getStartTime() {
        return startTime;

    }

    // Retrieves the question needed at a given index
    public Question getCurrentQuestion() {
        if (currentIndex < questions.size())  {
            return questions.get(currentIndex);
        }
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
    }


}
