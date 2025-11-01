package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Simple page-object wrapper for Facebook login actions.
 * Use this class from tests; it does not manage driver lifecycle.
 */
public class FacebookLogin {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public FacebookLogin(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void open() {
        driver.get("https://www.facebook.com/");
    }

    public void acceptCookiesIfPresent() {
        try {
            By cookiesBtn = By.xpath("//button[contains(.,'Allow essential and optional cookies') or contains(.,'Only allow essential cookies') or contains(.,'Allow all cookies') or contains(.,'Accept All')]");
            wait.withTimeout(Duration.ofSeconds(3)).until(ExpectedConditions.elementToBeClickable(cookiesBtn)).click();
        } catch (Exception ignored) {
        } finally {
            // restore wait timeout
            wait.withTimeout(Duration.ofSeconds(20));
        }
    }







    public void login(String email, String password) throws InterruptedException {
        By emailLocator = By.id("email");
        By passLocator = By.id("pass");
        By loginButton = By.name("login");

        WebElement emailField = wait.until(ExpectedConditions.elementToBeClickable(emailLocator));
        emailField.clear();
        emailField.sendKeys(email);

        WebElement passField = wait.until(ExpectedConditions.elementToBeClickable(passLocator));
        passField.clear();
        passField.sendKeys(password);

        Thread.sleep(10000);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
        } catch (Exception e) {
            // fallback to any submit button
            By alt = By.xpath("//button[@type='submit']");
            wait.until(ExpectedConditions.elementToBeClickable(alt)).click();
        }
    }

    /**
     * Returns true when the login form is present (indicating login failed / still on login page).
     */
    public boolean isLoginFormPresent() {
        try {
            return !driver.findElements(By.id("login_form")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public String currentUrl() {
        try {
            return driver.getCurrentUrl();
        } catch (Exception e) {
            return null;
        }
    }
}

