package com.utility;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExtentManager {

    private static ExtentReports extent;

    public synchronized static ExtentReports getExtentReports() {
        if (extent == null) {
            try {
                Path outDir = Paths.get("test-output");
                if (!Files.exists(outDir)) {
                    Files.createDirectories(outDir);
                }
                String reportPath = outDir.resolve("extent-report.html").toAbsolutePath().toString();

                ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
                spark.config().setDocumentTitle("Adactin Test Report");
                spark.config().setReportName("Adactin Test Automation");
                spark.config().setTheme(Theme.STANDARD);

                extent = new ExtentReports();
                extent.attachReporter(spark);
                extent.setSystemInfo("OS", System.getProperty("os.name"));
                extent.setSystemInfo("Java", System.getProperty("java.version"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return extent;
    }
}
