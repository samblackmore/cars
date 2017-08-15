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
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.MINUTES);
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void priceComparisonTestUSA() {
        String pickup = "SFO";      // San Francisco
        String dropoff = "LAX";     // L.A.
        Locale locale = new Locale.Builder().setLanguage("en").setRegion("US").build();

        priceComparisonTest(locale, pickup, dropoff, getTestDates());
    }

    @Test
    public void priceComparisonTestFrenchCanadian() {
        String pickup = "YUL";      // Montreal
        String dropoff = "YYZ";     // Toronto
        Locale locale = new Locale.Builder().setLanguage("fr").setRegion("CA").build();

        priceComparisonTest(locale, pickup, dropoff, getTestDates());
    }

    private void priceComparisonTest(Locale locale, String pickup, String dropoff, List<Date> dates) {

        List<PriceComparison> results = new ArrayList<>();

        driver.get("http://www.kayak.com/cars");

        SearchPage searchPage = new SearchPage(driver);
        searchPage = searchPage.changeLocale(locale);

        // Set up location for rest of test
        searchPage.chooseDifferentDropoff();
        searchPage.typePickupLocation(pickup);
        searchPage.typeDropoffLocation(dropoff);
        searchPage.compareNone();

        for (Date pickupDate : dates) {

            // Change dates
            Date dropoffDate = addDays(pickupDate, DEFAULT_RENTAL_DURATION);
            searchPage.enterDates(pickupDate, dropoffDate);

            // Search for return journey
            searchPage.chooseSameDropoff();
            ResultsPage resultsPage = searchPage.submitSearch();

            // Check search was entered correctly
            assertTrue(sameDay(resultsPage.getPickupDate(), pickupDate));
            assertTrue(sameDay(resultsPage.getDropoffDate(), dropoffDate));

            int lowestPriceReturn = resultsPage.getLowestPrice();

            driver.navigate().back();

            // Search one-way
            searchPage.chooseDifferentDropoff();
            resultsPage = searchPage.submitSearch();

            int lowestPriceOneWay = resultsPage.getLowestPrice();

            driver.navigate().back();

            results.add(new PriceComparison(pickup, dropoff, pickupDate, dropoffDate,
                    lowestPriceOneWay, lowestPriceReturn));
        }


        // Filter failed tests from results
        List<PriceComparison> failedTests = results.stream()
                .filter(r -> r.getLowestPriceOneWay() < r.getLowestPriceReturn())
                .collect(toList());

        // Report failures if any
        if (failedTests.size() > 0) {
            StrBuilder report = new StrBuilder();
            failedTests.forEach(r -> report.appendln(r.toString()));
            Assert.fail("The following tests failed: \n" + report);
        }
    }
}
