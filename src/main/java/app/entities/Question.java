package app.entities;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
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

    // Custom getters to return unmodifiable lists
    public List<String> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public List<String> getAliases() {
        return Collections.unmodifiableList(aliases);
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
