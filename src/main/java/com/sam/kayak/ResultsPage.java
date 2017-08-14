package com.sam.kayak;

import junit.framework.AssertionFailedError;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sam.kayak.Utils.waitForElement;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ResultsPage {

    // Example URL - https://www.kayak.com/cars/San-Francisco,CA-c13852/2017-08-13/2017-08-14

    private static final String URL_REGEX = SearchPage.URL + "/(.*)/(.*)/(.*)";
    private static final SimpleDateFormat URL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private Date pickupDate;
    private Date dropoffDate;
    private final WebDriver driver;

    By resultsBodyLocator = By.className("resultsBody");
    By priceResultLocator = By.className("priceValue");

    public ResultsPage(WebDriver driver) {
        this.driver = driver;

        if (driver.findElements(resultsBodyLocator).isEmpty()) {
            throw new IllegalStateException("This is not the results page!");
        }

        String url = driver.getCurrentUrl();
        Pattern r = Pattern.compile(URL_REGEX);
        Matcher m = r.matcher(url);

        try {
            assertTrue(m.find());
            pickupDate = URL_DATE_FORMAT.parse(m.group(2));
            dropoffDate = URL_DATE_FORMAT.parse(m.group(3));
        } catch (AssertionFailedError | IndexOutOfBoundsException | ParseException e) {
            throw new IllegalStateException(
                    String.format("Search results URL (%s) did not match the expected pattern: %s", url, URL_REGEX));
        }

        waitForElement(this.driver, priceResultLocator, 60);
    }

    public int getLowestPrice() {

        List<WebElement> prices = driver.findElements(priceResultLocator);

        // TODO - What if the currency isn't dollars?
        String lowestPrice = prices.get(0).getText().replace("$", "");

        return Integer.valueOf(lowestPrice);
    }

    public SearchPage goBack() {
        driver.navigate().back();
        return new SearchPage(driver);
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public Date getDropoffDate() {
        return dropoffDate;
    }
}
