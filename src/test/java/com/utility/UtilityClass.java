package com.utility;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverInfo;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverInfo;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import io.github.bonigarcia.wdm.WebDriverManager;

public class UtilityClass {

	public static WebDriver driver ;

	// Backward-compatible overload: default to non-CICD (interactive) mode
	public void switchDrivers(String Browser) {
		// Delegate to the two-argument method with cicd=false
		switchDrivers(Browser, false);
	}

	// Updated switchDrivers to accept a CICD flag and delegate to helper methods
	public void switchDrivers(String Browser, boolean cicd) {
		// Handle null/empty and be case-insensitive so TestNG parameters like "chrome" or "CHROME" both work
		if (Browser == null || Browser.trim().isEmpty()) {
			System.out.println("BROWSER NOT SELECTED");
			return;
		}
		String b = Browser.trim();
		if (b.equalsIgnoreCase("chrome") || b.equalsIgnoreCase("googlechrome")) {
			if (cicd) {
				launchChromeCICD();
			} else {
				launchChrome();
			}
		} else if (b.equalsIgnoreCase("firefox") || b.equalsIgnoreCase("mozilla")) {
			if (cicd) {
				launchFirefoxCICD();
			} else {
				launchFirefox();
			}
		} else if (b.equalsIgnoreCase("edge") || b.equalsIgnoreCase("msedge") || b.equalsIgnoreCase("microsoftedge")) {
			if (cicd) {
				launchEdgeCICD();
			} else {
				launchEdge();
			}
		} else {
			System.out.println("BROWSER NOT SELECTED: " + Browser);
		}
	}

	// Launch Chrome in normal (interactive) mode
	public void launchChrome() {
		// Ensure driver binary is available
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
	}

	// Launch Chrome configured for CI (headless + common flags)
	public void launchChromeCICD() {
		WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--display=:99");
		driver = new ChromeDriver(options);
	}

	// Launch Firefox in normal (interactive) mode
	public void launchFirefox() {
		WebDriverManager.firefoxdriver().setup();
		driver = new FirefoxDriver();
	}

	// Launch Firefox configured for CI (headless)
	public void launchFirefoxCICD() {
		WebDriverManager.firefoxdriver().setup();
		FirefoxOptions fo = new FirefoxOptions();
		fo.addArguments("-headless");
		driver = new FirefoxDriver(fo);
	}

	// Launch Edge in normal (interactive) mode
	public void launchEdge() {
		// 1) Prefer an explicit system property if set: webdriver.edge.driver
		String propPath = System.getProperty("webdriver.edge.driver");
		if (propPath != null && !propPath.isEmpty()) {
			java.io.File f = new java.io.File(propPath);
			if (f.exists()) {
				System.out.println("Using Edge driver from system property webdriver.edge.driver: " + propPath);
				driver = new EdgeDriver();
				return;
			}
		}
		// 2) Next, look for an environment variable EDGE_DRIVER_PATH
		String envPath = System.getenv("EDGE_DRIVER_PATH");
		if (envPath != null && !envPath.isEmpty()) {
			java.io.File f = new java.io.File(envPath);
			if (f.exists()) {
				System.setProperty("webdriver.edge.driver", envPath);
				System.out.println("Using Edge driver from env EDGE_DRIVER_PATH: " + envPath);
				driver = new EdgeDriver();
				return;
			}
		}
		// 3) Try WebDriverManager to download/setup the driver
		try {
			WebDriverManager.edgedriver().setup();
			driver = new EdgeDriver();
			return;
		} catch (Exception e) {
			// 4) If that fails, log a helpful message and fall back to Chrome
			System.err.println("[WARN] Unable to setup Edge driver via WebDriverManager: " + e.getMessage());
			System.err.println("[WARN] To run Edge, either: (a) ensure the runner has network access so WebDriverManager can download msedgedriver, or (b) download the matching msedgedriver and set system property 'webdriver.edge.driver' or env 'EDGE_DRIVER_PATH' to its path.");
			System.err.println("[WARN] Falling back to Chrome interactive mode.");
			try {
				launchChrome();
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new RuntimeException("Failed to launch Edge and fallback to Chrome also failed", ex);
			}
		}
	}

