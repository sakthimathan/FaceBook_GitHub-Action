package com.adactin;

import java.time.Duration;

import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.pom.AgainLogin;
import com.pom.BookHotel;
import com.pom.ConfirmBooking;
import com.pom.ItenaryPage;
import com.pom.LoginPage;
import com.pom.SearchHotel;
import com.pom.SelectHotel;
import com.utility.UtilityClass;

public class AdactinHotel extends UtilityClass {
	// public static WebDriver driver;

	@Parameters({ "BROWSERNAME" })
	// @Test(priority = 0)
	@BeforeClass
	public void BrowserLaunch(String BROWSERNAME) {
		switchDrivers(BROWSERNAME);
		getUrl("https://adactinhotelapp.com/");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
		maximizeWindow();
		String gettingTitle = GettingTitle();
		System.out.println(gettingTitle);
		System.out.println(gettingTitle);
	}

	@AfterClass
	public void closeBrowser() {
		closeAllWindow();
	}

	@BeforeMethod
	public void startTime() {
		long starts = System.currentTimeMillis();
		System.out.println(starts);
	}

	@AfterMethod
	public void endTime() {
		long ends = System.currentTimeMillis();
		System.out.println(ends);
	}

	@Test(priority = 1)
	public void loginTest() {

		LoginPage lp = new LoginPage();
		lp.goLogin("SrihariMahendran", "India@2024");

	}

	@Test(priority = 2)
	public void searchTest() {
		SearchHotel sh = new SearchHotel();
		sh.fillSearchHotel(3, "13/02/2024", "15/02/2024");
	}

	@Test(priority = 3)

	public void selectTest() {
		SelectHotel sh1 = new SelectHotel();
		sh1.ClickSelectHotel();

	}

	@Test(priority = 4)
	public void BookHotelTest() {

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,500)");

		BookHotel bh = new BookHotel();
		bh.bookingHotel();
	}

	@Test(priority = 5)
	public void confirmBookTest() throws InterruptedException {

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,1000)");

		ConfirmBooking cb = new ConfirmBooking();

		String IdOrder = getAttribute(cb.getPrintOrderId(), "value");
		System.out.println(" BOOK ORDER ID IS --> " + IdOrder);
		//
	}

	@Test(priority = 6)
	public void itenary() throws InterruptedException {

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,3000)");
		ItenaryPage ip = new ItenaryPage();
		// js.executeScript("arguments[0].scrollIntoView(true)", ip.getMyItenary());
		ip.getMyItenary().click();
		ip.getRadiobookList().click();
		ip.getCancelAll().click();
		acceptAlert();
		Thread.sleep(5000);
		ip.getLogOut().click();

	}

	@Test(priority = 7)
	public void reLog() {
		AgainLogin a = new AgainLogin(driver);
		a.getAgainlog().click();

	}
}
