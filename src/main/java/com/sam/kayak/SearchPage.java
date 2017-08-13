package com.sam.kayak;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.sam.kayak.Utils.getFirstVisibleElement;
import static com.sam.kayak.Utils.replaceText;
import static com.sam.kayak.Utils.waitForElement;

public class SearchPage {

    public static final String URL = "https://www.kayak.com/cars";

    private final WebDriver driver;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");


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
    private By pickupDateLocator = By.xpath("//div[contains(@id,'pickup-date-input')]");
    private By dropoffDateLocator = By.xpath("//div[contains(@id,'dropoff-date-input')]");
    private By sameDropoffLocator = By.xpath("//label[contains(@id,'same-label')]");
    private By diffDropoffLocator = By.xpath("//label[contains(@id,'oneway-label')]");
    private By compareNoneLocator = By.xpath("//*[contains(@id,'compareTo-none')]");

    private By bySearchResult(String location) {
        String xpath = String.format(locationResultFormat, location);
        return By.xpath(xpath);
    }

    public SearchPage typePickupLocation(String location) {
        replaceText(driver, pickupLocator, location);
        waitForElement(driver, bySearchResult(location), 10).click();
        return this;
    }

    public SearchPage typeDropoffLocation(String location) {
        replaceText(driver, dropoffLocator, location);
        waitForElement(driver, bySearchResult(location), 10).click();
        return this;
    }

    public SearchPage typePickupDate(Date date) {
        replaceText(driver, pickupDateLocator, DATE_FORMAT.format(date));
        return this;
    }

    public SearchPage typeDropoffDate(Date date) {
        replaceText(driver, dropoffDateLocator, DATE_FORMAT.format(date));
        return this;
    }

    public SearchPage compareNone() {
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
        typePickupDate(pickupDate);
        typeDropoffDate(dropoffDate);
        compareNone();
        return submitSearch();
    }

    public ResultsPage searchOneWay(Date pickupDate, Date dropoffDate, String pickupLocation, String dropoffLocation) {
        getFirstVisibleElement(driver, diffDropoffLocator).click();
        typePickupLocation(pickupLocation);
        typeDropoffLocation(dropoffLocation);
        typePickupDate(pickupDate);
        typeDropoffDate(dropoffDate);
        compareNone();
        return submitSearch();
    }
}
