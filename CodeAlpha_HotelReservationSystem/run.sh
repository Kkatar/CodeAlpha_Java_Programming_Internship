#!/usr/bin/env bash
# Compile and run the Hotel Reservation System
set -e
cd "$(dirname "$0")"
mkdir -p out
javac -encoding UTF-8 -d out src/*.java
java -Dfile.encoding=UTF-8 -cp out HotelReservationSystem
