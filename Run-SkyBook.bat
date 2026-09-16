@echo off
REM Move to the directory where this script is located
cd /d "%~dp0"

echo Compiling Sky-Book...
REM Find all .java files and write them to sources.txt
dir /s /b src\*.java > sources.txt
javac @sources.txt

echo Starting Sky-Book...
java -cp src app.FlightReservationApp
pause
