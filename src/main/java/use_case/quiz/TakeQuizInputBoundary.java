package use_case.quiz;

public interface TakeQuizInputBoundary {
    void startQuiz(TakeQuizStartRequestModel requestModel);
    void submitAnswer(SubmitAnswerRequestModel requestModel);
    void nextQuestion();
}
