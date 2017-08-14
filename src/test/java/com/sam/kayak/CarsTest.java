package com.sam.kayak;

import org.apache.commons.lang3.text.StrBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.apache.commons.lang3.time.DateUtils.addWeeks;
import static org.junit.Assert.assertTrue;

public class CarsTest {

    private WebDriver driver;

    private final int DATES_PER_TEST = 5;
    private final int DEFAULT_RENTAL_DURATION = 7;

    private List<Date> getTestDates() {
        List<Date> dates = new ArrayList<>();
        Date tomorrow = addDays(new Date(), 1);

        for (int i = 0; i < DATES_PER_TEST; i++) {
            dates.add(addWeeks(tomorrow, i));
        }

        return dates;
    }

    private boolean sameDay(Date date1, Date date2) {
        return date1.getDay() == date2.getDay()
                && date1.getMonth() == date2.getMonth()
                && date1.getYear() == date2.getYear();
    }

    @Before
    public void setup() {
        driver = new ChromeDriver();
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void priceComparisonTestSFO() {
        String pickup = "SFO";
        String dropoff = "LAX";
        List<Date> dates = getTestDates();

        List<PriceComparison> results = priceComparisonTest(pickup, dropoff, dates);

        List<PriceComparison> failedTests = results.stream()
                .filter(r -> r.getLowestPriceOneWay() < r.getLowestPriceReturn())
                .collect(toList());

        if (failedTests.size() > 0) {
            StrBuilder report = new StrBuilder();
            failedTests.forEach(r -> report.appendln(r.toString()));
            Assert.fail("The following tests failed: \n" + report);
        }
    }

    private List<PriceComparison> priceComparisonTest(String pickup, String dropoff, List<Date> dates) {

        List<PriceComparison> results = new ArrayList<>();

        driver.get("http://www.kayak.com/cars");

        SearchPage searchPage = new SearchPage(driver);
        searchPage.chooseDifferentDropoff();
        searchPage.typePickupLocation(pickup);
        searchPage.typeDropoffLocation(dropoff);
        searchPage.compareNone();

        for (Date pickupDate : dates) {

            Date dropoffDate = addDays(pickupDate, DEFAULT_RENTAL_DURATION);

            // Get lowest price for return journey
            searchPage.chooseSameDropoff();
            searchPage.enterDates(pickupDate, dropoffDate);
            ResultsPage resultsPage = searchPage.submitSearch();
            assertTrue(sameDay(resultsPage.getPickupDate(), pickupDate));
            assertTrue(sameDay(resultsPage.getDropoffDate(), dropoffDate));
            int lowestPriceReturn = resultsPage.getLowestPrice();

            searchPage = resultsPage.goBack();

            // Get lowest price for one-way
            searchPage.chooseDifferentDropoff();
            resultsPage = searchPage.submitSearch();
            assertTrue(sameDay(resultsPage.getPickupDate(), pickupDate));
            assertTrue(sameDay(resultsPage.getDropoffDate(), dropoffDate));
            int lowestPriceOneWay = resultsPage.getLowestPrice();

            searchPage = resultsPage.goBack();

            results.add(new PriceComparison(pickup, dropoff, pickupDate, dropoffDate,
                    lowestPriceOneWay, lowestPriceReturn));
        }

        return results;
    }
}
