package app.use_cases.quiz;

/**
 * Input Boundary for the Take Quiz use case.
 */
public interface TakeQuizInputBoundary {
    void startQuiz(TakeQuizStartRequestModel requestModel);
    void submitAnswer(SubmitAnswerRequestModel requestModel);
    void nextQuestion();
    void timeExpired();
}
