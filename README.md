# Case Study 3 : AI Assisted BDD Test Automation Framework

Self-initiated project to build hands-on test automation engineering skills, developed with AI assistance and run against the public Infor website.

A beginner-friendly web test automation framework, built with **Java, Selenium, Rest Assured, Cucumber (BDD), Maven and Allure**.
It has two kinds of tests:

- **Live test report** - https://melyroseconciso-qaportfolio.github.io/Case-Study-3-AI-Assisted-BDD-Test-Automation-Framework/
- **Browser (UI) tests** – read-only health checks against the public Infor website (https://www.infor.com), tagged `@ui`
- **API tests** – create, read, update and delete checks against the public practice API Restful-Booker (https://restful-booker.herokuapp.com), tagged `@api`

## Features

- Page Object Model + BDD (plain-English tests in `.feature` files)
- **CI/CD pipelines** (GitHub Actions and GitLab CI/CD): smoke tests gate the full regression, and the Allure report is published automatically
- **Docker**: run the whole framework in a container, with nothing installed except Docker
- **Mobile testing** with Chrome device emulation (Pixel 7, iPhone 12 Pro, iPad Air): screen size, viewport tag and sideways-overflow checks
- **API testing** with Rest Assured: GET, POST, PUT, DELETE, login, negative tests; every request and response is attached to the Allure report
- Config-driven (website, browser, headless mode, timeouts in `config.properties`)
- Screenshot attached automatically on failure
- **Allure reports** (dashboard, charts, history, screenshots, tags)
- **Parallel execution** (3 browsers at once)
- Optional **screen video recording** and **step-by-step screenshots**

## One-time setup (about 20 minutes)

1. **Install Java 17 or higher (JDK)** – download "Temurin 21 (LTS)" from https://adoptium.net and install it (tested with Java 21).
2. **Install Maven** – https://maven.apache.org/download.cgi (or `brew install maven` on Mac, `choco install maven` on Windows).
3. **Install Google Chrome** (the framework downloads the matching driver automatically).
4. **Install a code editor** – IntelliJ IDEA Community or VS Code (with the "Extension Pack for Java").
5. Check it worked – open a terminal and run:
   ```
   java -version
   mvn -version
   ```
   Both should print a version number (Java 17 or higher).

## Run the tests

Open a terminal **inside this folder** and run:

```
mvn clean test
```

The first run downloads libraries, so it takes a few minutes. (`clean` removes old results first.)

| What you want | Command |
|---|---|
| Run everything (3 in parallel) | `mvn clean test` |
| Only browser tests | `mvn clean test -Dcucumber.filter.tags="@ui"` |
| Only mobile tests | `mvn clean test -Dcucumber.filter.tags="@mobile"` |
| Only API tests (no browser needed) | `mvn clean test -Dcucumber.filter.tags="@api"` |
| Only quick checks | `mvn clean test -Dcucumber.filter.tags="@smoke"` |
| One test at a time | `mvn clean test -Dcucumber.execution.parallel.enabled=false` |
| Watch the browser | `mvn clean test -Dheadless=false -Dcucumber.execution.parallel.enabled=false` |
| Use Firefox | `mvn clean test -Dbrowser=firefox` |
| Screenshot after every step | `mvn clean test -DstepScreenshots=true` |
| Record a screen video | `mvn clean test -Dheadless=false -Drecord=true -Dcucumber.execution.parallel.enabled=false` |

## See the results

**Allure report (recommended)** – after the tests finish, run:

```
mvn allure:serve
```

A browser tab opens with the dashboard. Press `Ctrl+C` in the terminal to stop the report server.
The first time, Maven downloads the Allure tool, which takes a minute.

**Simple HTML report** – open `target/cucumber-reports/report.html`.

**Videos** (only when recording is on) – `target/videos/*.avi`. These are standard AVI (MJPG) files that play in Windows Media Player or VLC.

## How the project is organised

```
src/test/resources/features/   <- Tests written in plain English (.feature files)
src/test/java/.../steps/       <- Java code behind each plain-English sentence, plus Hooks
src/test/java/.../pages/       <- Page Objects: how to find things on each page
src/test/java/.../core/        <- Browser start-up, settings, video recorder
src/test/java/.../api/         <- ApiClient: how to talk to the API (like a Page Object for APIs)
src/test/java/.../runner/      <- The "start button" Maven runs
src/test/resources/config.properties         <- Website address, browser, timeouts, options
src/test/resources/junit-platform.properties <- Reports and parallel settings
```

## Run in Docker (optional)

With Docker Desktop installed, from the project folder:

```
docker build -t inforqa-tests .
docker run --rm --shm-size=1g -v "%cd%\target:/app/target" inforqa-tests
```

(`%cd%` works in Windows Command Prompt; use `$(pwd)` on Mac/Linux.) Results appear in your local `target` folder,
so `mvn allure:serve` still works. Add arguments to run a subset, e.g. `docker run ... inforqa-tests -Dcucumber.filter.tags="@api"`.

## CI/CD pipelines

- **GitHub Actions** (`.github/workflows/tests.yml`): runs on every push to `main`, on pull requests, and on demand.
  Stage 1 runs the `@smoke` tests; only if they pass does the full regression run (a quality gate).
  The Allure report is saved with each run and published to GitHub Pages.
- **GitLab CI/CD** (`.gitlab-ci.yml`): same flow with `smoke`, `regression` and `report` stages.

## Notes on the API tests

Restful-Booker is a shared public practice service. It resets itself regularly and can occasionally be slow or unavailable,
so an API failure is not always a framework problem. The login credentials in `config.properties` are the ones published in its documentation.

## If a test fails

Look at the screenshot in the report, then adjust the page address in the `.feature` file
or the element locator in the matching Page Object.
Be considerate: these tests only read pages. Do not add tests that submit forms or send heavy traffic.

## Roadmap

- [x] Phase 1-3: Framework core + BDD
- [x] Phase 4: Allure reporting, parallel execution, screenshots and optional video
- [x] Phase 5: API tests with Rest Assured
- [x] Phase 6a: Mobile device emulation (Chrome)
- [ ] Phase 6b: BrowserStack real devices, Appium
- [x] Phase 7: Dockerfile, GitHub Actions and GitLab CI/CD pipelines
- [ ] Phase 8: Kubernetes/Helm Selenium Grid, UiPath bot
- [ ] Phase 9: AI-assisted failure analysis
