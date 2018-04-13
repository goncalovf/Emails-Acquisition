package com.emailsacquisition;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.Keys;
import java.util.ArrayList;
import java.util.List;


public class Spider {
    public static void expand_search_criteria(String currentUrl) {
        System.setProperty("webdriver.chrome.driver","C:\\Users\\gonca\\IdeaProjects\\exe\\chromedriver.exe");      // Get ChromeDriver
        WebDriver chrome = new ChromeDriver();
        chrome.get(currentUrl);
        Boolean modalActive = chrome.findElement(By.className("modal-dialog")).isDisplayed();                       // When subject is available in different levels of education, a modal appears. Need to close it
        if (modalActive) {
            WebElement closeModalButton= chrome.findElement(By.cssSelector("button[data-dismiss='modal']"));
            closeModalButton.click();
        }
        List<WebElement> handles = chrome.findElements(By.className("ui-slider-handle"));                           // Get list of handles
        Integer i = 1;
        Integer numberRightClicks = 0;
        for (WebElement handle : handles) {
            if (i == 1) numberRightClicks = 10;             // Price
            else if (i == 2) numberRightClicks = 20;        // Distance
            else if (i == 3) break;                         // Reputation
            for (int j = 1; j <= numberRightClicks; j++) {
                handle.sendKeys(Keys.ARROW_RIGHT);
            }
            i++;
        }
        String resultsTitle = chrome.findElement(By.id("results-title")).getText();
        System.out.println("Number of results: " + resultsTitle);
    }


    public static List<String> get_profile_links(Document webpage) {
        List<String> profileLinks = new ArrayList<String>();
        Elements linksAndChildren = webpage.select("ul#results_list a[href*=id]");
        for (Element uniqueElement : linksAndChildren) {
            if (uniqueElement.tagName().equals("a")) {
                String profileLink = "https://www.explicas.me/" + uniqueElement.attr("href");
                profileLinks.add(profileLink);
            }
        }
        return profileLinks; // Until now only gathers the 30 that come in the first page. Need to extend list
    }






}
