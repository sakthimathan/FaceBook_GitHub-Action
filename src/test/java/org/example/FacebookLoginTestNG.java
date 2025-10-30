package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchEvent;
import java.nio.file.WatchService;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.NoSuchFileException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class FacebookLoginTestNG {

    private static String email;
    private static String password;

    private WebDriver driver;
    private WebDriverWait wait;

    // ExtentReports objects
    private static ExtentReports extent;
    private ExtentTest testReport;

    @BeforeClass(alwaysRun = true)
    public void checkCredentials() {
        email = System.getenv("FB_EMAIL");
        password = System.getenv("FB_PASSWORD");
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new SkipException("FB_EMAIL and/or FB_PASSWORD environment variables are not set. Skipping Facebook login test.");
        }
    }

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        try {
            Path reportsDir = Path.of("target", "extent-reports");
            Files.createDirectories(reportsDir);
            String reportPath = reportsDir.resolve("extent-report.html").toAbsolutePath().toString();
            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
            extent = new ExtentReports();
            extent.attachReporter(spark);
            System.out.println("Initialized ExtentReports at: " + reportPath);
        } catch (Exception e) {
            System.err.println("Failed to initialize ExtentReports: " + e.getMessage());
        }
    }


    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        // create an Extent test for this test method
        if (extent != null) {
            testReport = extent.createTest(method.getName());
        }
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless=new"); // uncomment for headless runs
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        if (testReport != null) {
            testReport.info("Launched ChromeDriver and created WebDriverWait");
        }
    }

    @Test(description = "Simple Facebook login using credentials from environment variables")
    public void testFacebookLogin() {
        driver.get("https://www.facebook.com/");
        if (testReport != null) testReport.info("Opened facebook.com");

        // Try to dismiss cookie banner / dialogs if present (best-effort)
        try {
            By cookiesBtn = By.xpath("//button[contains(.,'Allow essential and optional cookies') or contains(.,'Only allow essential cookies') or contains(.,'Allow all cookies') or contains(.,'Accept All')]");
            wait.withTimeout(Duration.ofSeconds(3)).until(ExpectedConditions.elementToBeClickable(cookiesBtn)).click();
        } catch (Exception ignored) {
        } finally {
            // reset wait
            wait.withTimeout(Duration.ofSeconds(20));
        }

        // Fill in credentials
        By emailLocator = By.id("email");
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailLocator)).sendKeys(email);
        if (testReport != null) testReport.info("Entered email");

        By passLocator = By.id("pass");
        driver.findElement(passLocator).sendKeys(password);
        if (testReport != null) testReport.info("Entered password");

        // Click login button
        By loginButton = By.name("login");
        try {
            wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
            if (testReport != null) testReport.info("Clicked login button");
        } catch (Exception e) {
            By alt = By.xpath("//button[@type='submit']");
            wait.until(ExpectedConditions.elementToBeClickable(alt)).click();
            if (testReport != null) testReport.info("Clicked fallback submit button");
        }

        // --- CAPTCHA / "I'm not a robot" detection ---
        // If a CAPTCHA (reCAPTCHA or similar) appears, create a sentinel file and pause so
        // you can solve it manually in the opened browser. Delete the sentinel file to continue.
        try {
            By captchaIframe = By.xpath("//iframe[contains(translate(@title,'RECAPTCHA','recaptcha'),'recaptcha') or contains(@src,'recaptcha')]");
            By captchaDiv = By.xpath("//div[contains(@class,'g-recaptcha') or contains(@id,'captcha') or contains(.,'I'm not a robot')]");
            boolean captchaPresent = !driver.findElements(captchaIframe).isEmpty() || !driver.findElements(captchaDiv).isEmpty();

            if (captchaPresent) {
                Path captchaSentinel = Path.of("target", "FB_CAPTCHA");
                try {
                    Files.createDirectories(captchaSentinel.getParent());
                    if (!Files.exists(captchaSentinel)) {
                        Files.createFile(captchaSentinel);
                    }
                    System.out.println("CAPTCHA detected — created sentinel: " + captchaSentinel.toAbsolutePath());
                    if (testReport != null) testReport.warning("CAPTCHA detected; waiting for manual solve. Sentinel: " + captchaSentinel.toAbsolutePath());
                    System.out.println("Please solve the CAPTCHA in the browser. Delete the file to continue and finish the test.");
                    waitForDeletion(captchaSentinel);
                    System.out.println("CAPTCHA sentinel removed — resuming test.");
                    if (testReport != null) testReport.info("CAPTCHA sentinel removed — resumed test");
                } catch (Exception ex) {
                    System.err.println("Failed to create captcha sentinel: " + ex.getMessage());
                }
            }
        } catch (Exception ignored) {
        }

        // Determine success: not on login page or checkpoint
        boolean loggedIn = false;
        try {
            // wait for either some profile-related element or URL change
            wait.withTimeout(Duration.ofSeconds(10)).until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("facebook.com"),
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@href,'/me') or contains(@href,'/profile.php')]") )
            ));

            String cur = driver.getCurrentUrl();
            if (cur != null && !cur.contains("login") && !cur.contains("checkpoint")) {
                loggedIn = true;
            }
        } catch (Exception e) {
            // keep default false
        }

        Assert.assertTrue(loggedIn, "Login not detected - possible wrong credentials, 2FA, or checkpoint. Current URL: " + driver.getCurrentUrl());
        if (testReport != null) {
            if (loggedIn) testReport.pass("Login detected. Current URL: " + driver.getCurrentUrl());
            else testReport.fail("Login not detected. Current URL: " + driver.getCurrentUrl());
        }
    }

    // Wait for the file to be deleted. Prefer using WatchService; fallback to polling if unavailable.
    private void waitForDeletion(Path sentinel) {
        // Read optional timeout in seconds from env var FB_CAPTCHA_TIMEOUT_SECONDS (or 0 for no timeout)
        long timeoutSeconds = 0;
        try {
            String t = System.getenv("FB_CAPTCHA_TIMEOUT_SECONDS");
            if (t != null && !t.isEmpty()) {
                timeoutSeconds = Long.parseLong(t);
            }
        } catch (Exception ignored) {
        }
        long deadline = timeoutSeconds > 0 ? System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(timeoutSeconds) : 0;
        try {
            Path dir = sentinel.getParent();
            try (WatchService ws = dir.getFileSystem().newWatchService()) {
                dir.register(ws, StandardWatchEventKinds.ENTRY_DELETE);
                // if file doesn't exist already, return immediately
                if (!Files.exists(sentinel)) {
                    return;
                }
                while (true) {
                    long pollMillis;
                    if (deadline > 0) {
                        long remaining = deadline - System.currentTimeMillis();
                        if (remaining <= 0) {
                            System.out.println("waitForDeletion: timeout reached waiting for " + sentinel);
                            return;
                        }
                        pollMillis = Math.min(remaining, 1000);
                    } else {
                        pollMillis = 1000;
                    }
                    WatchKey key = ws.poll(pollMillis, TimeUnit.MILLISECONDS);
                    if (key == null) {
                        // timed out waiting for events; loop to check deadline or continue polling
                        continue;
                    }
                    for (WatchEvent<?> ev : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = ev.kind();
                        if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                            Object ctx = ev.context();
                            if (ctx != null && ctx.toString().equals(sentinel.getFileName().toString())) {
                                return;
                            }
                        }
                    }
                    if (!key.reset()) {
                        break;
                    }
                }
            }
        } catch (NoSuchFileException nsf) {
            // directory doesn't exist; nothing to wait for
        } catch (Exception e) {
            // fallback polling
            try {
                while (Files.exists(sentinel)) {
                    TimeUnit.MILLISECONDS.sleep(500);
                    // honor timeout if set
                    if (deadline > 0 && System.currentTimeMillis() > deadline) {
                        System.out.println("waitForDeletion (fallback): timeout reached waiting for " + sentinel);
                        break;
                    }
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            if (driver instanceof TakesScreenshot) {
                TakesScreenshot ts = (TakesScreenshot) driver;
                File src = ts.getScreenshotAs(OutputType.FILE);
                Path dst = Path.of("target", "screenshots", result.getName() + ".png");
                Files.createDirectories(dst.getParent());
                Files.copy(src.toPath(), dst);
                System.out.println("Saved screenshot to: " + dst.toAbsolutePath());
                if (testReport != null) {
                    String rel = dst.toString();
                    if (!result.isSuccess()) {
                        testReport.fail(result.getThrowable(), MediaEntityBuilder.createScreenCaptureFromPath(rel).build());
                    } else {
                        testReport.pass("Test passed", MediaEntityBuilder.createScreenCaptureFromPath(rel).build());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
        } finally {
            if (driver != null) {
                // Allow tests to keep the browser open for debugging when requested.
                // Use either system property -DkeepBrowserOpen=true or environment variable KEEP_BROWSER_OPEN=true
                String keepProp = System.getProperty("keepBrowserOpen");
                if (keepProp == null) {
                    keepProp = System.getenv("KEEP_BROWSER_OPEN");
                }
                boolean keepOpen = keepProp != null && keepProp.equalsIgnoreCase("true");

                if (keepOpen) {
                    System.out.println("KEEP_BROWSER_OPEN is true — leaving browser open for debugging.");
                    System.out.println("Browser URL: " + driver.getCurrentUrl());
                    // Create a sentinel file that keeps the browser open while it exists.
                    // Delete the file to allow the test to continue and close the browser.
                    Path sentinel = Path.of("target", "KEEP_BROWSER_OPEN");
                    try {
                        Files.createDirectories(sentinel.getParent());
                        if (!Files.exists(sentinel)) {
                            Files.createFile(sentinel);
                        }
                        System.out.println("Created sentinel file: " + sentinel.toAbsolutePath());
                        System.out.println("Delete the file to close the browser and let the test finish.");
                        waitForDeletion(sentinel);
                        System.out.println("Sentinel removed — continuing test cleanup.");
                    } catch (Exception ignored) {
                    }
                }

                try {
                    driver.quit();
                } catch (Exception ignored) {
                }
            }
        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        if (extent != null) {
            extent.flush();
            System.out.println("Flushed ExtentReports");
        }
    }

}
