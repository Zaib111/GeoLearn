package app.presenters;

import app.use_cases.quiz.*;
import app.views.quiz.QuizView;

/**
 * Presenter for the quiz feature. Converts use-case response models
 * into method calls on the QuizView. The presenter does not help with formatting but instead
 * simply passes values from the interactor to the view.
 */
public class TakeQuizPresenter implements TakeQuizOutputBoundary{
    private final QuizView view;

    /**
     * Creates a presenter that updates the given QuizView.
     */
    public TakeQuizPresenter(QuizView view){
        this.view = view;
    }

    /**
     * Displays the first question of a newly started quiz.
     */
    @Override
    public void prepareQuizStart(TakeQuizStartResponseModel r) {
        view.showQuestion(
                r.getQuizTitle(),
                r.getPrompt(),
                r.getOptions(),
                r.getQuestionIndex(),
                r.getTotalQuestions(),
                r.getMediaUrl()
        );
    }

    /**
     * Displays the next question during an ongoing quiz.
     */
    @Override
    public void presentQuestion(TakeQuizQuestionResponseModel r){
        view.showQuestion(
                null,
                r.getPrompt(),
                r.getOptions(),
                r.getQuestionIndex(),
                r.getTotalQuestions(),
                r.getMediaUrl()
        );
    }

    /**
     * Shows feedback after the user submits an answer.
     */
    @Override
    public void presentAnswerFeedback(AnswerFeedbackResponseModel r) {
        view.showAnswerFeedback(
                r.getFeedbackMessage(),
                r.getCorrectAnswer(),
                r.getExplanation(),
                r.getScore(),
                r.getCurrentStreak(),
                r.getHighestStreak()
        );
    }

    /**
     * Shows the quiz summary screen and ends the quiz.
     */
    @Override
    public void presentQuizEnd(TakeQuizEndResponseModel r) {
        view.showQuizEnd(
                "Quiz Completed!",
                r.getScore(),
                r.getTotalQuestions(),
                r.getDurationSeconds(),
                r.getHighestStreak()
        );
    }
}
