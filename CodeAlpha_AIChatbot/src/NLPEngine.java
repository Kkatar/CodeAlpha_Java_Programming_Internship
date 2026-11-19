import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * The "trained model" of the chatbot.
 *
 * Training : every example pattern of every intent is pre-processed (NLP pipeline)
 *            and converted into a TF-IDF vector. The IDF values are computed from
 *            the whole training corpus.
 * Inference: the user's sentence is vectorised with the same vocabulary and IDF
 *            weights, then the cosine similarity against every trained pattern is
 *            calculated. The best matching intent above a confidence threshold wins.
 */
public class NLPEngine {

    private static final double CONFIDENCE_THRESHOLD = 0.20;

    private final List<Intent> intents = new ArrayList<>();
    private final List<String> vocabulary = new ArrayList<>();
    private final Map<String, Integer> vocabIndex = new HashMap<>();
    private final double[] idf;

    // trained vectors, parallel to trainedTags
    private final List<double[]> trainedVectors = new ArrayList<>();
    private final List<String> trainedTags = new ArrayList<>();

    private final Random random = new Random();

    public NLPEngine(List<Intent> intents) {
        this.intents.addAll(intents);
        train();
        this.idf = computeIdf();
        buildVectors();
    }

    // ------------------------------------------------------------------
    //  Training
    // ------------------------------------------------------------------
    private void train() {
        for (Intent intent : intents) {
            for (String pattern : intent.getPatterns()) {
                for (String token : TextProcessor.preprocess(pattern)) {
                    if (!vocabIndex.containsKey(token)) {
                        vocabIndex.put(token, vocabulary.size());
                        vocabulary.add(token);
                    }
                }
            }
        }
    }

    /** idf(t) = ln( N / df(t) ) + 1  (smoothed, never zero). */
    private double[] computeIdf() {
        int n = 0;
        Map<String, Integer> df = new HashMap<>();
        for (Intent intent : intents) {
            for (String pattern : intent.getPatterns()) {
                n++;
                for (String token : new java.util.HashSet<>(TextProcessor.preprocess(pattern))) {
                    df.merge(token, 1, Integer::sum);
                }
            }
        }
        double[] result = new double[vocabulary.size()];
        for (int i = 0; i < vocabulary.size(); i++) {
            int docFreq = df.getOrDefault(vocabulary.get(i), 0);
            result[i] = Math.log((double) Math.max(1, n) / Math.max(1, docFreq)) + 1.0;
        }
        return result;
    }

    private void buildVectors() {
        for (Intent intent : intents) {
            for (String pattern : intent.getPatterns()) {
                trainedVectors.add(vectorise(pattern));
                trainedTags.add(intent.getTag());
            }
        }
    }

    /** TF-IDF vector of a raw sentence. */
    private double[] vectorise(String text) {
        double[] v = new double[vocabulary.size()];
        List<String> tokens = TextProcessor.preprocess(text);
        if (tokens.isEmpty()) {
            return v;
        }
        Map<String, Integer> tf = new HashMap<>();
        for (String t : tokens) {
            tf.merge(t, 1, Integer::sum);
        }
        for (Map.Entry<String, Integer> e : tf.entrySet()) {
            Integer idx = vocabIndex.get(e.getKey());
            if (idx != null) {
                double termFreq = (double) e.getValue() / tokens.size();
                v[idx] = termFreq * idf[idx];
            }
        }
        return v;
    }

    // ------------------------------------------------------------------
    //  Inference
    // ------------------------------------------------------------------
    public static class Prediction {
        public final String tag;
        public final double confidence;

        Prediction(String tag, double confidence) {
            this.tag = tag;
            this.confidence = confidence;
        }
    }

    /** Returns the best matching intent and its confidence score. */
    public Prediction predict(String userText) {
        double[] userVector = vectorise(userText);
        double best = 0.0;
        String bestTag = null;

        for (int i = 0; i < trainedVectors.size(); i++) {
            double sim = cosineSimilarity(userVector, trainedVectors.get(i));
            if (sim > best) {
                best = sim;
                bestTag = trainedTags.get(i);
            }
        }

        // keyword fallback: direct containment of a strong keyword
        if (best < CONFIDENCE_THRESHOLD) {
            String normalised = TextProcessor.normalise(userText);
            for (Intent intent : intents) {
                for (String pattern : intent.getPatterns()) {
                    String p = TextProcessor.normalise(pattern);
                    if (!p.isEmpty() && normalised.contains(p) && p.length() >= 4) {
                        return new Prediction(intent.getTag(), Math.max(best, 0.35));
                    }
                }
            }
        }
        return new Prediction(bestTag, Math.round(best * 1000.0) / 1000.0);
    }

    private static double cosineSimilarity(double[] a, double[] b) {
        double dot = 0.0, na = 0.0, nb = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        if (na == 0 || nb == 0) {
            return 0.0;
        }
        return dot / (Math.sqrt(na) * Math.sqrt(nb));
    }

    /** Picks a random trained answer for the intent tag. */
    public String getResponse(String tag) {
        for (Intent intent : intents) {
            if (intent.getTag().equals(tag) && !intent.getResponses().isEmpty()) {
                List<String> r = intent.getResponses();
                return r.get(random.nextInt(r.size()));
            }
        }
        return getFallback();
    }

    public String getFallback() {
        String[] fallbacks = {
            "I did not quite understand that. You can ask me about the tasks, the deadline, submission steps, certificates or GitHub.",
            "Sorry, I am still learning. Try asking something like \"what are the tasks\" or \"how do I submit my work\".",
            "I could not match that question to my training data. Type \"help\" to see what I can answer."
        };
        return fallbacks[random.nextInt(fallbacks.length)];
    }

    public double getConfidenceThreshold() {
        return CONFIDENCE_THRESHOLD;
    }

    public int getVocabularySize() {
        return vocabulary.size();
    }

    public int getTrainingPatternCount() {
        return trainedVectors.size();
    }

    public List<Intent> getIntents() {
        return intents;
    }
}
