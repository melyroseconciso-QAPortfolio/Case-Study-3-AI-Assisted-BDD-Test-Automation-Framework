# A ready-to-run box containing Java 21, Maven and Chrome, so the tests run the same everywhere.
FROM maven:3.9-eclipse-temurin-21

COPY ci/install-chrome.sh /tmp/install-chrome.sh
RUN bash /tmp/install-chrome.sh

WORKDIR /app

# Download libraries first (cached), then copy the tests.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline || true
COPY src ./src

# Default: run everything. Override by adding arguments, e.g. -Dcucumber.filter.tags="@smoke"
ENTRYPOINT ["mvn", "-B", "clean", "test"]
