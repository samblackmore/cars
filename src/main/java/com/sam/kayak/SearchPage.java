package com.sam.kayak;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.sam.kayak.Utils.*;

public class SearchPage {

    private final WebDriver driver;
    public static final String URL = "https://www.kayak.com/cars";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");


    public SearchPage(WebDriver driver) {
        this.driver = driver;

        // Check that we're on the right page
        if (!URL.equals(driver.getCurrentUrl())) {
            throw new IllegalStateException("This is not the search page!");
        }
    }

    private String locationResultFormat = "//*[@class='airportCode']/*[text()='%s']";

    private By pickupLocator = By.name("pickup");
    private By dropoffLocator = By.name("dropoff");
    private By pickupDateLocator = byPartialId("pickup-date-input");
    private By dropoffDateLocator = byPartialId("dropoff-date-input");
    private By sameDropoffLocator = byPartialId("same-label");
    private By diffDropoffLocator = byPartialId("oneway-label");
    private By compareNoneLocator = byPartialId("compareTo-none");

    private By bySearchResult(String location) {
        String xpath = String.format(locationResultFormat, location);
        return By.xpath(xpath);
    }

    public SearchPage typePickupLocation(String location) {
        replaceText(driver, pickupLocator, location);
        waitForElement(driver, bySearchResult(location), 10);
        driver.findElement(bySearchResult(location)).click();
        return this;
    }

    public SearchPage typeDropoffLocation(String location) {
        replaceText(driver, dropoffLocator, location);
        waitForElement(driver, bySearchResult(location), 10);
        driver.findElement(bySearchResult(location)).click();
        return this;
    }

    public SearchPage enterDates(Date pickupdate, Date dropoffDate) {

        long diff = dropoffDate.getTime() - pickupdate.getTime();
        assert diff > 0;

        // Pickup date can be typed directly
        replaceTextAndSubmit(driver, pickupDateLocator, DATE_FORMAT.format(pickupdate));

        // Keyboard control then moves to on-screen calendar
        WebElement dropoffDateElement = driver.findElement(dropoffDateLocator);

        // Press right arrow until we get desired date
        while (!dropoffDateElement.getText().equals(DATE_FORMAT.format(dropoffDate))) {
            dropoffDateElement.sendKeys(Keys.ARROW_RIGHT);
        }

        dropoffDateElement.sendKeys(Keys.ENTER);

        return this;
    }

    public SearchPage chooseSameDropoff() {
        getFirstVisibleElement(driver, sameDropoffLocator).click();
        return this;
    }

    public SearchPage chooseDifferentDropoff() {
        getFirstVisibleElement(driver, diffDropoffLocator).click();
        return this;
    }

    public SearchPage compareNone() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(compareNoneLocator));
        getFirstVisibleElement(driver, compareNoneLocator).click();
        return this;
    }

    public ResultsPage submitSearch() {
        driver.findElement(pickupLocator).submit();
        return new ResultsPage(driver);
    }

    public ResultsPage searchReturn(Date pickupDate, Date dropoffDate, String location) {
        getFirstVisibleElement(driver, sameDropoffLocator).click();
        typePickupLocation(location);
        enterDates(pickupDate, dropoffDate);
        compareNone();
        return submitSearch();
    }

    public ResultsPage searchOneWay(Date pickupDate, Date dropoffDate, String pickupLocation, String dropoffLocation) {
        getFirstVisibleElement(driver, diffDropoffLocator).click();
        typePickupLocation(pickupLocation);
        typeDropoffLocation(dropoffLocation);
        enterDates(pickupDate, dropoffDate);
        compareNone();
        return submitSearch();
    }
}
