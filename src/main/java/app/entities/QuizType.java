package app.entities;

import lombok.Getter;

@Getter
public enum QuizType {
    CAPITALS("Capitals Quiz"),
    FLAGS("Flags Quiz"),
    LANGUAGES("Languages Quiz"),
    CURRENCIES("Currencies Quiz");

    private final String displayName;

    QuizType(String displayName) {
        this.displayName = displayName;
    }

    public String toString(){
        return displayName;
    }
}