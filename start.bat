@echo off
if not defined SERVER_PORT set SERVER_PORT=24480
for /f "usebackq tokens=1,* delims==" %%A in (.env_50b38c46-0195-4b5c-9b2b-c44cb07ca7bd) do set %%A=%%B
call gradlew.bat bootJar -q
java -jar build\libs\app-0.1.0.jar --server.port=%SERVER_PORT%
