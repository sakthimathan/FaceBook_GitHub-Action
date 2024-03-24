package com.pom;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utility.UtilityClass;

public class BookHotel extends UtilityClass {
	
	public BookHotel() {
		PageFactory.initElements(driver, this);

	}
	@FindBy(id = "first_name")
	private WebElement cusfName;
	@FindBy(id = "last_name")
	private WebElement cuslName;
	@FindBy(id = "address")
	private WebElement billAddres;
	@FindBy(id = "cc_num")
	private WebElement creditNo;
	@FindBy(id = "cc_type")
	private WebElement creditTypedd;
	@FindBy(id = "cc_exp_month")
	private WebElement edMondd;
	@FindBy(id = "cc_exp_year")
	private WebElement edYeardd;
	@FindBy(id = "cc_cvv")
	private WebElement cvv;
	@FindBy(id = "book_now")
	private WebElement bookNow;
	
	public WebElement getCusfName() {
		return cusfName;
	}
	public WebElement getCuslName() {
		return cuslName;
	}
	public WebElement getBillAddres() {
		return billAddres;
	}
	public WebElement getCreditNo() {
		return creditNo;
	}
	public WebElement getCreditTypedd() {
		return creditTypedd;
	}
	public WebElement getEdMondd() {
		return edMondd;
	}
	public WebElement getEdYeardd() {
		return edYeardd;
	}
	public WebElement getCvv() {
		return cvv;
	}
	public WebElement getBookNow() {
		return bookNow;
	}

	public void bookingHotel() {
		
		sendKeys(getCusfName(),"SHREE HARI");
		sendKeys(getCuslName(),"MAHENDRAN");
		sendKeys(getBillAddres(),"ERODE CHENNAI");
		sendKeys(getCreditNo(),"1452365274589658");
		selectOptionByIndex(getCreditTypedd(), 2);
		selectOptionByIndex(getEdMondd(), 4);
		selectOptionByIndex(getEdYeardd(),15);
		sendKeys(getCvv(),"736");
		click(getBookNow());
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
