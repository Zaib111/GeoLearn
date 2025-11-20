package app.views.quiz;

import app.controllers.TakeQuizController;

import java.util.List;

public interface QuizView {
    void showQuestion(String quizTitle,
                      String prompt,
                      List<String> options,
                      int questionIndex,
                      int totalQuestions,
                      String mediaUrl);
    void showAnswerFeedback(String feedbackMessage,
                            String correctAnswer,
                            String explanation,
                            int score,
                            int currentStreak,
                            int highestStreak);
    void showQuizEnd(String summaryText,
                     int score,
                     int totalQuestions,
                     int durationSeconds,
                     int highestStreak);

    void setController(TakeQuizController controller);
}
