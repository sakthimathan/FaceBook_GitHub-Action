package com.enumconcept;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Execution {

	public static void main(String[] args) {
		WebDriver driver = new ChromeDriver();
		driver.get("https://adactinhotelapp.com/");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		driver.findElement(By.id(Locators.IDforUser)).sendKeys("Manoth92");
		driver.findElement(By.id(Locators.IDforPass)).sendKeys("Q19OY8");
		driver.findElement(Base.getloc(EnumClass.button.click)).click();

	}
}
