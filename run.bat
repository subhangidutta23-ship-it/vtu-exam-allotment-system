@echo off
cd /d %~dp0
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac -cp "lib\*" -d out @sources.txt
if %errorlevel%==0 java -cp "out;lib\*" com.vtu.examsystem.Main