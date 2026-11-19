import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * One trained intent of the chatbot: a set of example patterns (training data)
 * and one or more possible answers.
 */
public class Intent {

    private final String tag;
    private final List<String> patterns = new ArrayList<>();
    private final List<String> responses = new ArrayList<>();

    public Intent(String tag, String[] patterns, String[] responses) {
        this.tag = tag;
        this.patterns.addAll(Arrays.asList(patterns));
        this.responses.addAll(Arrays.asList(responses));
    }

    public String getTag() {
        return tag;
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public List<String> getResponses() {
        return responses;
    }

    @Override
    public String toString() {
        return tag + " (" + patterns.size() + " patterns)";
    }
}
