#!/usr/bin/env bash
# Compile and run the AI Chatbot
#   ./run.sh        -> console chat
#   ./run.sh gui    -> Swing GUI chat window
set -e
cd "$(dirname "$0")"
mkdir -p out
javac -encoding UTF-8 -d out src/*.java
if [ "$1" = "gui" ]; then
  java -Dfile.encoding=UTF-8 -cp out AIChatbot --gui
else
  java -Dfile.encoding=UTF-8 -cp out AIChatbot
fi
