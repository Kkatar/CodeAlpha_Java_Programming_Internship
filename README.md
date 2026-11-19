# CodeAlpha — Java Programming Internship

**Student:** Piyush Vishnupant Divase
**Student ID:** CA/DF1/433397
**Domain:** Java Programming
**Batch:** 10th November 2026 – 10th December 2026
**Organisation:** CodeAlpha (www.codealpha.tech)

This repository contains the Java Programming internship tasks completed for CodeAlpha.
Four tasks from the task document are implemented, one folder per task.

---

## Tasks

| # | Task | Folder | Key requirements |
|---|------|--------|------------------|
| 1 | Student Grade Tracker | [`CodeAlpha_StudentGradeTracker`](./CodeAlpha_StudentGradeTracker) | Input & manage grades, average/highest/lowest, `ArrayList`, summary report, console UI |
| 2 | Stock Trading Platform | [`CodeAlpha_StockTradingPlatform`](./CodeAlpha_StockTradingPlatform) | Market data display, buy/sell, portfolio performance tracking, OOP, file I/O persistence |
| 3 | Artificial Intelligence Chatbot | [`CodeAlpha_AIChatbot`](./CodeAlpha_AIChatbot) | NLP techniques, trained TF-IDF intent model, FAQ training data, Swing GUI + console |
| 4 | Hotel Reservation System | [`CodeAlpha_HotelReservationSystem`](./CodeAlpha_HotelReservationSystem) | Room categories, availability search, booking & cancellation, payment simulation, file I/O |

Each project folder has its own `README.md` with requirements coverage, structure,
sample output and design notes.

---

## Requirements

- **JDK 8 or newer** (developed and tested on OpenJDK 11) — no external libraries, no build tool needed.
- Optional: a terminal that can display UTF-8 output.

Check your installation:

```bash
java -version
javac -version
```

---

## How to Run Any Project

Every project follows the same pattern — compile the `src` folder, run the main class:

```bash
cd CodeAlpha_StudentGradeTracker
javac -d out src/*.java
java -cp out StudentGradeTracker
```

| Task | Run command (from the project folder) |
|------|---------------------------------------|
| 1 | `java -cp out StudentGradeTracker` |
| 2 | `java -cp out StockTradingPlatform` |
| 3 | `java -cp out AIChatbot` &nbsp;·&nbsp; GUI: `java -cp out AIChatbot --gui` |
| 4 | `java -cp out HotelReservationSystem` |

Each folder also ships an executable `run.sh` (`./run.sh`) that compiles and runs it in one step.

> **Windows note:** if `javac` complains about unmappable characters, add `-encoding UTF-8`:
> `javac -encoding UTF-8 -d out src/*.java`

---

## File Storage

Tasks 2, 3 and 4 read/write plain-text data files in their own `data/` folder:

| Project | Data file | Purpose |
|---|---|---|
| Student Grade Tracker | `data/grades.csv` | Saved student records and scores |
| Stock Trading Platform | `data/account.csv`, `holdings.csv`, `transactions.csv`, `performance.csv` | Account balance, holdings, trade log, net-worth history |
| AI Chatbot | `data/training_data.txt` | FAQ training corpus (edit it to retrain the bot) |
| Hotel Reservation System | `data/reservations.csv` | Bookings and derived availability |

All files are created automatically on first run and reloaded on the next start.

---

## Concepts Demonstrated

- **OOP** — encapsulation, inheritance-free but interface-heavy domain models, enums, composition
- **Collections** — `ArrayList`, `LinkedHashMap`, streams for aggregation
- **File I/O** — `java.nio.file` readers/writers with CSV (and pipe-delimited) persistence
- **Exception handling** and input validation throughout the consoles
- **NLP / ML basics** — tokenisation, stop-word removal, stemming, TF-IDF vectors, cosine similarity
- **GUI** — Java Swing chat window (Task 3)
- **Domain modelling** — weighted average cost for holdings, half-open interval overlap for hotel availability

---

## Submission

- GitHub repository link: this repository
- Submission form: https://forms.gle/Za9tPtzLUiMJVL9k8 (Student ID **CA/DF1/433397**)
- LinkedIn: internship status post + project video explanation, tagging **@CodeAlpha**

*Submitted as part of the CodeAlpha Java Programming internship.*
