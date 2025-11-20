package app.entities;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class AnswerChecker {
    private static final Pattern NON_ALNUM_SPACE = Pattern.compile("[^\\p{Alnum}\\s]");
    private static final Pattern MULTISPACE = Pattern.compile("\\s+");

    private AnswerChecker() {}

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

    public static boolean isMcqCorrect(String clickedAnswer, Question q) {
        return normalize(clickedAnswer).equals(normalize(q.getCorrect()));
    }

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
