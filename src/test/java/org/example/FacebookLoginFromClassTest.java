package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;

import java.nio.file.Path;
import java.time.Duration;

public class FacebookLoginFromClassTest {

    private WebDriver driver;
    private FacebookLogin fb;

    private static ExtentReports extent;
    private ExtentTest testReport;

    // Dummy credentials (do not use real ones)
    private static final String DUMMY_EMAIL = "dummy.email@example.com";
    private static final String DUMMY_PASSWORD = "FakePassword123";

    @BeforeClass
    public void setupClass() {
        WebDriverManager.chromedriver().setup();
        extent = ExtentManager.getExtent();
    }

    @BeforeMethod
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // enable for CI
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        fb = new FacebookLogin(driver);
    }

    @Test
    public void testLoginUsingPageObject() throws InterruptedException {
        testReport = extent.createTest("testLoginUsingPageObject");

        fb.open();
        fb.acceptCookiesIfPresent();
        fb.login(DUMMY_EMAIL, DUMMY_PASSWORD);

        // after attempting login with dummy creds we expect to still be on login page
        boolean loginFormPresent = fb.isLoginFormPresent();
        if (loginFormPresent) {
            testReport.pass("Login form still present as expected after dummy login attempt");
        } else {
            testReport.fail("Login form not present (unexpected)");
        }
        Assert.assertTrue(loginFormPresent, "Expected to still be on login page after dummy login attempt.");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        try {
            if (!result.isSuccess()) {
                Path shot = ScreenshotUtil.capture(driver, result.getName());
                if (shot != null) {
                    testReport.fail(result.getThrowable(), MediaEntityBuilder.createScreenCaptureFromPath(shot.toString()).build());
                } else {
                    testReport.fail(result.getThrowable());
                }
            } else {
                Path shot = ScreenshotUtil.capture(driver, result.getName());
                if (shot != null) testReport.pass("Passed", MediaEntityBuilder.createScreenCaptureFromPath(shot.toString()).build());
                else testReport.pass("Passed");
            }
        } catch (Exception e) {
            System.err.println("Error during teardown reporting: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    @AfterClass
    public void afterClass() {
        ExtentManager.flush();
    }
}
