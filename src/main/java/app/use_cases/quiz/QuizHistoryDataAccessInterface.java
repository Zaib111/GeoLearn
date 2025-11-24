package app.use_cases.quiz;

import app.entities.QuizHistoryEntry;

import java.util.List;

/**
 * Data access interface for storing and retrieving quiz history.
 * Implemented by infrastructure classes (e.g., in-memory, database).
 */
public interface QuizHistoryDataAccessInterface {
    void saveQuizAttempt(QuizHistoryEntry entry);
    List<QuizHistoryEntry> getAllQuizAttempts();
}
