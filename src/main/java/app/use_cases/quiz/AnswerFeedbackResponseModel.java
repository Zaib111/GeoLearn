package app.use_cases.quiz;

/**
 * Response model representing the feedback returned after the user submits answer
 * during a quiz. This object is created by the Interactor and passed to the Presenter,
 * which then updates the ViewModel or View.
 */
public class AnswerFeedbackResponseModel {
    private final String feedbackMessage;
    private final String correctAnswer;
    private final String explanation;

    private final int score;
    private final int currentStreak;
    private final int highestStreak;

    /**
     * Creates a response model containing feedback and scoring information after an answer is processed.
     *
     * @param feedbackMessage the textual feedback shown to the user
     * @param correctAnswer   the actual correct answer to the current question
     * @param explanation     a short educational explanation related to the question
     * @param score           the user's updated score after answering
     * @param currentStreak   the user's current number of consecutive correct answers
     * @param highestStreak   the highest number of consecutive correct answers achieved so far
     */
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
