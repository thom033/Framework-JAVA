@echo off
setlocal

REM Set the servlet API path
set "SERVLET_API=%cd%\lib\servlet-api.jar"
set "GSON_API=%cd%\lib\gson-2.10.jar"

REM Set the source directory
set "src=%cd%\src"

REM Set the output directory and JAR file path
set "OUTPUT_DIR=bin"
set "JAR_FILE=myLib\framework.jar"

REM Compile all .java files in the source directory
for /R "%src%" %%f in (*.java) do (
    javac -cp "%SERVLET_API%;%GSON_API%;%src%" -d "%OUTPUT_DIR%" "%%f"
)

REM Change directory to the output directory
cd /d %OUTPUT_DIR%

REM Create the JAR file
jar cvf "%JAR_FILE%" *

REM Copy the JAR file to the target directory
copy "%JAR_FILE%" "F:\S4\Sprint0Test\lib"

echo JAR creation and copy completed.
pause
