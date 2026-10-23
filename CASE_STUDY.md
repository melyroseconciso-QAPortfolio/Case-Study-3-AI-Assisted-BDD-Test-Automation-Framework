# Case Study 3: Building an AI-Assisted BDD Test Automation Framework

**Type:** Independent learning project (not commercial delivery)
**Built with:** Java 21, Selenium 4, Cucumber (BDD), Rest Assured, Maven, Allure, GitHub Actions
**Built with AI assistance:** Yes. I directed the work, ran every build, debugged the failures and made the decisions; an AI assistant generated the initial code and explanations.

**Repository:** https://github.com/melyroseconciso-QAPortfolio/Case-Study-3-AI-Assisted-BDD-Test-Automation-Framework
**Live test report:** https://melyroseconciso-qaportfolio.github.io/Case-Study-3-AI-Assisted-BDD-Test-Automation-Framework/

---

## 1. Why I built this

I applied for a Senior Automation Engineering role and received a strong overall score, with one clear piece of feedback: my profile pointed to broad QA leadership and quality governance rather than deep, hands-on automation engineering. The feedback recommended showing specific tools (Selenium, Appium, UiPath RPA), AI-driven automation, and CI/CD pipeline design.

I agreed with the assessment. My background is in leading QA teams and quality governance, and my hands-on framework work dates from earlier in my career (see section 2). Rather than reword my resume, I decided to close the gap by building a modern framework end to end, with current tools and an automated pipeline, so that I could understand the engineering decisions first-hand and show evidence instead of claims.

## 2. What I am building on: earlier framework experience

At Accenture, while delivering an automated regression suite for an SAP ERP programme, I built the automation framework in HP QTP (later UFT):

- **Keyword-driven design** for simpler UI test cases, then a **data-driven, modular, hybrid framework** for more complex flows.
- **Integration with HP ALM Test Lab**, so manual and automated tests were organised and run from one place, including parallel execution depending on the setup.
- **Automatic failure categorisation** (data issue, environment issue, flaky test, or genuine defect), which made it easy to identify real software issues.
- Separately, I built a **data-driven framework in about a day** with the same tools to support a data migration of about 50,000 records, which was completed, including validation of the migrated data, in a few hours.

That work used the tools of its time and had **no CI/CD pipeline**, and I had not worked hands-on with today's open-source stack. This project rebuilds the same disciplines with modern tools.

**My own comparison of old and new** (how I understand the concepts carried over):

| Earlier work (QTP/UFT and HP ALM) | This project |
|---|---|
| Keyword-driven test cases | Plain-English Cucumber steps backed by reusable step code |
| Data-driven and modular scripts | Scenario tables, shared Page Objects and a settings file |
| HP ALM Test Lab (run and organise tests) | GitHub Actions pipeline with a smoke gate, plus Allure reporting |
| Automatic failure categorisation | Rule-based failure triage (suspected product defect, test data, environment, flaky, test script), plus a redacted prompt that a person reviews before using an AI assistant. No AI model is integrated |

## 3. Goal and scope

**Goal:** design, build and run a maintainable automation framework covering web UI, API and mobile-viewport testing, executed automatically in a CI/CD pipeline with clear reporting.

**Scope and ground rules**
- The application under test is the **public Infor website** (read-only checks on public pages) and the **Restful-Booker** public practice API.
- No non-public information was used. The project is not affiliated with or endorsed by either site, and it submits no forms and generates no heavy traffic.
- I had not written code for about ten years, so I treated this as relearning: AI assistance for the first draft, then my own running, debugging and extending.

## 4. What I built

| Area | What it does |
|---|---|
| **Framework core** | Page Object Model, configuration-driven settings (website, browser, headless mode, timeouts), a thread-safe browser factory |
| **BDD layer** | Tests written in plain English (Cucumber `.feature` files) so non-technical readers can follow them |
| **Web UI tests** | Home page, header and footer, and key section pages of the public site |
| **API tests** | Rest Assured: GET, POST, PUT and DELETE, admin login, and negative cases (missing record, wrong credentials, delete without login) |
| **Mobile tests** | Chrome device emulation for Pixel 7, iPhone 12 Pro and iPad Air: screen size, mobile viewport tag, and sideways-scroll check |
| **Reporting and evidence** | Allure dashboard, screenshot on failure, API request and response attached to each API test, optional step screenshots and screen recording |
| **Speed** | Parallel execution (three browsers at once) |
| **Failure triage** | After every run, each failure is sorted into a category by simple rules, with the reason and a suggested next step, in a report file, in the Allure Categories tab and on the summary page of every pipeline run. A redacted prompt file (no tokens, passwords, emails, IP addresses or long IDs) is produced for AI-assisted analysis with a person in the loop |
| **CI/CD** | GitHub Actions pipeline: smoke tests run first as a quality gate; the full regression runs only if they pass; the Allure report is published automatically |

## 5. Results (measured)

- **20 automated scenarios**, all passing: 8 web UI, 9 API, 3 mobile (100% pass rate in the latest pipeline run).
- **Local UI-suite speed-up of about 38%** (about 84 s sequential to about 52 s with three parallel browsers).
- **Full suite of 20 scenarios completes in about 34 seconds** in the cloud pipeline (Linux), and the first end-to-end pipeline run took under two minutes.
- **Zero manual steps** from pushing a change to a published report.
- **Failure triage checked on four deliberately failing demo scenarios:** all four were sorted into the expected categories (2 suspected product defects, 1 test data, 1 environment).
- **Failure triage in the pipeline:** every run shows a triage report on its summary page (a clean run states "No failures to triage") and saves the report files with the run's evidence.

