package app.use_cases.quiz;

import java.util.List;

import app.entities.QuizHistoryEntry;

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

