package com.pom;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utility.UtilityClass;

public class ConfirmBooking extends UtilityClass {
	
	public ConfirmBooking() {
		PageFactory.initElements(driver, this);

	}
	@FindBy(id = "order_no")
	private WebElement printOrderId;
	
	public WebElement getPrintOrderId() {
		return printOrderId;
	}
	
}
