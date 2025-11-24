package app.entities;

import lombok.Getter;

/**
 * Represents the categories of quizzes available in GeoLearn.
 * Each type has a user-friendly display name shown in the UI.
 */
@Getter
public enum QuizType {
    CAPITALS("Capitals Quiz"),
    FLAGS("Flags Quiz"),
    LANGUAGES("Languages Quiz"),
    CURRENCIES("Currencies Quiz");

    private final String displayName;

    /**
     * Constructs a QuizType with the given display name.
     *
     * @param displayName name shown in the quiz selection UI
     */
    QuizType(String displayName) {
        this.displayName = displayName;
    }

    public String toString(){
        return displayName;
    }
}