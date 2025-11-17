package adapters;

import use_case.quiz.*;
import view.QuizView;

public class TakeQuizPresenter implements TakeQuizOutputBoundary{
    private final QuizView view;

    public TakeQuizPresenter(QuizView view){
        this.view = view;
    }

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
