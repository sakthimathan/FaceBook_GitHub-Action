@echo off
REM Create branch, add files, commit, and push to origin (run in cmd.exe)
set BRANCH=feat/extent-report-screenshots

echo Creating and switching to branch %BRANCH%...
git checkout -b %BRANCH%
if ERRORLEVEL 1 (
  echo Failed to create branch. Is this a git repository and are there local changes?& exit /b 1
)

echo Adding files...
git add src\test\java\org\example\ExtentManager.java src\test\java\org\example\ScreenshotUtil.java src\test\java\org\example\FacebookLoginFromClassTest.java src\test\java\org\example\FacebookLoginTestNG.java
if ERRORLEVEL 1 (
  echo git add failed. Verify the file paths exist.& exit /b 1
)

echo Committing...
git commit -m "Add ExtentReports manager and screenshot util; integrate report + screenshots into tests"
if ERRORLEVEL 1 (
  echo git commit failed (maybe no changes to commit).& REM continue
)

echo Pushing to origin/%BRANCH%...
git push -u origin %BRANCH%
if ERRORLEVEL 1 (
  echo git push failed. You may need to set up remotes or credentials.& exit /b 1
)

echo Done. Open GitHub and create a PR from %BRANCH% into your target branch (or CI will run on push if configured).
pause