	// Launch Edge configured for CI (headless)
	public void launchEdgeCICD() {
		// 1) Prefer an explicit system property if set: webdriver.edge.driver
		String propPath = System.getProperty("webdriver.edge.driver");
		if (propPath != null && !propPath.isEmpty()) {
			java.io.File f = new java.io.File(propPath);
			if (f.exists()) {
				System.out.println("Using Edge driver from system property webdriver.edge.driver: " + propPath);
				EdgeOptions eo = new EdgeOptions();
				eo.addArguments("--headless=new");
				eo.addArguments("--disable-gpu");
				eo.addArguments("--window-size=1920,1080");
				eo.addArguments("--no-sandbox");
				driver = new EdgeDriver(eo);
				return;
			}
		}
		// 2) Next, look for an environment variable EDGE_DRIVER_PATH
		String envPath = System.getenv("EDGE_DRIVER_PATH");
		if (envPath != null && !envPath.isEmpty()) {
			java.io.File f = new java.io.File(envPath);
			if (f.exists()) {
				System.setProperty("webdriver.edge.driver", envPath);
				System.out.println("Using Edge driver from env EDGE_DRIVER_PATH: " + envPath);
				EdgeOptions eo = new EdgeOptions();
				eo.addArguments("--headless=new");
				eo.addArguments("--disable-gpu");
				eo.addArguments("--window-size=1920,1080");
				eo.addArguments("--no-sandbox");
				driver = new EdgeDriver(eo);
				return;
			}
		}
		// 3) Try WebDriverManager to download/setup the driver
		try {
			WebDriverManager.edgedriver().setup();
			EdgeOptions eo = new EdgeOptions();
			// Edge uses Chromium under the hood; use similar headless flags
			eo.addArguments("--headless=new");
			eo.addArguments("--disable-gpu");
			eo.addArguments("--window-size=1920,1080");
			eo.addArguments("--no-sandbox");
			driver = new EdgeDriver(eo);
			return;
		} catch (Exception e) {
			// 4) If that fails, log a helpful message and fall back to Chrome headless
			System.err.println("[WARN] Unable to setup Edge driver via WebDriverManager: " + e.getMessage());
			System.err.println("[WARN] To run Edge, either: (a) ensure the runner has network access so WebDriverManager can download msedgedriver, or (b) download the matching msedgedriver and set system property 'webdriver.edge.driver' or env 'EDGE_DRIVER_PATH' to its path.");
			System.err.println("[WARN] Falling back to Chrome CICD (headless) mode.");
			try {
				launchChromeCICD();
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new RuntimeException("Failed to launch EdgeCICD and fallback to ChromeCICD also failed", ex);
			}
		}
	}

	public static void getDriver() {

		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
	}

	public static void getUrl(String url) {
		driver.get(url);
	}

	public String currentUrlLaunch() {
		String currentUrl = driver.getCurrentUrl();
		return currentUrl;
	}

	public String GettingTitle() {
		String title = driver.getTitle();
		return title;
	}

	public void maximizeWindow() {
		driver.manage().window().maximize();
	}

	public static void closeTheCurrentWindow() {
		driver.close();
	}

	public void closeAllWindow() {
		driver.quit();
	}

	public void navigateToWebpage(String url) {
		driver.navigate().to(url);
	}

	public void navigateForward() {
		driver.navigate().forward();
	}

	public void navigateBackward() {
		driver.navigate().back();
	}

	public void toRefresh() {
		driver.navigate().refresh();
	}

	public Alert alertHandle() {
		Alert alert = driver.switchTo().alert();
		return alert;
	}

	public void acceptAlert() {
		driver.switchTo().alert().accept();
	}

	public void dismissAlert() {
		driver.switchTo().alert().dismiss();
	}

	public String getTextFromAlert() {
		String text = driver.switchTo().alert().getText();
		return text;
	}

