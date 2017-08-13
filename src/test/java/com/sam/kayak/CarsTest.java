package com.sam.kayak;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Date;

import static org.apache.commons.lang3.time.DateUtils.addDays;

public class CarsTest {

    private WebDriver driver;

    @Before
    public void setup() {
        driver = new ChromeDriver();
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void priceComparisonTest() {

        String pickupLocation = "SFO";
        String dropoffLocation = "LAX";

        Date now = new Date();
        Date pickupDate = addDays(now, 1);
        Date dropoffDate = addDays(now, 7);

        driver.get("http://www.kayak.com/cars");

        SearchPage searchPage = new SearchPage(driver);

        // Get lowest price for return journey
        ResultsPage resultsPage = searchPage.searchReturn(pickupDate, dropoffDate, pickupLocation);
        int lowestPriceReturn = resultsPage.getLowestPrice();

        searchPage = resultsPage.goBack();

        // Get lowest price for one-way
        resultsPage = searchPage.searchOneWay(pickupDate, dropoffDate, pickupLocation, dropoffLocation);
        int lowestPriceOneWay = resultsPage.getLowestPrice();

        System.out.println("Cheapest one-way: " + lowestPriceOneWay);
        System.out.println("Cheapest return: " + lowestPriceReturn);

        Assert.assertTrue(lowestPriceOneWay > lowestPriceReturn);
    }
}
