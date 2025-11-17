package use_case.quiz;

import entity.QuizType;
import entity.QuestionType;

public class TakeQuizStartRequestModel {
    private final QuizType quizType;
    private final QuestionType questionType;
    private final int numberOfQuestions;

    public TakeQuizStartRequestModel(QuizType quizType,
                                     QuestionType questionType,
                                     int numberOfQuestions) {
        this.quizType = quizType;
        this.questionType = questionType;
        this.numberOfQuestions = numberOfQuestions;
    }

    public QuizType getQuizType() { return quizType; }
    public QuestionType getQuestionType() { return questionType; }
    public int getNumberOfQuestions() { return numberOfQuestions; }
}
