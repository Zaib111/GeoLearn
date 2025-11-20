package app.use_cases.quiz;

public class AnswerFeedbackResponseModel {
    private final String feedbackMessage;
    private final String correctAnswer;
    private final String explanation;

    private final int score;
    private final int currentStreak;
    private final int highestStreak;

    public AnswerFeedbackResponseModel(String feedbackMessage,
                                       String correctAnswer,
                                       String explanation,
                                       int score,
                                       int currentStreak,
                                       int highestStreak) {
        this.feedbackMessage = feedbackMessage;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.score = score;
        this.currentStreak = currentStreak;
        this.highestStreak = highestStreak;
    }

    public String getFeedbackMessage() { return feedbackMessage; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getExplanation() { return explanation; }

    public int getScore() { return score; }
    public int getCurrentStreak() { return currentStreak; }
    public int getHighestStreak() { return highestStreak; }
}
