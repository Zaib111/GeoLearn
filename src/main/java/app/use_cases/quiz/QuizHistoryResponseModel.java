package app.use_cases.quiz;

import app.entities.QuizHistoryEntry;

import java.util.List;

/**
 * Response model containing quiz history data to be presented to the user.
 */
public class QuizHistoryResponseModel {
    private final List<QuizHistoryEntry> historyEntries;

    public QuizHistoryResponseModel(List<QuizHistoryEntry> historyEntries) {
        this.historyEntries = historyEntries;
    }

    public List<QuizHistoryEntry> getHistoryEntries() {
        return historyEntries;
    }
}

