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

public class Cars {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
    private static final String SELECT_ALL = Keys.chord(Keys.CONTROL, "a");
    private static WebDriver driver = new ChromeDriver();

    public static void main(String[] args) {

        driver.get("http://www.kayak.com/cars");

        String location = "SFO";

        By elemSearch = By.name("pickup");
        By elemSearchResult = By.xpath("//span[@class=\"airportCode\"]/b[text()=\"" + location + "\"]");
        By elemPickupDate = By.xpath("//div[contains(@id,'pickup-date-input')]");
        By elemDropoffDate = By.xpath("//div[contains(@id,'dropoff-date-input')]");
        By elemSameDropOff = By.xpath("//label[contains(@id,'same-label')]");

        Date now = new Date();
        Date tomorrow = addDays(now, 1);
        Date nextWeek = addDays(now, 7);

        String pickupDate = DATE_FORMAT.format(tomorrow);
        String dropoffDate = DATE_FORMAT.format(nextWeek);

        // Type location to search
        replaceText(elemSearch, location);
        waitForElement(elemSearchResult).click();

        // Choose dates
        replaceText(elemPickupDate, pickupDate);
        replaceText(elemDropoffDate, dropoffDate);

        // Search
        driver.findElement(elemSearch).submit();

        // Check the title of the page
        System.out.println("Page title is: " + driver.getTitle());

        driver.quit();
    }

    private static WebElement waitForElement(By byLocator) {
        return (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(byLocator));
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
}