*Note: the local and cloud timings come from different machines and environments, so they are not a like-for-like comparison.*

## 6. Problems I hit and how I solved them

These are the part of the project I learned the most from.

1. **A test failed against the real site (404).** One of my guessed page addresses did not exist. The failure screenshot, captured automatically, showed a "page not found" screen. I replaced the guess with real addresses taken from the site's own menu. *Lesson: failure evidence turns a vague "test failed" into a one-minute diagnosis.*
2. **Four API tests failed at once.** The report showed a 418 response on the create-booking call; the other three failures were knock-on effects, because no booking was created, so there was nothing to read, update or delete. The cause was the request's `Accept` header: my library sent a long list of content types, but the service accepts only plain `application/json`. Setting the header explicitly fixed all four. *Lesson: find the first failure in a chain before touching the others.*
3. **Screen recordings would not play.** The default video format was not supported by Windows Media Player. Switching the recorder to a standard MJPG format fixed it.
4. **Environment setup issues.** Maven not found on the system path, and commands run from the wrong folder. Both were small but typical of real onboarding friction, and the README now documents the setup steps.
5. **Duplicate entries in Allure's Categories tab.** One failed check appeared under two categories, and an unreachable-address failure fell into Allure's default "Test defects". Allure files a result under every category whose pattern matches, and it reads only the error message text (which on Windows says "No such host is known"). I narrowed the product-defect pattern so it excludes failures other categories already claim, and added the missing message text. *Lesson: the same rules kept in two places (Java and Allure) can drift apart, so I check both against the same demo failures.*

## 7. Design decisions worth explaining

- **Page Objects keep locators out of tests**, so a page change means one edit, not many.
- **Config over code:** the website address, browser and timeouts live in one settings file.
- **Smoke gate before regression:** a broken build fails in seconds instead of wasting a full regression run.
- **Browser only where needed:** API scenarios skip the browser entirely, which keeps them fast.
- **Evidence by default:** every failure leaves a screenshot or request/response record.
- **Triage rules are suggestions:** the categories come from simple rules and a person confirms each one. The AI-ready prompt removes sensitive-looking values automatically, and nothing is sent anywhere automatically.

## 8. How this addresses the feedback

| Feedback / role expectation | Status |
|---|---|
| Selenium, Cucumber/BDD, Java automation framework design | **Done**, with working code and evidence |
| Test automation for API (Rest Assured) | **Done** |
| Mobile testing | **Partly done**: browser-based device emulation only. Appium and real devices are not covered |
| CI/CD pipeline design with quality gates | **Done on GitHub Actions** (executed). A GitLab CI file is written but not yet run |
| Docker | **Dockerfile written, not yet built or run** by me |
| Reporting and recording of test execution | **Done** (Allure, screenshots, optional video) |
| UFT and ALM-integrated frameworks | **Done earlier in my career** (Accenture, SAP regression), not part of this project |
| AI-driven automation (intelligent failure analysis, test optimisation) | **Partly done.** Rule-based failure triage and a redacted, human-reviewed prompt for AI assistants. No AI model is integrated, and test optimisation is not covered |
| UiPath RPA, Kubernetes/Helm, BrowserStack | **Not yet done** |

I would rather show exactly what is proven than overstate it. The gaps above are my learning roadmap.

## 9. How I would scale this in a team (design thinking, not delivered work)

This section describes how I would apply the approach with a team, drawing on my QA leadership background. It has **not** been delivered in a commercial setting.

- Publish the framework as a shared template so teams start from the same structure.
- Keep business-readable scenarios owned by testers and technical step code owned by automation engineers.
- Make the smoke gate a required check before merging, and track pass rate and run time as health metrics.
- Set rules for what AI-generated code and tests must pass (code review, running locally, no sensitive data sent to external tools) before they are accepted.

## 10. What I learned

- Building a framework again with modern tools showed me how much has changed since my earlier Accenture work (BDD tooling, API libraries, automated pipelines, rich reporting) and how much has stayed the same (clear structure, reusable components, trustworthy failure analysis).
- I can now read a framework's structure, spot where it will be fragile, and ask better questions of engineers.
- Failure triage only helps if people trust it. Showing the reason for each category, and keeping a person in the loop, matters more than the cleverness of the rules.
- AI assistance speeds up the first draft, but the learning and the reliability come from running, breaking and fixing the code.
- Good reporting and clear failure evidence matter as much as the tests themselves.

## 11. Next steps

1. Connect an AI model to the triage step, to write plain-English failure summaries automatically, keeping the rule on what data may be shared with an AI service. Add re-runs so "flaky" can be confirmed instead of suspected.
2. Execute and verify the Docker image and the GitLab CI pipeline.
3. Real-device cloud testing (BrowserStack) and Appium.
4. Explore Kubernetes/Helm-based test execution and UiPath RPA integration.

---

*This is an independent learning project. All tests run against public pages and a public practice API. Results shown are from my own runs.*
