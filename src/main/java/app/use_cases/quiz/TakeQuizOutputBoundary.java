package app.use_cases.quiz;

/**
 * Output boundary for the Take Quiz use case.
 */
public interface TakeQuizOutputBoundary {
    /**
     * Prepares the response model for the quiz start, initializing the quiz session
     * with the necessary metadata and first question details.
     *
     * @param responseModel the response model containing details for the quiz start,
     *                      including the quiz title, first question prompt, options,
     *                      question index, total questions, and media URL if applicable
     */
    void prepareQuizStart(TakeQuizStartResponseModel responseModel);

    /**
     * Presents the current quiz question to the user.
     *
     * @param responseModel the response model containing the question prompt,
     *                      possible answer options, question index, total number of
     *                      questions in the quiz, and an optional media URL for
     *                      supplementary visual content
     */
    void presentQuestion(TakeQuizQuestionResponseModel responseModel);

    /**
     * Presents feedback to the user after submitting an answer in the quiz.
     *
     * @param responseModel a response model containing feedback details such as
     *                      the feedback message, correct answer, explanation,
     *                      updated score, current streak, and highest streak
     */
    void presentAnswerFeedback(AnswerFeedbackResponseModel responseModel);

    /**
     * Displays the final results of the quiz to the user.
     *
     * @param responseModel the response model containing quiz completion details,
     *                      such as the total score, number of questions, quiz duration
     *                      in seconds, and the highest streak achieved during the quiz
     */
    void presentQuizEnd(TakeQuizEndResponseModel responseModel);

    /**
     * Presents the user's quiz history in a view-friendly format.
     *
     * @param responseModel the response model containing a list of quiz history entries,
     *                      where each entry includes details such as quiz type, question type,
     *                      total questions, score, duration, highest streak, and the timestamp
     *                      of when the quiz was completed
     */
    void presentQuizHistory(QuizHistoryResponseModel responseModel);
}
