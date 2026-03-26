# BlackDuck Security Pipeline — Demo Script

**Audience:** DevOps Engineers
**Duration:** ~30 minutes
**Flow:** Break → Fix SAST → Break again → Fix SCA → Full green pipeline → DAST results

---

## Setup (before demo starts)

1. Have **Polaris UI** open in browser (eu.polaris.blackduck.com)
2. Have **GitHub Actions** tab open on the repo
3. Have **VS Code / IntelliJ** open with `UserController.java` and `pom.xml` visible
4. Have a **failed pipeline run** already done (so results are pre-loaded in Polaris)

---

## Demo Flow

---

### STEP 1 — Show the app (1 min)

> "This is a simple Java Spring Boot REST API. Think of it as any typical enterprise Java service a dev team would build. We have some endpoints, it uses a database. Nothing special — but it has two issues baked in that we'll find."

Point to:
- `UserController.java` → `getUsers()` method
- `pom.xml` → log4j dependency (don't reveal the version yet)

---

### STEP 2 — Trigger the pipeline / show the broken run (3 min)

> "The developer commits their code and pushes. GitHub Actions picks it up automatically. Let's look at what happens."

**Show GitHub Actions — first failing run**

Point out the 4 phases in the pipeline:
- Phase 1: Maven Build ✅
- Phase 2: SAST + SCA ❌ ← stops here

> "Black Duck Polaris integrates directly into the pipeline. No separate tool to run, no manual step. The developer just pushes and the platform does its job."

---

### STEP 3 — SAST Result: SQL Injection (3 min)

Go to **Polaris UI → Issues → SAST tab**

> "Coverity found a SQL Injection vulnerability. CWE-89. This is a classic code-level flaw — the developer is concatenating user input directly into a SQL query. An attacker could manipulate this to dump the entire database."

Point to the code snippet in Polaris showing the vulnerable line.

> "The build broke. The code never made it to a container, never hit production. That's exactly what we want."

**Show the fix in IntelliJ/VS Code:**

Change `UserController.java` line:
```java
// BEFORE (vulnerable):
String query = "SELECT * FROM users WHERE name = '" + name + "'";
return jdbcTemplate.queryForList(query);

// AFTER (fixed):
return jdbcTemplate.queryForList("SELECT * FROM users WHERE name = ?", name);
```

---

### STEP 4 — SAST passes, SCA fails (3 min)

> "Developer commits the fix, pipeline runs again. SAST passes this time. But now SCA catches something different."

**Show GitHub Actions — second run, Phase 2 fails on SCA**

Go to **Polaris UI → Issues → SCA tab**

> "Black Duck found Log4Shell. CVE-2021-44228. CVSS score: 10.0 — the maximum. This is the most famous Java vulnerability of the last decade. The developer was using log4j version 2.14.1 — the version that's vulnerable."

Show the component in Polaris — version, CVE, CVSS score, affected versions, fix version.

> "Notice that Black Duck also shows us the SBOM here — the complete Software Bill of Materials. Every dependency, every transitive dependency, all the licenses. This was generated automatically. No extra step."

**Show the fix:**

Change `pom.xml`:
```xml
<!-- BEFORE: -->
<version>2.14.1</version>

<!-- AFTER: -->
<version>2.17.2</version>
```

---

### STEP 5 — SAST + SCA both pass, container scan runs (4 min)

> "Developer fixes the dependency version. Now let's watch the full pipeline go green."

**Show GitHub Actions — third run:**
- Phase 1: Build ✅
- Phase 2: SAST + SCA ✅
- Phase 3: Docker Build + Container Scan ✅

> "After the source code is clean, we build a Docker image. Black Duck then scans the container image itself — not just the application code, but the base OS layer, system packages, everything inside the image."

Go to **Polaris UI → Container scan results**

> "This catches things the source scan can't see — OS-level vulnerabilities in the base image, packages added by the Dockerfile, things that might have been introduced in the build process."

> "Notice the SBOM was generated for the container as well — CycloneDX format, ready for your supply chain compliance team."

---

### STEP 6 — DAST Scan (5 min)

> "Now we have a running container. The last phase starts the container in the CI environment and points Polaris DAST at it — this is runtime scanning. Different from static analysis."

**Show GitHub Actions — Phase 4 DAST running**

Go to **Polaris UI → DAST tab**

> "DAST actually sends HTTP requests to the running application. It crawls the API, tests inputs, looks for runtime vulnerabilities — things you simply cannot find by reading source code. Authentication flaws, session handling issues, injection points that only appear at runtime."

Show DAST findings if available.

> "And this was triggered automatically from the same pipeline — no separate process, no manual scan scheduled somewhere. The same commit that fixes the code also triggers a runtime test."

---

### STEP 7 — AI Signal (3 min)

Go to **Polaris UI → any vulnerability → AI Signal panel**

> "This is Black Duck's AI Signal. It's not just telling you 'this CVE exists.' It's telling you whether this specific vulnerability is actually reachable in your code, whether there's a known exploit in the wild, and how urgently you need to act."

Point to the AI Signal score/indicators:

> "For a DevOps team, this means you're not chasing every medium CVE in your backlog. You're focusing on what actually matters. Prioritization built into the tool."

---

### STEP 8 — Summary (2 min)

> "So what did we just see?"

- Source code commit → **SAST** catches SQL Injection → build breaks
- Dev fixes code → **SCA** catches Log4Shell → build breaks again
- Dev fixes dependency → pipeline goes green
- **Container image scanned** → SBOM generated automatically
- **DAST** triggered against live container → runtime findings
- **AI Signal** prioritizes what to fix first

> "This whole flow runs on every commit. No human approvals, no scheduled scans, no 'we'll do a security review before release.' Security is part of the pipeline."

---

## If asked about Jenkins / Azure DevOps

> "The same Synopsys Action we used for GitHub Actions has equivalents for Jenkins and Azure DevOps. It's the same Polaris backend — just a different trigger. The results all land in the same Polaris dashboard regardless of which CI system triggered the scan."

## If asked about binary scanning

> "Black Duck can also scan compiled binaries and Docker layers that come from third parties — not just your own source code. That's especially useful if you consume pre-built artifacts or commercial off-the-shelf software. That's a separate scan mode we can cover in a follow-up."

## If asked about on-prem

> "Everything we showed today is Polaris SaaS — nothing to install, nothing to maintain. If there's a requirement for on-prem, Black Duck also offers a self-hosted deployment with the same feature set."
