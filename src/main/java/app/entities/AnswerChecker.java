package app.entities;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utility class for comparing user answers to correct answers.
 * Performs normalization (case folding, accent removal, punctuation cleanup)
 * so that small formatting differences do not affect correctness.
 */
public final class AnswerChecker {
    /** Removes punctuation except letters, numbers, and spaces. */
    private static final Pattern NON_ALNUM_SPACE = Pattern.compile("[^\\p{Alnum}\\s]");

    /** Collapses multiple spaces into one. */
    private static final Pattern MULTISPACE = Pattern.compile("\\s+");

    private AnswerChecker() {}

    /**
     * Returns whether a free-text answer is correct.
     * Compares the normalized user input against the normalized correct answer
     * and all valid aliases.
     */
    public static boolean isCorrect(String userInput, Question q) {
        if (userInput == null) {
            return false;
        }
        String u = normalize(userInput);
        String c = normalize(q.getCorrect());

        if (u.equals(c)) return true;

        for (String alt : q.getAliases()) {
            if (u.equals(normalize(alt))) return true;
        }
        return false;
    }

    /**
     * Returns whether a multiple-choice option matches the correct answer.
     * Only compares the clicked text to the correct answer directly.
     */
    public static boolean isMcqCorrect(String clickedAnswer, Question q) {
        return normalize(clickedAnswer).equals(normalize(q.getCorrect()));
    }

    /**
     * Normalizes a string by:
     *  - trimming whitespace
     *  - removing accents
     *  - lowercasing
     *  - expanding abbreviations like "St." becoming "Saint"
     *  - removing punctuation
     *  - collapsing multiple spaces
     */
    public static String normalize(String s) {
        if (s == null) return "";
        String t = s.trim();

        t = Normalizer.normalize(t, Normalizer.Form.NFD);
        t = t.replaceAll("\\p{M}+", "");
        t = t.toLowerCase(Locale.ROOT);

        t = t.replace("st.", "saint")
                .replace("st ", "saint ")
                .replace("&", " and ")
                .replace("’","'");

        t = NON_ALNUM_SPACE.matcher(t).replaceAll("");
        t = MULTISPACE.matcher(t).replaceAll(" ").trim();

        return t;
    }
}
