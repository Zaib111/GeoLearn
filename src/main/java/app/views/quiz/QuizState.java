package app.views.quiz;

import java.util.Collections;
import java.util.List;

import app.entities.QuizHistoryEntry;

/**
 * View state for the quiz screen. Stores UI data for the current question,
 * feedback, and quiz completion so the presenter can update the view.
 */
public class QuizState {

    // Current question info
    private String quizTitle;
    private String prompt;
    private List<String> options = Collections.emptyList();
    private int questionIndex;
    private int totalQuestions;
    private String mediaUrl;

    // Feedback info
    private String feedbackMessage;
    private String correctAnswer;
    private String explanation;
    private int score;
    private int currentStreak;
    private int highestStreak;

    // End-of-quiz info
    private int durationSeconds;

    // Quiz history
    private List<QuizHistoryEntry> historyEntries = Collections.emptyList();

    // UI flags
    private boolean showQuestion;
    private boolean showFeedback;
    private boolean showEnd;
    private boolean showHistory;

    public QuizState() {
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public List<String> getOptions() {
        return options;
    }

    /**
     * Sets the options for the current question. If null, an empty list is used.
     * @param options - The options for the current question.
     */
    public void setOptions(List<String> options) {
        if (options != null) {
            this.options = options;
        }
        else {
            this.options = Collections.emptyList();
        }
    }

    public int getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public void setFeedbackMessage(String feedbackMessage) {
        this.feedbackMessage = feedbackMessage;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getHighestStreak() {
        return highestStreak;
    }

    public void setHighestStreak(int highestStreak) {
        this.highestStreak = highestStreak;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public List<QuizHistoryEntry> getHistoryEntries() {
        return historyEntries;
    }

    /**
     * Sets the quiz history entries. If null, an empty list is used.
     * @param historyEntries - The quiz history entries.
     */
    public void setHistoryEntries(List<QuizHistoryEntry> historyEntries) {
        if (historyEntries != null) {
            this.historyEntries = historyEntries;
        }
        else {
            this.historyEntries = Collections.emptyList();
        }
    }

    public boolean isShowQuestion() {
        return showQuestion;
    }

    public void setShowQuestion(boolean showQuestion) {
        this.showQuestion = showQuestion;
    }

    public boolean isShowFeedback() {
        return showFeedback;
    }

    public void setShowFeedback(boolean showFeedback) {
        this.showFeedback = showFeedback;
    }

    public boolean isShowEnd() {
        return showEnd;
    }

    public void setShowEnd(boolean showEnd) {
        this.showEnd = showEnd;
    }

    public boolean isShowHistory() {
        return showHistory;
    }

    public void setShowHistory(boolean showHistory) {
        this.showHistory = showHistory;
    }
}
