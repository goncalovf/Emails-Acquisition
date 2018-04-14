package com.emailsacquisition;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


public class Spider {
    public static List<String> get_profile_pages_urls(String currentUrl) {
        System.setProperty("webdriver.chrome.driver","C:\\Users\\gonca\\IdeaProjects\\exe\\chromedriver.exe");      // Get ChromeDriver
        WebDriver chrome = new ChromeDriver();
        chrome.get(currentUrl);

        /*
         * When subject is available in different levels of education, a modal appears. Close it
         */
        Boolean modalActive = chrome.findElement(By.className("modal-dialog")).isDisplayed();
        if (modalActive) {
            chrome.findElement(By.cssSelector("button[data-dismiss='modal']")).click();
        }

        /*
         * Wait to be used whenever we want to wait for javascript to end execution.
         */
        Wait<WebDriver> wait = new FluentWait<WebDriver>(chrome)
                .withTimeout(Duration.ofSeconds(8))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);

        /*
         * Expand search criteria, which are sliders
         */
        List<WebElement> handles = chrome.findElements(By.className("ui-slider-handle"));
        Integer i = 1;
        Integer numberOfArrowRight = 0;
        for (WebElement handle : handles) {
            if (i == 1) numberOfArrowRight = 10;                // Price
            else if (i == 2) numberOfArrowRight = 20;           // Distance
            else if (i == 3) break;                             // Reputation
            for (int j = 1; j <= numberOfArrowRight; j++) {
                handle.sendKeys(Keys.ARROW_RIGHT);
            }
            i++;
        }

        /*
         * Get number of pages with profiles and if more than one, go to bottom of page to make page select box visible and selection items visible
         */
        int listPagesCount;
        WebElement pageSelectBox = null;
        boolean moreThanOnePage = chrome.findElements(By.cssSelector("ul.select-clone.custom-list li")).size() > 0;
        if (moreThanOnePage) {
            /*
             * Hide zendesk because it opens automatically (don't know why) and puts itself in front of page list options, blocking its view and executable actions
             */
            WebElement zendesk = wait.until(new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver chrome) {
                    return chrome.findElement(By.cssSelector("div[data-test-id='ChatWidgetButton']"));
                }
            });
            ((JavascriptExecutor)chrome).executeScript("arguments[0].style.visibility='hidden'", zendesk);

            listPagesCount = chrome.findElements(By.cssSelector("ul.select-clone.custom-list li")).size();
            pageSelectBox = chrome.findElement(By.cssSelector("div.properties-listing-footer.clearfix span.select-box"));

            ((JavascriptExecutor) chrome).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        } else {
            listPagesCount = 1;
        }
        System.out.println("Number of pages: " + listPagesCount);

        /*
         * Get urls to profile pages
         */
        List<String> profileUrls = new ArrayList<String>();
        for (int k = 1; k <= listPagesCount; k++) {
            /*
             * Navigate to next profile list page
             */
            if (k > 1) {
                Actions openPageSelectBox = new Actions(chrome);
                openPageSelectBox.moveToElement(pageSelectBox).click().perform();
                try {
                    Thread.sleep(2000);     // I cannot see condition that could be defined in this case
                } catch (InterruptedException e){
                    System.out.println("Did not sleep.");
                }
                WebElement pageListItem = chrome.findElement(By.cssSelector("span.select-box li[data-page='" + k + "']"));
                Actions clickPageListItem = new Actions(chrome);
                clickPageListItem.moveToElement(pageListItem).click().perform();
                try {
                    Thread.sleep(3500);     // I cannot see condition that could be defined in this case
                } catch (InterruptedException e){
                    System.out.println("Did not sleep.");
                }
            }
            /*
             * Get links to profiles listed on page
             */
            int profilesCount = chrome.findElements(By.cssSelector("ul#results_list li")).size();
            System.out.println("Profiles in page: " + profilesCount);
            for (int m = 1; m <= profilesCount; m++) {
                String profileUrlElementSelector = String.format("ul#results_list li:nth-child(%d) a", m);      // Needs to be out of function
                String profileUrl = wait.until(new Function<>() {
                    public String apply(WebDriver chrome) {
                        WebElement profileUrlElement = chrome.findElement(By.cssSelector(profileUrlElementSelector));
                        return profileUrlElement.getAttribute("href");
                    }
                });
                profileUrls.add(profileUrl);
            }
        }
        System.out.println("List of profile urls: " + profileUrls);
        return profileUrls;
    }

    /**
     *  get_profile_links works, but now we're using selenium to get profile urls since we have to use javascript to manipulate search criteria and list navigation
     *
     *  @deprecated use {@link #get_profile_pages_urls(String CurrentUrl)} instead
     **/
    @Deprecated
    public static List<String> get_profile_links(Document webpage) {
        List<String> profileLinks = new ArrayList<String>();
        Elements linksAndChildren = webpage.select("ul#results_list a[href*=id]");
        for (Element uniqueElement : linksAndChildren) {
            if (uniqueElement.tagName().equals("a")) {
                String profileLink = "https://www.explicas.me/" + uniqueElement.attr("href");
                profileLinks.add(profileLink);
            }
        }
        return profileLinks;
    }
}
