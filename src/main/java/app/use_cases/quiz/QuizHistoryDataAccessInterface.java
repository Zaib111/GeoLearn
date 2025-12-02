package app.use_cases.quiz;

import java.util.List;

import app.entities.QuizHistoryEntry;

/**
 * Data access interface for storing and retrieving quiz history.
 * Implemented by infrastructure classes (e.g., in-memory, database).
 */
public interface QuizHistoryDataAccessInterface {
    /**
     * Saves a completed quiz attempt into the quiz history data storage.
     *
     * @param entry the QuizHistoryEntry object containing the details of the completed quiz attempt,
     *              such as quiz type, question type, number of questions, score, duration, highest streak,
     *              and the completion timestamp
     */
    void saveQuizAttempt(QuizHistoryEntry entry);

    /**
     * Retrieves the list of all quiz attempts stored in the quiz history data storage.
     *
     * @return a list of QuizHistoryEntry objects representing details of all completed quiz attempts,
     *         including quiz type, question type, number of questions, score, duration, highest streak,
     *         and the completion timestamp
     */
    List<QuizHistoryEntry> getAllQuizAttempts();
}
