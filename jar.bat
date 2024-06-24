@echo off
setlocal

REM Set the servlet API path
set "SERVLET_API=%cd%\lib\servlet-api.jar"

REM Set the source directory
set "src=%cd%\src"

REM Set the output directory and JAR file path
set "OUTPUT_DIR=bin"
set "JAR_FILE=myLib\framework.jar"

REM Compile all .java files in the source directory
for /R "%src%" %%f in (*.java) do (
    javac -cp "%SERVLET_API%;%src%" -d "%OUTPUT_DIR%" "%%f"
)

REM Change directory to the output directory
cd /d %OUTPUT_DIR%

REM Create the JAR file
jar cvf "%JAR_FILE%" *

REM Copy the JAR file to the target directory
<<<<<<< Updated upstream
copy "%JAR_FILE%" "D:\S4\reseaux\Sprint0Test\lib"
=======
copy "%JAR_FILE%" "E:\S4\Web-Dynamique\GitLab\Sprint0Test\lib"
>>>>>>> Stashed changes

echo JAR creation and copy completed.
pause
