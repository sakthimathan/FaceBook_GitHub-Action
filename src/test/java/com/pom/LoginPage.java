package com.pom;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utility.UtilityClass;

public class LoginPage extends UtilityClass {
	public LoginPage() {
		PageFactory.initElements(driver, this);

	}

	@FindBy(id = "username")
	private WebElement userid;
	@FindBy(id = "password")
	private WebElement pass;
	@FindBy(id = "login")
	private WebElement btn;

	public WebElement getUserid() {
		return userid;
	}

	public WebElement getPass() {
		return pass;
	}

	public WebElement getBtn() {
		return btn;
	}

	public void goLogin(String userName, String Password) {
		sendKeys(getUserid(), userName);
		sendKeys(getPass(), Password);
		click(getBtn());

	}

}
