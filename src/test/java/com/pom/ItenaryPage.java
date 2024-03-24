package com.pom;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utility.UtilityClass;

public class ItenaryPage extends UtilityClass {

	public ItenaryPage() {
		PageFactory.initElements(driver, this);

	}
	@FindBy(name = "my_itinerary")
	private WebElement myItenary;
	@FindBy(id = "check_all")
	private WebElement radiobookList;
	@FindBy(name = "cancelall")
	private WebElement cancelAll;
	@FindBy(id = "logout")
	private WebElement logOut;
	
	public WebElement getMyItenary() {
		return myItenary;
	}
	public WebElement getRadiobookList() {
		return radiobookList;
	}
	
	public WebElement getCancelAll() {
		return cancelAll;
	}
	public WebElement getLogOut() {
		return logOut;
	}
	
	

	/*
	 * public WebElement getMyItenary() { return myItenary; }
	 * 
	 * public void setMyItenary(WebElement myItenary) {
	 * driver.findElement(By.name("my_itinerary")); this.myItenary = myItenary; }
	 * 
	 * public WebElement getRadiobookList() { return radiobookList; }
	 * 
	 * public void setRadiobookList(WebElement radiobookList) {
	 * driver.findElement(By.id("check_all")); this.radiobookList = radiobookList; }
	 * 
	 * public WebElement getCancelAll() { return cancelAll; }
	 * 
	 * public void setCancelAll(WebElement cancelAll) {
	 * driver.findElement(By.name("logout")); this.cancelAll = cancelAll; }
	 */
	
	
	
	
	
}