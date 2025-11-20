package app.use_cases.quiz;

import app.entities.Question;
import app.entities.QuestionType;
import app.entities.QuizType;

import java.util.List;

public interface QuestionRepository {
    List<Question> getQuestionsForQuiz(QuizType quizType,
                                QuestionType questionType,
                                int limit);
}
