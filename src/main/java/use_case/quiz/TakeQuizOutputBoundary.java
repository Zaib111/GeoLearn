package use_case.quiz;

public interface TakeQuizOutputBoundary {
    void prepareQuizStart(TakeQuizStartResponseModel responseModel);
    void presentQuestion(TakeQuizQuestionResponseModel responseModel);
    void presentAnswerFeedback(AnswerFeedbackResponseModel responseModel);
    void presentQuizEnd(TakeQuizEndResponseModel responseModel);
}
