package entity;

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

    public String getDisplayName() {
        return displayName;
        }
}