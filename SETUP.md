# Setup Instructions (do this tonight)

## 1. Create GitHub repo

Go to https://github.com/new
- Name: `blackduck-demo`
- Visibility: **Private**
- Do NOT initialize with README

## 2. Push code to GitHub

Open a terminal in the `blackduck-demo` folder and run:

```bash
git init
git add .
git commit -m "Initial vulnerable app - demo setup"
git branch -M main
git remote add origin https://github.com/wernerob/blackduck-demo.git
git push -u origin main
```

## 3. Add GitHub Secrets

Go to: https://github.com/wernerob/blackduck-demo/settings/secrets/actions

Add these two secrets (click "New repository secret" for each):

| Name | Value |
|------|-------|
| `POLARIS_URL` | `https://eu.polaris.blackduck.com` |
| `POLARIS_ACCESS_TOKEN` | *(your Polaris access token)* |

## 4. Enable GitHub Container Registry (GHCR)

Go to: https://github.com/settings/tokens
- Not needed — GHCR uses the built-in `GITHUB_TOKEN`, already in the workflow.

Go to your repo → Settings → Actions → General
- Under "Workflow permissions" → select **"Read and write permissions"**
- Check "Allow GitHub Actions to create and approve pull requests"
- Click Save

## 5. Trigger the first (failing) run

The push in step 2 already triggered the pipeline.
Go to: https://github.com/wernerob/blackduck-demo/actions

Wait for it to fail at Phase 2 (SAST+SCA). This is expected.

## 6. Pre-load Polaris with results before the demo

Run the pipeline tonight so results are already in Polaris UI when you demo.
You want:
- 1 failed run (with findings) already in Polaris
- 1 green run ready to show

## 7. Fix and re-run (for the green pipeline)

### Fix SAST — edit UserController.java line ~25:
```java
// Change FROM:
String query = "SELECT * FROM users WHERE name = '" + name + "'";
return jdbcTemplate.queryForList(query);

// Change TO:
return jdbcTemplate.queryForList("SELECT * FROM users WHERE name = ?", name);
```

### Fix SCA — edit pom.xml lines ~35-36:
```xml
<!-- Change FROM: -->
<version>2.14.1</version>
...
<version>2.14.1</version>

<!-- Change TO: -->
<version>2.17.2</version>
...
<version>2.17.2</version>
```

Then commit and push — this triggers the green run.

## 8. Verify Polaris DAST is configured

Log into https://eu.polaris.blackduck.com
- Go to DAST settings
- Make sure a scan config exists for project "blackduck-demo"
- If not, create one with target URL: http://localhost:8080

---

## Troubleshooting

**Pipeline fails at container scan with auth error:**
→ Check "Workflow permissions" is set to Read+Write in repo settings

**Polaris scan says "project not found":**
→ The first run creates the project automatically — check Polaris UI

**DAST scan times out:**
→ DAST scans can take 20-40 min. For the demo, trigger it tonight and show cached results.
