package app.entities;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single quiz question.
 *
 * Stores the prompt, answer options (if MCQ), the correct answer,
 * accepted aliases, an explanation, and an optional media URL (e.g., flag).
 */
@Getter
public class Question {

    /** Category of quiz this question belongs to (e.g., Capitals, Flags). */
    private final QuizType quizType;

    /** Whether the question is MCQ or type-in. */
    private final QuestionType questionType;

    /** The text of the question presented to the user. */
    private final String prompt;

    /** Multiple-choice options (empty for type-in questions). */
    private final List<String> options;

    /** The correct answer. */
    private final String correct;

    /** Alternate acceptable answers for type-in mode. */
    private final List<String> aliases;

    /** Explanation displayed after answering. */
    private final String explanation;

    /** Optional flag image. */
    private final String mediaUrl;

    /**
     * Constructs a Question.
     */
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

    /** Returns an unmodifiable list of options. */
    public List<String> getOptions() {
        return Collections.unmodifiableList(options);
    }

    /** Returns an unmodifiable list of aliases. */
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
