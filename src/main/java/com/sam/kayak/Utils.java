package com.sam.kayak;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

class Utils {

    private static final String SELECT_ALL = Keys.chord(Keys.CONTROL, "a");

    static WebElement waitForElement(WebDriver driver, By byLocator, int seconds) {
        return (new WebDriverWait(driver, seconds))
                .until(ExpectedConditions.presenceOfElementLocated(byLocator));
    }

    static WebElement getFirstVisibleElement(WebDriver driver, By byLocator) {
        for (WebElement element : driver.findElements(byLocator)) {
            if (element.isDisplayed()) {
                return element;
            }
        }
        return null;
    }

    static void replaceText(WebDriver driver, By byLocator, String text) {
        WebElement input = driver.findElement(byLocator);
        input.click();
        input.sendKeys(SELECT_ALL);
        input.sendKeys(text);
    }
}
