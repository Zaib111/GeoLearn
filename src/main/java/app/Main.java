package app;

import adapters.TakeQuizController;
import adapters.TakeQuizPresenter;
import entity.QuestionType;
import use_case.quiz.LocalQuestionRepository;
import use_case.quiz.QuestionRepository;
import use_case.quiz.TakeQuizInputBoundary;
import use_case.quiz.TakeQuizOutputBoundary;
import use_case.quiz.TakeQuizInteractor;
import view.QuizPanel;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        final AppBuilder appBuilder = new AppBuilder();
        appBuilder.build().setVisible(true);

        JFrame frame = new JFrame("Geography Quiz");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        QuizPanel quizPanel = new QuizPanel();
        TakeQuizOutputBoundary presenter = new TakeQuizPresenter(quizPanel);
        QuestionRepository repo = new LocalQuestionRepository();
        TakeQuizInputBoundary interactor = new TakeQuizInteractor(repo, presenter);
        TakeQuizController controller = new TakeQuizController(interactor);
        quizPanel.setController(controller);
        controller.startQuiz(entity.QuizType.CAPITALS,
                QuestionType.MCQ,
                5);
        frame.add(quizPanel);
        frame.setVisible(true);
    }
}
