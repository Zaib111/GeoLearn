package use_case.quiz;

import java.util.List;

public class TakeQuizQuestionResponseModel {
    private final String prompt;
    private final List<String> options;
    private final int questionIndex;
    private final int totalQuestions;
    private final String mediaUrl;

    public TakeQuizQuestionResponseModel(String prompt,
                                         List<String> options,
                                         int questionIndex,
                                         int totalQuestions,
                                         String mediaUrl) {
        this.prompt = prompt;
        this.options = options;
        this.questionIndex = questionIndex;
        this.totalQuestions = totalQuestions;
        this.mediaUrl = null;
    }

    public String getPrompt() { return prompt; }
    public List<String> getOptions() { return options; }
    public int getQuestionIndex() { return questionIndex; }
    public int getTotalQuestions() { return totalQuestions; }
    public String getMediaUrl() { return mediaUrl; }
}
