import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class Cars {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
    private static final String SELECT_ALL = Keys.chord(Keys.CONTROL, "a");
    private static WebDriver driver = new ChromeDriver();

    public static void main(String[] args) {

        driver.get("http://www.kayak.com/cars");

        String pickupLocation = "SFO";
        String dropoffLocation = "LAX";

        By elemSearch = By.name("pickup");
        By elemDropOff = By.name("dropoff");
        By elemSearchResult = By.xpath("//span[@class=\"airportCode\"]/b[text()=\"" + pickupLocation + "\"]");
        By elemDropOffSearchResult = By.xpath("//span[@class=\"airportCode\"]/b[text()=\"" + dropoffLocation + "\"]");
        By elemPickupDate = By.xpath("//div[contains(@id,'pickup-date-input')]");
        By elemDropoffDate = By.xpath("//div[contains(@id,'dropoff-date-input')]");
        By elemSameDropOff = By.xpath("//label[contains(@id,'same-label')]");
        By elemDiffDropOff = By.xpath("//label[contains(@id,'oneway-label')]");
        By elemCompareNone = By.xpath("//*[contains(@id,'compareTo-none')]");
        By elemPriceResult = By.className("priceValue");

        Date now = new Date();
        Date tomorrow = addDays(now, 1);
        Date nextWeek = addDays(now, 7);

        String pickupDate = DATE_FORMAT.format(tomorrow);
        String dropoffDate = DATE_FORMAT.format(nextWeek);

        WebElement sameDropOff = driver.findElement(elemSameDropOff);
        WebElement diffDropOff = getFirstVisibleElement(elemDiffDropOff);

        assert diffDropOff != null;

        assert sameDropOff.getAttribute("class").contains("checked");
        assert !diffDropOff.getAttribute("class").contains("checked");

        // Type location to search
        replaceText(elemSearch, pickupLocation);
        waitForElement(elemSearchResult, 10).click();

        WebElement compareNone = getFirstVisibleElement(elemCompareNone);

        compareNone.click();

        driver.findElement(elemSearch).click();

        // Choose dates
        replaceText(elemPickupDate, pickupDate);
        replaceText(elemDropoffDate, dropoffDate);

        // Search
        driver.findElement(elemSearch).submit();

        waitForElement(elemPriceResult, 60);

        List<WebElement> prices = driver.findElements(elemPriceResult);

        // TODO - What if the currency isn't dollars?
        String lowestPrice = prices.get(0).getText().replace("$", "");

        int lowestPriceSameLocation = Integer.valueOf(lowestPrice);

        driver.navigate().back();

        System.out.println("Page title is: " + driver.getTitle());

        diffDropOff = getFirstVisibleElement(elemDiffDropOff);

        diffDropOff.click();

        replaceText(elemDropOff, dropoffLocation);
        waitForElement(elemDropOffSearchResult, 10).click();

        driver.findElement(elemSearch).submit();

        System.out.println("Page title is: " + driver.getTitle());

        waitForElement(elemPriceResult, 60);

        prices = driver.findElements(elemPriceResult);

        lowestPrice = prices.get(0).getText().replace("$", "");

        int lowestPriceDiffLocation = Integer.valueOf(lowestPrice);

        System.out.println("Cheapest one-way: " + lowestPriceDiffLocation);
        System.out.println("Cheapest return: " + lowestPriceSameLocation);

        assert lowestPriceSameLocation < lowestPriceDiffLocation;

        driver.quit();
    }

    private static WebElement waitForElement(By byLocator, int seconds) {
        return (new WebDriverWait(driver, seconds))
                .until(ExpectedConditions.presenceOfElementLocated(byLocator));
    }

    private static WebElement getFirstVisibleElement(By byLocator) {
        for (WebElement element : driver.findElements(byLocator)) {
            if (element.isDisplayed()) {
                return element;
            }
        }
        return null;
    }

    private static void replaceText(By byLocator, String text) {
        WebElement input = driver.findElement(byLocator);
        input.click();
        input.sendKeys(SELECT_ALL);
        input.sendKeys(text);
    }

    private static Date addDays(Date startDate, int days) {
        Calendar day = Calendar.getInstance();
        day.setTime(startDate);
        day.add(Calendar.DATE, days);
        return day.getTime();
    }

    private static void switchWindows() {
        String currentWindow = driver.getWindowHandle();
        Set<String> allWindows = driver.getWindowHandles();

        assert allWindows.size() == 2;

        allWindows.remove(currentWindow);
        String newWindow = (String) allWindows.toArray()[0];

        //driver.close();
        driver.switchTo().window(newWindow);
    }
}
