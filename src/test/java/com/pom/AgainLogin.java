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
		// Lazy-init the element so callers don't get a null pointer if it wasn't set explicitly
		if (this.againlog == null) {
			try {
				this.againlog = driver.findElement(By.xpath("//a[text()='Click here to login again']"));
			} catch (Exception e) {
				// keep againlog null and let caller handle it; log for debugging
				System.out.println("AgainLogin: unable to locate 'Click here to login again' link: " + e.getMessage());
			}
		}
		return againlog;

	}

}
