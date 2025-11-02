package com.utility;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import java.io.File;

public class ExtentTestNGListener implements ITestListener {

    private static ExtentReports extent = ExtentManager.getExtentReports();
    private static ThreadLocal<ExtentTest> testThread = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        // nothing
    }

    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentTest test = extent.createTest(testName);
        testThread.set(test);
        test.log(Status.INFO, "Test started: " + testName);

        // take screenshot at start of test
        try {
            String path = UtilityClass.saveScreenshot(result.getMethod().getMethodName() + "_start");
            if (path != null) {
                test.addScreenCaptureFromPath(path);
            }
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = testThread.get();
        test.log(Status.PASS, "Test passed");
        try {
            String path = UtilityClass.saveScreenshot(result.getMethod().getMethodName() + "_success");
            if (path != null) {
                test.addScreenCaptureFromPath(path);
            }
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = testThread.get();
        test.log(Status.FAIL, result.getThrowable());
        try {
            String path = UtilityClass.saveScreenshot(result.getMethod().getMethodName() + "_failure");
            if (path != null) {
                test.addScreenCaptureFromPath(path);
            }
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = testThread.get();
        test.log(Status.SKIP, "Test skipped");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // nothing
    }
}

