package org.example;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class ScreenshotUtil {

    public static Path capture(WebDriver driver, String name) {
        try {
            if (driver == null) return null;
            if (!(driver instanceof TakesScreenshot)) return null;
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path dst = Path.of("screenshots", name + ".png");
            Files.createDirectories(dst.getParent());
            Files.copy(src.toPath(), dst);
            System.out.println("Saved screenshot to: " + dst.toAbsolutePath());
            return dst;
        } catch (Exception e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }
}

