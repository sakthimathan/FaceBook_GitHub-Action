package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class FacebookLoginFromClassTest {

    private WebDriver driver;
    private FacebookLogin fb;

    // Dummy credentials (do not use real ones)
    private static final String DUMMY_EMAIL = "dummy.email@example.com";
    private static final String DUMMY_PASSWORD = "FakePassword123";

    @BeforeClass
    public void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeMethod
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless=new"); // enable for CI
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        fb = new FacebookLogin(driver);
    }

    @Test
    public void testLoginUsingPageObject() throws InterruptedException {
        fb.open();
        fb.acceptCookiesIfPresent();
        fb.login(DUMMY_EMAIL, DUMMY_PASSWORD);

        // after attempting login with dummy creds we expect to still be on login page
        boolean loginFormPresent = fb.isLoginFormPresent();
        Assert.assertTrue(loginFormPresent, "Expected to still be on login page after dummy login attempt.");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

