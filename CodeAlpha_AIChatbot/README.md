# CodeAlpha_AIChatbot

**Java Programming Internship — Task 3: Artificial Intelligence Chatbot**
CodeAlpha | Student: Piyush Vishnupant Divase | ID: CA/DF1/433397

---

## Task Requirements Covered

| Requirement | Implementation |
|---|---|
| Create a Java-based chatbot for interactive communication | `AIChatbot.java` (console) + `ChatBotGUI.java` (Swing GUI) |
| Use Natural Language Processing (NLP) techniques | `TextProcessor` — normalisation, tokenisation, stop-word removal, stemming |
| Implement machine learning logic or rule-based answers | Trained **TF-IDF + cosine similarity** intent classifier (`NLPEngine`) with a keyword rule fallback |
| Train the bot to respond to frequently asked questions | 29 intents / 160+ example patterns in `data/training_data.txt` |
| Integrate with a GUI or web interface for real-time interaction | Swing desktop chat window (`--gui` argument) with a live chat area |

## Project Structure

```
CodeAlpha_AIChatbot/
├── src/
│   ├── TextProcessor.java      # NLP pipeline: normalise, tokenize, stop-words, stem
│   ├── Intent.java             # One trained intent (patterns + responses)
│   ├── TrainingDataLoader.java # Parses data/training_data.txt
│   ├── NLPEngine.java          # TF-IDF training + cosine similarity prediction
│   ├── ChatBot.java            # Conversation layer used by console and GUI
│   ├── ChatBotGUI.java         # Swing GUI (real-time chat window)
│   └── AIChatbot.java          # Main class (console mode / --gui)
├── data/
│   └── training_data.txt       # FAQ training corpus (editable, no recompile needed)
├── run.sh
└── README.md
```

## How to Compile and Run

```bash
javac -encoding UTF-8 -d out src/*.java
```

Console chat:

```bash
java -cp out AIChatbot
```

Graphical chat window:

```bash
java -cp out AIChatbot --gui
```

or simply `./run.sh` (console) / `./run.sh gui`.

## How the AI Works

**Training phase (at start-up)**

1. `TrainingDataLoader` reads every `##intent` block, its `-` example patterns and `@response` answers.
2. `TextProcessor` pre-processes each pattern:
   normalise → tokenise → remove stop-words → stem (`fees → fee`, `submission → submi…` style suffix stripping).
3. A vocabulary is built from the whole corpus and inverse document frequency is computed:
   `idf(t) = ln(N / df(t)) + 1`.
4. Every training pattern becomes a TF-IDF vector; its intent tag is stored beside it.

**Inference phase (per user message)**

1. The message goes through the same NLP pipeline and is converted to a TF-IDF vector
   using the trained vocabulary and IDF weights.
2. Cosine similarity is computed against every trained pattern vector.
3. The intent with the highest similarity above the confidence threshold (`0.20`) wins.
4. A direct keyword-containment rule is used as a safety net, otherwise a
   fallback ("I did not quite understand that…") is returned.
5. A random response is chosen from the matched intent so the bot does not repeat itself.

## Sample Session

```
You: what are the tasks
AlphaBot: There are 4 Java Programming tasks: 1) Student Grade Tracker, 2) Stock Trading
Platform, 3) Artificial Intelligence Chatbot, 4) Hotel Reservation System. You must
complete any 2 or 3 of them.
   [intent: tasks | confidence: 1.000]

You: how do i upload to github
AlphaBot: Upload your complete source code to GitHub in a repository named
CodeAlpha_ProjectName, and submit the GitHub repository link...
   [intent: github | confidence: 1.000]

You: is one task enough
AlphaBot: You must complete a minimum of 2 or 3 tasks out of the 4 listed. Submitting
only one task is considered incomplete and the certificate will not be issued.
   [intent: number_of_tasks | confidence: 1.000]

You: asdkjh qwe
AlphaBot: I could not match that question to my training data. Type "help" to see what
I can answer.
   [intent: fallback | confidence: 0.000]
```

## Extending the Training Data

No code change is needed — just edit `data/training_data.txt`:

```
##intent my_new_topic
- some example question
- another way of asking the same thing
@response The answer the bot should give.
```

Restart the application and the model is retrained automatically.

## Concepts Used

- Basic NLP pipeline without external libraries
- TF-IDF vector space model and cosine similarity (machine-learning style classification)
- Rule-based fallback matching
- Object oriented design with a clear separation of training data, model and interface
- Java Swing for the graphical chat interface