	public void sendKeysInAlert(String keysToSend) {
		driver.switchTo().alert().sendKeys(keysToSend);
	}

	public void childWindow(String handle) {
		driver.switchTo().window(handle);
	}

	public void switchToFrameByIndex(int index) {
		driver.switchTo().frame(index);
	}

	public void switchToFrameByText(String name) {
		driver.switchTo().frame(name);
	}

	public void switchToFrameByWebElement(WebElement frameElement) {
		driver.switchTo().frame(frameElement);
	}

	public void switchFromFrame() {
		driver.switchTo().defaultContent();
	}

	public void switchToParentFrame() {
		driver.switchTo().parentFrame();
	}

	public String windowHandle() {
		String windowHandle = driver.getWindowHandle();
		return windowHandle;
	}

	public Set<String> WindowHandles() {
		Set<String> windowHandles = driver.getWindowHandles();
		return windowHandles;
	}

	public WebElement findElementById(String attributeValue) {
		WebElement findElement = driver.findElement(By.id(attributeValue));
		return findElement;
	}

	public WebElement findElementByName(String attributeValue) {
		WebElement findElement = driver.findElement(By.name(attributeValue));
		return findElement;
	}

	public WebElement findElementByXpath(String attributeValue) {
		WebElement findElement = driver.findElement(By.xpath(attributeValue));
		return findElement;
	}

	public WebElement findElementByClassName(String className) {
		WebElement findElement = driver.findElement(By.className(className));
		return findElement;
	}

	public WebElement findElementByTagName(String tagName) {
		WebElement findElement = driver.findElement(By.tagName(tagName));
		return findElement;
	}

	public WebElement findByCssSelector(String cssSelector) {
		WebElement findElement = driver.findElement(By.cssSelector(cssSelector));
		return findElement;
	}

	public WebElement findByLinkText(String linkText) {
		WebElement findElement = driver.findElement(By.linkText(linkText));
		return findElement;
	}

	public WebElement findByPartialLinkText(String partialLinkText) {
		WebElement findElement = driver.findElement(By.partialLinkText(partialLinkText));
		return findElement;
	}

	public List<WebElement> forWebTable(String attributeValue) {
		List<WebElement> findElements = driver.findElements(By.id(attributeValue));
		return findElements;
	}

	public void sendKeys(WebElement element, String data) {
		element.sendKeys(data);
	}

	public void click(WebElement element) {
		element.click();
	}

	public void clear(WebElement element) {
		element.clear();
	}

	public String getAttribute(WebElement element, String text) {
		String attribute = element.getAttribute(text);
		return attribute;
	}

	public String getText(WebElement element) {
		String text = element.getText();
		return text;
	}

