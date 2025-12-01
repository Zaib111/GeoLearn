package app.controllers;

import app.entities.QuestionType;
import app.entities.QuizType;
import app.use_cases.quiz.SubmitAnswerRequestModel;
import app.use_cases.quiz.TakeQuizInputBoundary;
import app.use_cases.quiz.TakeQuizStartRequestModel;

/**
 * Controller for the quiz feature. Converts UI actions into
 * input models and forwards them to the TakeQuizInteractor.
 */
public class TakeQuizController {

    private final TakeQuizInputBoundary interactor;

    /**
     * Creates a controller that forwards requests to the quiz interactor.
     *
     * @param interactor the take-quiz input boundary (interactor) to forward requests to
     */
    public TakeQuizController(TakeQuizInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Starts a new quiz with the selected type, mode, and number of questions.
     *
     * @param quizType          the type of quiz to start
     * @param questionType      the question type to include in the quiz
     * @param numberOfQuestions the number of questions to generate for the quiz
     */
    public void startQuiz(QuizType quizType,
                          QuestionType questionType,
                          int numberOfQuestions) {
        final TakeQuizStartRequestModel request = new TakeQuizStartRequestModel(
                quizType,
                questionType,
                numberOfQuestions
        );
        interactor.startQuiz(request);
    }

    /**
     * Submits the user's answer to the current question.
     *
     * @param userAnswer the user's answer text for the current question
     */
    public void submitAnswer(String userAnswer) {
        final SubmitAnswerRequestModel request = new SubmitAnswerRequestModel(userAnswer);
        interactor.submitAnswer(request);
    }

    /**
     * Requests the next question in the quiz.
     */
    public void nextQuestion() {
        interactor.nextQuestion();
    }

    /**
     * Signals that the timer for the current question has expired.
     */
    public void timeExpired() {
        interactor.timeExpired();
    }

    /**
     * Requests the quiz history from the database.
     */
    public void loadQuizHistory() {
        interactor.loadQuizHistory();
    }
}
