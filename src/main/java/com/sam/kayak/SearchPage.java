package com.sam.kayak;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.sam.kayak.Utils.*;
import static org.apache.commons.lang3.time.DateUtils.addDays;

public class SearchPage {

    private final WebDriver driver;
    private SimpleDateFormat dateFormat = null;

    private final static List<SimpleDateFormat> POSSIBLE_DATE_FORMATS = new ArrayList<>();
    static {
        POSSIBLE_DATE_FORMATS.add(new SimpleDateFormat("dd/MM/yyyy"));
        POSSIBLE_DATE_FORMATS.add(new SimpleDateFormat("MM/dd/yyyy"));
        POSSIBLE_DATE_FORMATS.add(new SimpleDateFormat("yyyy/MM/dd"));
    }

    private By searchBodyLocator = By.className("CarsSearch");
    private By pickupLocator = By.name("pickup");
    private By dropoffLocator = By.name("dropoff");
    private By pickupDateLocator = byPartialId("pickup-date-input");
    private By dropoffDateLocator = byPartialId("dropoff-date-input");
    private By sameDropoffLocator = byPartialId("same-label");
    private By diffDropoffLocator = byPartialId("oneway-label");
    private By compareNoneLocator = byPartialId("compareTo-none");
    private By localeMenuLocator = byPartialId("countryPicker-dropdown");

    private String airportCodeResultXPathFormat = "//*[@class='airportCode']/*[text()='%s']";
    private String localeXPathFormat = "//*[@data-locale='%s']";

    SearchPage(WebDriver driver) {
        this.driver = driver;

        if (driver.findElements(searchBodyLocator).isEmpty())
            throw new IllegalStateException("This is not the search page!");

        // Work out which date format is currently in use
        // based on the pre-populated value when page first loads
        WebElement dateElement = this.driver.findElement(pickupDateLocator);
        dateElement.click();
        String actualDate = dateElement.getText();
        Date expectedDate = addDays(new Date(), 1);

        for (SimpleDateFormat formatter : POSSIBLE_DATE_FORMATS) {
            if (actualDate.equals(formatter.format(expectedDate)))
                dateFormat = formatter;
        }

        if (dateFormat == null)
            throw new IllegalStateException("Date format not recognized");
    }

    public SearchPage typePickupLocation(String location) {
        By airportCodeResultLocator = By.xpath(String.format(airportCodeResultXPathFormat, location));

        replaceText(driver, pickupLocator, location);
        waitForElement(driver, airportCodeResultLocator, 10);
        // Find again to avoid stale link?
        driver.findElement(airportCodeResultLocator).click();

        return this;
    }

    public SearchPage typeDropoffLocation(String location) {
        By airportCodeResultLocator = By.xpath(String.format(airportCodeResultXPathFormat, location));

        replaceText(driver, dropoffLocator, location);
        waitForElement(driver, airportCodeResultLocator, 10);
        driver.findElement(airportCodeResultLocator).click();
        return this;
    }

    public SearchPage enterDates(Date pickupDate, Date dropoffDate) {

        long diff = dropoffDate.getTime() - pickupDate.getTime();
        assert diff > 0;

        // Pickup date can be typed directly
        replaceTextAndSubmit(driver, pickupDateLocator, dateFormat.format(pickupDate));

        // Keyboard control then moves to on-screen calendar
        WebElement dropoffDateElement = driver.findElement(dropoffDateLocator);

        // Press right arrow until we get desired date
        while (!dropoffDateElement.getText().equals(dateFormat.format(dropoffDate))) {
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
        chooseSameDropoff();
        typePickupLocation(location);
        enterDates(pickupDate, dropoffDate);
        compareNone();
        return submitSearch();
    }

    public ResultsPage searchOneWay(Date pickupDate, Date dropoffDate, String pickupLocation, String dropoffLocation) {
        chooseDifferentDropoff();
        typePickupLocation(pickupLocation);
        typeDropoffLocation(dropoffLocation);
        enterDates(pickupDate, dropoffDate);
        compareNone();
        return submitSearch();
    }

    public SearchPage changeLocale(Locale locale) {

        String localeString = locale.toString().replace("_", "-");
        By localeLocator = By.xpath(String.format(localeXPathFormat, localeString));

        // Countries drop-down
        driver.findElement(localeMenuLocator).click();

        // Find our locale
        WebElement link = driver.findElement(localeLocator);
        WebElement parent = link.findElement(By.xpath(".."));

        // Get a url we can wait for
        String url = link.getAttribute("href");

        // If a country has multiple locales, the parent has the url
        if (url == null) {
            url = parent.getAttribute("href");
        }

        // Change locale
        link.click();

        // Wait till page fully loaded
        driver.get(url);

        return new SearchPage(driver);
    }
}
