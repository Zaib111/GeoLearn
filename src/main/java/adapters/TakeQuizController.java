package adapters;

import entity.QuestionType;
import entity.QuizType;
import use_case.quiz.SubmitAnswerRequestModel;
import use_case.quiz.TakeQuizInputBoundary;
import use_case.quiz.TakeQuizStartRequestModel;

public class TakeQuizController {

    private final TakeQuizInputBoundary interactor;

    public TakeQuizController(TakeQuizInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void startQuiz(QuizType quizType,
                          QuestionType questionType,
                          int numberOfQuestions) {
        TakeQuizStartRequestModel request = new TakeQuizStartRequestModel(
                quizType,
                questionType,
                numberOfQuestions
        );
        interactor.startQuiz(request);
    }

    public void submitAnswer(String userAnswer) {
        SubmitAnswerRequestModel request = new SubmitAnswerRequestModel(userAnswer);
        interactor.submitAnswer(request);
    }

    public void nextQuestion() {
        interactor.nextQuestion();
    }

    public void timeExpired() {
        interactor.timeExpired();
    }
}
