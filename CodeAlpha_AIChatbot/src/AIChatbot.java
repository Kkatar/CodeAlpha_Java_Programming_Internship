import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Task 3 - Artificial Intelligence Chatbot
 *
 * A Java chatbot that talks to the user in the console (and also has a Swing GUI
 * in ChatBotGUI.java). It uses basic NLP (normalisation, tokenisation, stop-word
 * removal, stemming), a TF-IDF model trained on the FAQ data in
 * data/training_data.txt and cosine similarity for intent classification.
 *
 * Usage:
 *   java -cp out AIChatbot            -> console chat mode
 *   java -cp out AIChatbot --gui      -> graphical chat window
 */
public class AIChatbot {

    private static final String TRAINING_FILE = "data/training_data.txt";

    public static void main(String[] args) {
        if (args.length > 0 && (args[0].equals("--gui") || args[0].equals("-g"))) {
            try {
                ChatBotGUI.main(args);
            } catch (Exception ex) {
                System.out.println(" Could not start the GUI: " + ex.getMessage());
            }
            return;
        }

        ChatBot bot;
        try {
            bot = new ChatBot(TRAINING_FILE);
        } catch (IOException e) {
            System.out.println(" Error: " + e.getMessage());
            System.out.println(" Run this program from the project root folder so that"
                    + " data/training_data.txt is found.");
            return;
        }

        System.out.println("=========================================================");
        System.out.println("          AI CHATBOT  -  CodeAlpha  (AlphaBot)          ");
        System.out.println("=========================================================");
        System.out.println(" Model   : TF-IDF + cosine similarity (rule/trained based)");
        System.out.println(" Trained : " + bot.getIntentCount() + " intents, "
                + bot.getPatternCount() + " patterns, "
                + bot.getVocabularySize() + " vocabulary words");
        System.out.println(" Type your question, or 'help' for sample questions.");
        System.out.println(" Type 'bye' to end the conversation.");
        System.out.println("=========================================================\n");

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("You: ");
            if (!sc.hasNextLine()) {
                break;
            }
            String input = sc.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }
            if (input.equalsIgnoreCase("help")) {
                printHelp();
                continue;
            }
            if (input.equalsIgnoreCase("bye") || input.equalsIgnoreCase("exit")
                    || input.equalsIgnoreCase("quit")) {
                System.out.println("AlphaBot: " + bot.ask("bye"));
                break;
            }
            System.out.println("AlphaBot: " + bot.askWithMeta(input));
        }
        sc.close();
        System.out.println("\n Chat session ended. Thank you for talking to AlphaBot!");
    }

    private static void printHelp() {
        List<String> samples = Arrays.asList(
                "who are you",
                "what are the tasks",
                "what is task 1",
                "how many tasks do i need to complete",
                "what is the deadline",
                "how do i submit my task",
                "how do i upload to github",
                "will i get a certificate",
                "what are the perks",
                "what are the contact details",
                "how does this chatbot work"
        );
        System.out.println("\n Sample questions you can ask:");
        for (String s : samples) {
            System.out.println("   - " + s);
        }
        System.out.println();
    }
}
