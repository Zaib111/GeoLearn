package app.use_cases.quiz;

import java.util.List;

/**
 * Response model for presenting an individual quiz question to the user.
 * It contains all information needed to display a single question in the quiz flow, including:</p>
 * <ul>
 *     <li>The question prompt (text)</li>
 *     <li>Multiple-choice options (if applicable)</li>
 *     <li>Progress information such as current index and total questions</li>
 *     <li>An optional media URL (e.g., flag image)</li>
 * </ul>
 */
public class TakeQuizQuestionResponseModel {
    private final String prompt;
    private final List<String> options;
    private final int questionIndex;
    private final int totalQuestions;
    private final String mediaUrl;

    /**
     * Constructs the response model for a quiz question.
     *
     * @param prompt the text prompt to display
     * @param options answer choices (MCQ) or an empty list for type-in questions
     * @param questionIndex zero-based index of the current question
     * @param totalQuestions total questions in the quiz
     * @param mediaUrl URL to an image or other media associated with the question;
     *                 may be null if not applicable
     */
    public TakeQuizQuestionResponseModel(String prompt,
                                         List<String> options,
                                         int questionIndex,
                                         int totalQuestions,
                                         String mediaUrl) {
        this.prompt = prompt;
        this.options = options;
        this.questionIndex = questionIndex;
        this.totalQuestions = totalQuestions;
        this.mediaUrl = mediaUrl;
    }

    public String getPrompt() { return prompt; }
    public List<String> getOptions() { return options; }
    public int getQuestionIndex() { return questionIndex; }
    public int getTotalQuestions() { return totalQuestions; }
    public String getMediaUrl() { return mediaUrl; }
}
