package com.pom;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utility.UtilityClass;

public class SearchHotel extends UtilityClass {

	public SearchHotel() {
		PageFactory.initElements(driver, this);
	}

	@FindBy(id = "location")
	private WebElement locationDd;
	@FindBy(id = "hotels")
	private WebElement hotelDd;
	@FindBy(id = "room_type")
	private WebElement roomType;
	@FindBy(id = "room_nos")
	private WebElement noRoom;
	@FindBy(id = "datepick_in")
	private WebElement cid;
	@FindBy(id = "datepick_out")
	private WebElement cod;
	@FindBy(id = "adult_room")
	private WebElement aprDd;
	@FindBy(id = "child_room")
	private WebElement cprDd;
	@FindBy(id = "Submit")
	private WebElement searching;

	public WebElement getLocationDd() {
		return locationDd;
	}

	public WebElement getHotelDd() {
		return hotelDd;
	}

	public WebElement getRoomType() {
		return roomType;
	}

	public WebElement getNoRoom() {
		return noRoom;
	}

	public WebElement getCid() {
		return cid;
	}

	public WebElement getCod() {
		return cod;
	}

	public WebElement getAprDd() {
		return aprDd;
	}

	public WebElement getCprDd() {
		return cprDd;
	}

	public WebElement getSearching() {
		return searching;
	}

	public void fillSearchHotel(int l, String datein, String dateout) {
		selectOptionByIndex(getLocationDd(), l);
		selectOptionByIndex(getHotelDd(), l);
		selectOptionByIndex(getRoomType(), l);
		selectOptionByIndex(getNoRoom(), l);
		sendKeys(getCid(), datein);
		sendKeys(getCod(), dateout);
		selectOptionByIndex(getAprDd(), l);
		selectOptionByIndex(getCprDd(), l);
		click(getSearching());
	}
}