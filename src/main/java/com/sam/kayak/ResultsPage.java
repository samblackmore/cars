package com.sam.kayak;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.sam.kayak.Utils.waitForElement;

public class ResultsPage {
    private final WebDriver driver;

    By resultsBodyLocator = By.className("resultsBody");
    By priceResultLocator = By.className("priceValue");

    public ResultsPage(WebDriver driver) {
        this.driver = driver;

        // Check that we're on the right page
        if (driver.findElement(resultsBodyLocator) == null) {
            throw new IllegalStateException("This is not the search results page!");
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
}
