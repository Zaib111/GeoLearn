package app.controllers;

import app.entities.QuestionType;
import app.entities.QuizType;
import app.use_cases.quiz.SubmitAnswerRequestModel;
import app.use_cases.quiz.TakeQuizInputBoundary;
import app.use_cases.quiz.TakeQuizStartRequestModel;

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
