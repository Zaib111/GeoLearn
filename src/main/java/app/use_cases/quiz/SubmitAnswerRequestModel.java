package app.use_cases.quiz;

/**
 * Request Model for the “Submit Answer” use case.
 */
public class SubmitAnswerRequestModel {
    private final String userAnswer;

    public SubmitAnswerRequestModel(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getUserAnswer() {
        return userAnswer;
    }
}