	public void sendKeysByJse(String data, WebElement element) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].setAttribute('value','" + data + "')", element);
	}

	public void clickByJse(WebElement element) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].click", element);
	}

	public Object getAttributeByJse(WebElement element) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		Object script = jse.executeScript("returnarguments[0].getAttribute('value')", element);
		return script;
	}

	public void scrollDownByJse(WebElement element) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].scrollIntoView(true)", element);
	}

	public void scrollUpByJse(WebElement element) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].scrollIntoView(false)", element);
	}

	public File takingScreenShot() {
		TakesScreenshot ts = (TakesScreenshot) driver;
		File screenshotAs = ts.getScreenshotAs(OutputType.FILE);
		return screenshotAs;
	}

	/**
	 * Save a screenshot to test-output/screenshots with the given base name and timestamp.
	 * Returns the absolute path to the saved file or null if failed.
	 */
	public static String saveScreenshot(String baseName) {
		try {
			if (driver == null) {
				System.out.println("saveScreenshot: driver is null");
				return null;
			}
			File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			java.nio.file.Path screenshotsDir = java.nio.file.Paths.get("test-output", "screenshots");
			if (!java.nio.file.Files.exists(screenshotsDir)) {
				java.nio.file.Files.createDirectories(screenshotsDir);
			}
			String filename = baseName + "_" + System.currentTimeMillis() + ".png";
			File dest = screenshotsDir.resolve(filename).toFile();
			org.apache.commons.io.FileUtils.copyFile(src, dest);
			return dest.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void mouseOverAction(WebElement element) {
		Actions acc = new Actions(driver);
		acc.moveToElement(element).perform();
	}

	public void rightClick(WebElement element) {
		Actions acc = new Actions(driver);
		acc.contextClick(element).perform();
	}

	public void doubleClick(WebElement element) {
		Actions acc = new Actions(driver);
		acc.doubleClick(element).perform();
	}

	public void dragAndDrop(WebElement element, WebElement element1) {
		Actions acc = new Actions(driver);
		acc.dragAndDrop(element, element1).perform();
	}

	public void selectOptionByIndex(WebElement element, int index) {
		Select s = new Select(element);
		s.selectByIndex(index);
	}

	public void selectOptionByValue(WebElement element, String attributeValue) {
		Select s = new Select(element);
		s.selectByValue(attributeValue);
	}

	public void selectOptionByText(WebElement element, String text) {
		Select s = new Select(element);
		s.selectByVisibleText(text);
	}

	public void deselectOptionByIndex(WebElement element, int index) {
		Select s = new Select(element);
		s.deselectByIndex(index);
	}

	public void deselectOptionByValue(WebElement element, String attributeValue) {
		Select s = new Select(element);
		s.deselectByValue(attributeValue);
	}

	public void deselectOptionByText(WebElement element, String text) {
		Select s = new Select(element);
		s.deselectByVisibleText(text);
	}

	public void deselectAll(WebElement element) {
		Select s = new Select(element);
		s.deselectAll();
	}

	public boolean isMultipleSelect(WebElement element) {
		Select s = new Select(element);
		boolean multiple = s.isMultiple();
		return multiple;
	}

	public List<WebElement> getOptionsInSelect(WebElement element) {
		Select s = new Select(element);
		List<WebElement> options = s.getOptions();
		return options;
	}

	public WebElement allOptionsInSelect(WebElement element) {
		Select s = new Select(element);
		WebElement firstSelectedOption = s.getFirstSelectedOption();
		return firstSelectedOption;
	}

	/*
	 * public Sheet createSheet(String path, String name) throws IOException { File
	 * file = new File(path); FileInputStream fis = new FileInputStream(file);
	 * Workbook book = new XSSFWorkbook(fis); Sheet createSheet =
	 * book.createSheet(name); return createSheet; }
	 * 
	 * public Sheet getSheet(String path, String name) throws IOException { File
	 * file = new File(path); FileInputStream fis = new FileInputStream(file);
	 * Workbook book = new XSSFWorkbook(fis); Sheet sheet = book.getSheet(name);
	 * return sheet; }
	 * 
	 * public Row createRow(String path, String name, int rowIndex) throws
	 * IOException { File file = new File(path); FileInputStream fis = new
	 * FileInputStream(file); Workbook book = new XSSFWorkbook(fis); Sheet sheet =
	 * book.getSheet(name); Row row = sheet.createRow(rowIndex); return row; }
	 * 
	 * public Row getRow(String path, String name, int rowIndex) throws IOException
	 * { File file = new File(path); FileInputStream fis = new
	 * FileInputStream(file); Workbook book = new XSSFWorkbook(fis); Sheet sheet =
	 * book.getSheet(name); Row row = sheet.getRow(rowIndex); return row; }
	 * 
	 * public int getPhysicalNumberOfRows(String path, String name) throws
	 * IOException { File file = new File(path); FileInputStream fis = new
	 * FileInputStream(file); Workbook book = new XSSFWorkbook(fis); Sheet sheet =
	 * book.getSheet(name); int physicalNumberOfRows =
	 * sheet.getPhysicalNumberOfRows(); return physicalNumberOfRows; }
	 * 
	 * public Cell createCell(String path, String name, int rowIndex, int cellIndex,
	 * WebElement element, String text) throws IOException { File file = new
	 * File(path); FileInputStream fis = new FileInputStream(file); Workbook book =
	 * new XSSFWorkbook(fis); Sheet sheet = book.getSheet(name); Row row =
	 * sheet.getRow(rowIndex); Cell createCell = row.createCell(cellIndex); String
	 * attribute = element.getAttribute(text); createCell.setCellValue(attribute);
	 * 
	 * FileOutputStream out = new FileOutputStream(file); book.write(out); return
	 * createCell; }
	 * 
	 * public Cell getCell(String path, String name, int rowIndex, int cellIndex)
	 * throws IOException { File file = new File(path); FileInputStream fis = new
	 * FileInputStream(file); Workbook book = new XSSFWorkbook(fis); Sheet sheet =
	 * book.getSheet(name); Row row = sheet.getRow(rowIndex); Cell cell =
	 * row.getCell(cellIndex); return cell; }
	 * 
	 * public CellType cellType(String path, String name, int rowIndex, int
	 * cellIndex) throws IOException { File file = new File(path); FileInputStream
	 * fis = new FileInputStream(file); Workbook book = new XSSFWorkbook(fis); Sheet
	 * sheet = book.getSheet(name); Row row = sheet.getRow(rowIndex); Cell cell =
	 * row.getCell(cellIndex); CellType cellType = cell.getCellType(); return
	 * cellType; }
	 * 
	 * public String stringCellValue(String path, String name, int rowIndex, int
	 * cellIndex) throws IOException { File file = new File(path); FileInputStream
	 * fis = new FileInputStream(file); Workbook book = new XSSFWorkbook(fis); Sheet
	 * sheet = book.getSheet(name); Row row = sheet.getRow(rowIndex); Cell cell =
	 * row.getCell(cellIndex); String stringCellValue = cell.getStringCellValue();
	 * return stringCellValue; }
	 * 
	 * public double numericCellValue(String path, String name, int rowIndex, int
	 * cellIndex) throws IOException { File file = new File(path); FileInputStream
	 * fis = new FileInputStream(file); Workbook book = new XSSFWorkbook(fis); Sheet
	 * sheet = book.getSheet(name); Row row = sheet.getRow(rowIndex); Cell cell =
	 * row.getCell(cellIndex); double numericCellValue = cell.getNumericCellValue();
	 * return numericCellValue; }
	 * 
	 * public Date dateCellValue(String path, String name, int rowIndex, int
	 * cellIndex) throws IOException { File file = new File(path); FileInputStream
	 * fis = new FileInputStream(file); Workbook book = new XSSFWorkbook(fis); Sheet
	 * sheet = book.getSheet(name); Row row = sheet.getRow(rowIndex); Cell cell =
	 * row.getCell(cellIndex); Date dateCellValue = cell.getDateCellValue(); return
	 * dateCellValue; }
	 * 
	 * public String simpleDateFormat(String dateFormat, Date dateCellValue) {
	 * SimpleDateFormat sdf = new SimpleDateFormat(dateFormat); String format =
	 * sdf.format(dateCellValue); return format; }
	 * 
	 * public String getDataFromExcel(String name, int rowNo, int cellNo) throws
	 * IOException {
	 * 
	 * String res = null;
	 * 
	 * File file = new File(" "); FileInputStream stream = new
	 * FileInputStream(file); Workbook book = new XSSFWorkbook(stream); Sheet sheet
	 * = book.getSheet(name); Row row = sheet.getRow(rowNo); Cell cell =
	 * row.getCell(cellNo); CellType cellType = cell.getCellType(); switch
	 * (cellType) { case STRING: res = cell.getStringCellValue(); break; case
	 * NUMERIC: if (DateUtil.isCellDateFormatted(cell)) { Date dateCellValue =
	 * cell.getDateCellValue(); SimpleDateFormat date = new
	 * SimpleDateFormat("dd/MM/YYYY"); res = date.format(dateCellValue); } else {
	 * double numericCellValue = cell.getNumericCellValue(); long lo = (long)
	 * numericCellValue; Long l = Long.valueOf(lo); res = l.toString(); } break;
	 * 
	 * default: break; } return res;
	 * 
	 * }
	 */
}
