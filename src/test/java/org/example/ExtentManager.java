package org.example;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Simple singleton ExtentReports manager for tests.
 */
public class ExtentManager {

    private static ExtentReports extent;

    public static synchronized ExtentReports getExtent() {
        if (extent == null) {
            try {
                Path reportsDir = Path.of("test-output");
                Files.createDirectories(reportsDir);
                String reportPath = reportsDir.resolve("extent-report.html").toAbsolutePath().toString();
                ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
                extent = new ExtentReports();
                extent.attachReporter(spark);
                System.out.println("Initialized ExtentReports at: " + reportPath);
            } catch (Exception e) {
                System.err.println("Failed to initialize ExtentReports: " + e.getMessage());
                extent = new ExtentReports();
            }
        }
        return extent;
    }

    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}

