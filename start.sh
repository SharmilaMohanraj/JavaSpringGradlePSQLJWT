#!/usr/bin/env bash
set -e
SERVER_PORT="${SERVER_PORT:-24480}"
set -a; [ -f .env_50b38c46-0195-4b5c-9b2b-c44cb07ca7bd ] && . ./.env_50b38c46-0195-4b5c-9b2b-c44cb07ca7bd; set +a
./gradlew bootJar -q --no-daemon -Dorg.gradle.jvmargs='-Xmx192m -XX:MaxMetaspaceSize=128m' -Dorg.gradle.workers.max=1
java -jar "build/libs/JavaSpringMicroGradlePSQLJWT2-0.1.0.jar" --server.port=$SERVER_PORT
