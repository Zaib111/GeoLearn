package app.use_cases.quiz;

import app.entities.QuestionType;
import app.entities.QuizType;

/**
 * Request model for starting a quiz session.
 */
public class TakeQuizStartRequestModel {

    private final QuizType quizType;
    private final QuestionType questionType;
    private final int numberOfQuestions;

    /**
     * @param quizType type of quiz selected
     * @param questionType question format (MCQ or TYPE_IN)
     * @param numberOfQuestions number of questions to generate
     */
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
