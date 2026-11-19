import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Lightweight NLP pre-processing: normalisation, tokenisation, stop-word removal
 * and stemming. No external library is required.
 */
public class TextProcessor {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "an", "the", "is", "are", "was", "were", "am", "be", "been", "being",
            "do", "does", "did", "of", "to", "in", "on", "at", "for", "with", "about",
            "i", "me", "my", "you", "your", "we", "our", "it", "its", "this", "that",
            "and", "or", "but", "so", "if", "then", "please", "can", "could", "would",
            "will", "shall", "should", "may", "might", "have", "has", "had", "there"
    ));

    /** Words that carry the intent of a question and must never be dropped. */
    private static final Set<String> KEY_WORDS = new HashSet<>(Arrays.asList(
            "hi", "hey", "hello", "bye", "thanks", "help", "fee", "fees", "certificate",
            "deadline", "task", "tasks", "domain", "submit", "submission", "github",
            "whatsapp", "linkedin", "certify", "contact", "email", "duration", "internship"
    ));

    /** Poor-man's stemmer: strips common English suffixes. */
    private static final Map<String, String> STEMMER = new HashMap<>();
    static {
        STEMMER.put("ies", "y");
        STEMMER.put("ing", "");
        STEMMER.put("es", "");
        STEMMER.put("ed", "");
        STEMMER.put("s", "");
        STEMMER.put("?", "");
        STEMMER.put("!", "");
    }

    /** Lower-cases, removes punctuation and collapses whitespace. */
    public static String normalise(String text) {
        if (text == null) return "";
        return text.toLowerCase(Locale.ROOT)
                   .replaceAll("[^a-z0-9\\s?]", " ")
                   .replaceAll("\\s+", " ")
                   .trim();
    }

    public static List<String> tokenize(String text) {
        String normalised = normalise(text);
        if (normalised.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(normalised.split(" ")));
    }

    public static List<String> removeStopWords(List<String> tokens) {
        List<String> out = new ArrayList<>();
        for (String t : tokens) {
            if (!STOP_WORDS.contains(t) || KEY_WORDS.contains(t)) {
                out.add(t);
            }
        }
        return out;
    }

    public static String stem(String word) {
        if (word.length() <= 3) return word;
        for (Map.Entry<String, String> e : STEMMER.entrySet()) {
            if (word.endsWith(e.getKey()) && word.length() - e.getKey().length() >= 2) {
                return word.substring(0, word.length() - e.getKey().length()) + e.getValue();
            }
        }
        return word;
    }

    /** Full pipeline: normalise -> tokenize -> stop-word removal -> stemming. */
    public static List<String> preprocess(String text) {
        List<String> tokens = removeStopWords(tokenize(text));
        List<String> stemmed = new ArrayList<>();
        for (String t : tokens) {
            stemmed.add(stem(t));
        }
        return stemmed;
    }
}
