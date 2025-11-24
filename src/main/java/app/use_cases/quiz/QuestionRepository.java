package app.use_cases.quiz;

import app.entities.Question;
import app.entities.QuestionType;
import app.entities.QuizType;

import java.util.List;

/**
 * Question repository boundary for the quiz use case.
 */
public interface QuestionRepository {
    /**
     * Retrieves a list of questions for a quiz with the given type and question mode.
     *
     * @param quizType     the quiz category to retrieve questions for
     * @param questionType the type of question (multiple-choice or type-in)
     * @param limit        maximum number of questions to return
     */
    List<Question> getQuestionsForQuiz(QuizType quizType,
                                QuestionType questionType,
                                int limit);
}
