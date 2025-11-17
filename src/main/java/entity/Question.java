package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Objects;

public class Question {

    private final QuizType quizType;
    private final QuestionType questionType;
    private final String prompt;
    private final List<String> options;
    private final String correct;
    private final List<String> aliases;
    private final String explanation;
    private final String mediaUrl;

    public Question(QuizType quizType,
                    QuestionType questionType,
                    String prompt,
                    List<String> options,
                    String correct,
                    List<String> aliases,
                    String explanation,
                    String mediaUrl) {

        this.quizType = Objects.requireNonNull(quizType);
        this.questionType = Objects.requireNonNull(questionType);
        this.prompt = Objects.requireNonNull(prompt);

        this.options = new ArrayList<>(options != null ? options : List.of());
        this.correct = Objects.requireNonNull(correct);
        this.aliases = new ArrayList<>(aliases != null ? aliases : List.of());

        this.explanation = (explanation != null) ? explanation : "";
        this.mediaUrl = mediaUrl;
    }

    public QuizType getQuizType() {
        return quizType;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public String getPrompt() {
        return prompt;
    }

    public List<String> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public String getCorrect() {
        return correct;
    }

    public List<String> getAliases() {
        return Collections.unmodifiableList(aliases);
    }

    public String getExplanation() {
        return explanation;
    }

    @Override
    public String toString() {
        return "Question{" +
                "quizType=" + quizType +
                ", questionType=" + questionType +
                ", prompt='" + prompt + '\'' +
                ", correct='" + correct + '\'' +
                '}';
    }
}
