package app.presenters;

import app.use_cases.quiz.*;
import app.views.ViewModel;
import app.views.quiz.QuizState;

/**
 * Presenter for the quiz feature. Converts use-case response models
 * into view model state updates that trigger view changes.
 */
public class TakeQuizPresenter implements TakeQuizOutputBoundary {
    private final ViewModel<QuizState> viewModel;

    /**
     * Creates a presenter that updates the given view model.
     *
     * @param viewModel the quiz view model
     */
    public TakeQuizPresenter(ViewModel<QuizState> viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * Displays the first question of a newly started quiz.
     */
    @Override
    public void prepareQuizStart(TakeQuizStartResponseModel r) {
        final QuizState state = new QuizState();
        state.setQuizTitle(r.getQuizTitle());
        state.setPrompt(r.getPrompt());
        state.setOptions(r.getOptions());
        state.setQuestionIndex(r.getQuestionIndex());
        state.setTotalQuestions(r.getTotalQuestions());
        state.setMediaUrl(r.getMediaUrl());
        state.setShowQuestion(true);
        state.setShowFeedback(false);
        state.setShowEnd(false);
        state.setShowHistory(false);
        viewModel.updateState(state);
    }

    /**
     * Displays the next question during an ongoing quiz.
     */
    @Override
    public void presentQuestion(TakeQuizQuestionResponseModel r) {
        final QuizState state = new QuizState();
        state.setPrompt(r.getPrompt());
        state.setOptions(r.getOptions());
        state.setQuestionIndex(r.getQuestionIndex());
        state.setTotalQuestions(r.getTotalQuestions());
        state.setMediaUrl(r.getMediaUrl());
        state.setShowQuestion(true);
        state.setShowFeedback(false);
        state.setShowEnd(false);
        state.setShowHistory(false);
        viewModel.updateState(state);
    }

    /**
     * Shows feedback after the user submits an answer.
     */
    @Override
    public void presentAnswerFeedback(AnswerFeedbackResponseModel r) {
        final QuizState state = new QuizState();
        state.setFeedbackMessage(r.getFeedbackMessage());
        state.setCorrectAnswer(r.getCorrectAnswer());
        state.setExplanation(r.getExplanation());
        state.setScore(r.getScore());
        state.setCurrentStreak(r.getCurrentStreak());
        state.setHighestStreak(r.getHighestStreak());
        state.setShowQuestion(false);
        state.setShowFeedback(true);
        state.setShowEnd(false);
        state.setShowHistory(false);
        viewModel.updateState(state);
    }

    /**
     * Shows the quiz summary screen and ends the quiz.
     */
    @Override
    public void presentQuizEnd(TakeQuizEndResponseModel r) {
        final QuizState state = new QuizState();
        state.setScore(r.getScore());
        state.setTotalQuestions(r.getTotalQuestions());
        state.setDurationSeconds(r.getDurationSeconds());
        state.setHighestStreak(r.getHighestStreak());
        state.setShowQuestion(false);
        state.setShowFeedback(false);
        state.setShowEnd(true);
        state.setShowHistory(false);
        viewModel.updateState(state);
    }

    /**
     * Presents the quiz history from the database to the view.
     */
    @Override
    public void presentQuizHistory(QuizHistoryResponseModel r) {
        final QuizState state = new QuizState();
        state.setHistoryEntries(r.getHistoryEntries());
        state.setShowQuestion(false);
        state.setShowFeedback(false);
        state.setShowEnd(false);
        state.setShowHistory(true);
        viewModel.updateState(state);
    }
}
