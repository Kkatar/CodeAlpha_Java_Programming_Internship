import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Swing GUI for the chatbot (real-time interactive chat window).
 * Works with Java 8+ and needs no external library.
 */
public class ChatBotGUI extends JFrame {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
    private final ChatBot bot;
    private final JTextArea chatArea = new JTextArea();
    private final JTextField inputField = new JTextField();

    public ChatBotGUI(ChatBot bot) {
        this.bot = bot;
        setTitle("AlphaBot - CodeAlpha AI Chatbot");
        setSize(700, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        buildUI();
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 64, 124));
        JLabel title = new JLabel("  AlphaBot  |  CodeAlpha Internship Assistant");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
        header.add(title, BorderLayout.WEST);
        JLabel stats = new JLabel(String.format("%d intents | %d patterns  ",
                bot.getIntentCount(), bot.getPatternCount()));
        stats.setForeground(new Color(200, 215, 240));
        stats.setFont(new Font("SansSerif", Font.PLAIN, 11));
        header.add(stats, BorderLayout.EAST);

        // Chat area
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        chatArea.setBackground(new Color(248, 249, 251));
        chatArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JScrollPane scroll = new JScrollPane(chatArea);

        // Input row
        JPanel bottom = new JPanel(new BorderLayout(8, 0));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JButton send = new JButton("Send");
        send.setBackground(new Color(30, 64, 124));
        send.setForeground(Color.WHITE);
        send.setFont(new Font("SansSerif", Font.BOLD, 13));
        send.setFocusPainted(false);
        bottom.add(inputField, BorderLayout.CENTER);
        bottom.add(send, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        send.addActionListener(this::onSend);
        inputField.addActionListener(this::onSend);

        appendBot("Hello! I am AlphaBot, your CodeAlpha internship assistant. "
                + "Ask me about the tasks, deadlines, submission, certificates or GitHub.");
    }

    private void onSend(ActionEvent e) {
        String text = inputField.getText().trim();
        if (text.isEmpty()) {
            return;
        }
        appendUser(text);
        inputField.setText("");
        String reply = bot.ask(text);
        appendBot(reply);
    }

    private void appendUser(String text) {
        chatArea.append("[" + LocalTime.now().format(TIME) + "] You: " + text + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private void appendBot(String text) {
        chatArea.append("[" + LocalTime.now().format(TIME) + "] AlphaBot: " + text + "\n\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        ChatBot bot = new ChatBot("data/training_data.txt");
        SwingUtilities.invokeLater(() -> new ChatBotGUI(bot).setVisible(true));
    }
}
