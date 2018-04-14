package com.emailsacquisition;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


public class Spider {
    public static void expand_search_criteria(String currentUrl) {
        System.setProperty("webdriver.chrome.driver","C:\\Users\\gonca\\IdeaProjects\\exe\\chromedriver.exe");      // Get ChromeDriver
        WebDriver chrome = new ChromeDriver();
        chrome.get(currentUrl);
        Boolean modalActive = chrome.findElement(By.className("modal-dialog")).isDisplayed();                       // When subject is available in different levels of education, a modal appears. Need to close it
        if (modalActive) {
            WebElement closeModalButton = chrome.findElement(By.cssSelector("button[data-dismiss='modal']"));
            closeModalButton.click();
        }
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
        List<String> profileUrls = new ArrayList<String>();
        int profilesCount = chrome.findElements(By.cssSelector("ul#results_list li")).size();
        System.out.println("Count: " + profilesCount);
        for (int k = 1; k <= profilesCount; k++) {
            try {
                new WebDriverWait(chrome, 7)
                        .ignoring(StaleElementReferenceException.class)
                        .until(ExpectedConditions.attributeContains(By.cssSelector("ul#results_list li:nth-child(" + k + ") a"), "href", "id"));
                String profileUrl = chrome.findElement(By.cssSelector("ul#results_list li:nth-child(" + k + ") a")).getAttribute("href");
                profileUrls.add(profileUrl);
            } catch (Exception e) {
                System.out.println("Stale Element Reference at k = " + k);
            }
        }

    }

    /**
     *  get_profile_links works, but now we're using selenium to get profile urls since we have to use javascript to manipulate search criteria and list navigation
     *
     *  @deprecated use {@link #expand_search_criteria(String CurrentUrl)} instead
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
