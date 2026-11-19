import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the training data file (data/training_data.txt) and builds the intent list.
 *
 * File format:
 *   ##intent <tag>
 *   - example pattern
 *   @response bot answer
 */
public class TrainingDataLoader {

    public static List<Intent> load(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IOException("Training file not found: " + filePath);
        }

        List<Intent> intents = new ArrayList<>();
        String tag = null;
        List<String> patterns = new ArrayList<>();
        List<String> responses = new ArrayList<>();

        for (String raw : Files.readAllLines(path)) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#") && !line.startsWith("##")) {
                continue;
            }
            if (line.startsWith("##intent")) {
                // flush previous intent
                if (tag != null && !patterns.isEmpty()) {
                    intents.add(new Intent(tag, patterns.toArray(new String[0]),
                            responses.toArray(new String[0])));
                }
                tag = line.substring("##intent".length()).trim();
                patterns = new ArrayList<>();
                responses = new ArrayList<>();
            } else if (line.startsWith("-") && tag != null) {
                patterns.add(line.substring(1).trim());
            } else if (line.startsWith("@response") && tag != null) {
                responses.add(line.substring("@response".length()).trim());
            }
        }
        if (tag != null && !patterns.isEmpty()) {
            intents.add(new Intent(tag, patterns.toArray(new String[0]),
                    responses.toArray(new String[0])));
        }
        return intents;
    }
}
