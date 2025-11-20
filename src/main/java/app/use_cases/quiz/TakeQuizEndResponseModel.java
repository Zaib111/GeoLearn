package app.use_cases.quiz;

public class TakeQuizEndResponseModel {

    private final int score;
    private final int totalQuestions;
    private final int durationSeconds;
    private final int highestStreak;

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

