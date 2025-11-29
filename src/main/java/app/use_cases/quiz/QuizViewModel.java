package app.use_cases.quiz;

import app.views.ViewModel;
import app.views.quiz.QuizState;

/**
 * ViewModel for the Quiz feature.
 * Holds the QuizState and notifies observers when updated.
 */
public class QuizViewModel extends ViewModel<QuizState> {

    public QuizViewModel() {
        super(new QuizState());
    }
}