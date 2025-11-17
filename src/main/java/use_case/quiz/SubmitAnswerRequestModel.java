package use_case.quiz;

public class SubmitAnswerRequestModel {
    private final String userAnswer;

    public SubmitAnswerRequestModel(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getUserAnswer() {
        return userAnswer;
    }
}
