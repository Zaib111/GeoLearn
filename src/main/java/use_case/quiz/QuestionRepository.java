package use_case.quiz;
import entity.Question;
import entity.QuizType;
import entity.QuestionType;

import java.util.List;

public interface QuestionRepository {
    List<Question> getQuestionsForQuiz(QuizType quizType,
                                QuestionType questionType,
                                int limit);
}
