package app.use_cases.quiz;

import java.util.List;

/**
 * Response model sent when a quiz begins.
 */
public class TakeQuizStartResponseModel {
    private final String quizTitle;
    private final String prompt;
    private final List<String> options;
    private final int questionIndex;
    private final int totalQuestions;
    private final String mediaUrl;

    /**
     * @param quizTitle title shown to the user
     * @param prompt text of the first question
     * @param options MCQ options (empty for type-in)
     * @param questionIndex index of the first question (0-based)
     * @param totalQuestions total number of questions in the quiz
     * @param mediaUrl optional image/flag URL
     */
    public TakeQuizStartResponseModel(String quizTitle,
                                      String prompt,
                                      List<String> options,
                                      int questionIndex,
                                      int totalQuestions,
                                      String mediaUrl) {
        this.quizTitle = quizTitle;
        this.prompt = prompt;
        this.options = options;
        this.questionIndex = questionIndex;
        this.totalQuestions = totalQuestions;
        this.mediaUrl = mediaUrl;
    }

    public String getQuizTitle() { return quizTitle; }
    public String getPrompt() { return prompt; }
    public List<String> getOptions() { return options; }
    public int getQuestionIndex() { return questionIndex; }
    public int getTotalQuestions() { return totalQuestions; }
    public String getMediaUrl() { return mediaUrl; }
}
