package app.use_cases.quiz;

/**
 * Input Boundary for the Take Quiz use case.
 */
public interface TakeQuizInputBoundary {
    /**
     * Starts a quiz session based on the parameters provided in the request model.
     *
     * @param requestModel the model*/
    void startQuiz(TakeQuizStartRequestModel requestModel);

    /**
     * Submits the user's answer for the current question in the quiz session.
     *
     * @param requestModel the request model containing the user's answer to the current question
     */
    void submitAnswer(SubmitAnswerRequestModel requestModel);

    /**
     * Advances the quiz to the next question or concludes the quiz if there are no remaining questions.
     *
     * If there are additional questions:
     * - Updates the current quiz state to reflect the next question.
     * - Prepares and presents a response model with the details of the next question,
     *   including its prompt, shuffled options (if applicable), question index, total questions,
     *   and*/
    void nextQuestion();

    /**
     * Handles the expiration of the countdown timer during the quiz session.
     * When the timer runs out, the current question is automatically marked as
     * unanswered. The quiz progresses as though the user did not provide an answer
     * for the question, and feedback is presented to the user indicating that
     * time has expired.
     * This method ensures the quiz session continues to adhere to the timed
     * constraints and prevents indefinite waiting for user input.
     */
    void timeExpired();

    /**
     * Loads the quiz history from the data source and presents it to the output boundary.
     * This method retrieves a list of prior quiz attempts, including details such as the quiz
     * type, question type, scores, and other relevant metadata. The retrieved quiz history
     * is then packaged into a response model and passed to the presenter for display.
     */
    void loadQuizHistory();
}
