package app.use_cases.quiz;

public interface TakeQuizInputBoundary {
    void startQuiz(TakeQuizStartRequestModel requestModel);
    void submitAnswer(SubmitAnswerRequestModel requestModel);
    void nextQuestion();
    void timeExpired();
}
