package com.enumconcept;

import org.openqa.selenium.By;

import com.enumconcept.EnumClass.button;

public class Base {

	public static By getloc(button but) {
		String clickxpath = null;
		switch (but) {
		case click:
			clickxpath = Locators.IDforClick;

			break;

		default:
			break;
		}

		return By.id(clickxpath);

	}

}
