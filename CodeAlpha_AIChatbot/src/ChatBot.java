import java.io.IOException;
import java.util.List;

/**
 * Connects the NLP engine with the conversation: exposes a single ask() method
 * that both the console and the GUI use.
 */
public class ChatBot {

    private final NLPEngine engine;

    public ChatBot(String trainingFile) throws IOException {
        List<Intent> intents = TrainingDataLoader.load(trainingFile);
        if (intents.isEmpty()) {
            throw new IOException("No intents found in " + trainingFile);
        }
        this.engine = new NLPEngine(intents);
    }

    /** Returns the bot's reply for a user message. */
    public String ask(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Please type a question first.";
        }
        NLPEngine.Prediction p = engine.predict(userMessage);
        if (p.tag == null || p.confidence < engine.getConfidenceThreshold()) {
            return engine.getFallback();
        }
        return engine.getResponse(p.tag);
    }

    /** Same as ask() but also reports the detected intent and confidence (for the console). */
    public String askWithMeta(String userMessage) {
        NLPEngine.Prediction p = engine.predict(userMessage);
        String reply = (p.tag == null || p.confidence < engine.getConfidenceThreshold())
                ? engine.getFallback()
                : engine.getResponse(p.tag);
        return String.format("%s%n   [intent: %s | confidence: %.3f]", reply,
                p.tag == null ? "fallback" : p.tag, p.confidence);
    }

    public int getVocabularySize() {
        return engine.getVocabularySize();
    }

    public int getPatternCount() {
        return engine.getTrainingPatternCount();
    }

    public int getIntentCount() {
        return engine.getIntents().size();
    }
}
