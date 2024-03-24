package com.pom;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.utility.UtilityClass;

public class AgainLogin extends UtilityClass {

	public AgainLogin(WebDriver driver) {

		this.driver = driver;
	}

	private WebElement againlog;

	public void setAgainlog() {

		WebElement againlog = driver.findElement(By.xpath("//a[text()='Click here to login again']"));

		this.againlog = againlog;
	}

	public WebElement getAgainlog() {
		return againlog;

	}

}
