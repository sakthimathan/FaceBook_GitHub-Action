package com.pom;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utility.UtilityClass;

public class SelectHotel extends UtilityClass {

	public SelectHotel() {
		PageFactory.initElements(driver, this);

	}

	@FindBy(id = "radiobutton_0")
	private WebElement selectoptionclick;
	@FindBy(id = "continue")
	private WebElement continuebtnclick;

	public WebElement getSelectoptionclick() {
		return selectoptionclick;
	}

	public WebElement getContinuebtnclick() {
		return continuebtnclick;
	}

	public void ClickSelectHotel() {

		click(getSelectoptionclick());
		click(getContinuebtnclick());

	}

}
