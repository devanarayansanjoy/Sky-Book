#!/bin/bash
# Move to the directory where this script is located
cd "$(dirname "$0")"

# Compile the code
echo "Compiling Sky-Book..."
find src -name "*.java" > sources.txt
javac @sources.txt

# Run the application
echo "Starting Sky-Book..."
java -cp src app.FlightReservationApp
