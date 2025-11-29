package app.use_cases.quiz;

/**
 * Response Model for the “Quiz End” use case.
 */
public class TakeQuizEndResponseModel {

    private final int score;
    private final int totalQuestions;
    private final int durationSeconds;
    private final int highestStreak;

    /**
     * Constructs a response model summarizing the outcome of a completed quiz.
     *
     * @param score            the number of correct answers
     * @param totalQuestions   how many questions were in the quiz
     * @param durationSeconds  total time the user spent
     * @param highestStreak    the longest chain of consecutive correct answers
     */
    public TakeQuizEndResponseModel(int score,
                                int totalQuestions,
                                int durationSeconds,
                                int highestStreak) {
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.durationSeconds = durationSeconds;
        this.highestStreak = highestStreak;
    }

    public int getScore() { return score; }
    public int getTotalQuestions() { return totalQuestions; }
    public int getDurationSeconds() { return durationSeconds; }
    public int getHighestStreak() { return highestStreak; }
}

