package app.use_cases.quiz;

/**
 * Output boundary for the Take Quiz use case.
 */
public interface TakeQuizOutputBoundary {
    void prepareQuizStart(TakeQuizStartResponseModel responseModel);
    void presentQuestion(TakeQuizQuestionResponseModel responseModel);
    void presentAnswerFeedback(AnswerFeedbackResponseModel responseModel);
    void presentQuizEnd(TakeQuizEndResponseModel responseModel);
}
