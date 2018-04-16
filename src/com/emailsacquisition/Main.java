package com.emailsacquisition;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;


/************************************************************************************************
 ************************************************************************************************
 *
 *              *****   ***   ***********   *****   ***   ********     **********
 *              ******  ***   ***     ***   ******  ***   ***    ***   ***    ***
 *              *** *** ***   ***     ***   *** *** ***   ***    ***   ***    ***
 *              ***  ******   ***********   ***  ******   ***    ***   ***    ***
 *              ***    ****   ***     ***   ***    ****   ********     **********
 *
 * O método main gera uma query (um link URL com uma query de local e disciplina) e vai a cada
 * um dos resultados (explicadores), abre a respetiva página de perfil e obtém o link para o CV.
 *
 * Não sei como queres obter o email a partir do link do CV. Ou seja, se queres:
 *
 * A) Obter o email um a um sempre que o método abrir a página de perfil de um explicador;
 * B) A partir de uma array com os links, correr um método próprio para obter os emails.
 *
 * [Suponho que sejam estas as opções mas, como sabes, sou novo nisto]
 *
 * Neste momento tenho pensado a opção (A). Isto porque estou a fazer um registo na BD sempre que
 * o método acaba de buscar o link para o CV no perfil do explicador. Mas é possível mudar suponho.
 *
 * Insere o teu código antes de:
 *      258     chrome.close();
 *      259     chrome.switchTo().window(profileListWindow);
 *
 * Obrigado ;)
 *
 ************************************************************************************************
 ************************************************************************************************/


public class Main {

    public static void main(String[] args) {
        System.setProperty("jsse.enableSNIExtension", "false"); // https://stackoverflow.com/questions/7615645/ssl-handshake-alert-unrecognized-name-error-since-upgrade-to-java-1-7-0
        Set<String> profilesCrawled = new HashSet<>(); // keep track so as not to crawl the same profile a second time
        String[] locations = {"lisboa"};
        String[] subjects = {"ingles"};
        String[] recordData;
        Integer queryID = 0;

        for (String location : locations) {
            for (String subject : subjects) {

                /*
                 * Insert query data into database
                 */
                queryID = queryID + 1;
                recordData = new String[]{location, subject};
                SQLiteJDBC.insertToDB("queries", queryID, recordData);

                /*
                 * Setup Chromedriver and open a window with list of profiles
                 */
                System.setProperty("webdriver.chrome.driver","C:\\Users\\gonca\\IdeaProjects\\exe\\chromedriver.exe");      // Get ChromeDriver
                WebDriver chrome = new ChromeDriver();
                String currentUrl = "https://www.explicas.me/index.php?op=explicadores&" + "local=" + location + "&disciplina=" + subject;
                chrome.get(currentUrl);
                String profileListWindow = chrome.getWindowHandle();

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
                 * Hide zendesk because it opens automatically (don't know why) and puts itself in front of page list options, blocking its view and executable actions
                 */
                WebElement zendesk = wait.until(new Function<WebDriver, WebElement>() {
                    public WebElement apply(WebDriver chrome) {
                        return chrome.findElement(By.cssSelector("div[data-test-id='ChatWidgetButton']"));
                    }
                });
                ((JavascriptExecutor)chrome).executeScript("arguments[0].style.visibility='hidden'", zendesk);

                /*
                 * Hide notice of cookies because it will put itself in front of "Search profile" buttons (links to profiles), disabling them for clicking
                 */
                WebElement chookiesNotice = chrome.findElement(By.cssSelector("body div:nth-child(3)"));
                ((JavascriptExecutor)chrome).executeScript("arguments[0].style.visibility='hidden'", chookiesNotice);

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
                    listPagesCount = chrome.findElements(By.cssSelector("ul.select-clone.custom-list li")).size();
                    pageSelectBox = chrome.findElement(By.cssSelector("div.properties-listing-footer.clearfix span.select-box"));

                    ((JavascriptExecutor) chrome).executeScript("window.scrollTo(0, document.body.scrollHeight)");
                } else {
                    listPagesCount = 1;
                }
                System.out.println("Number of pages: " + listPagesCount);

                /*
                 * MAIN LOOP: for each profile listed, open it in new window, get Url to CV, while navigating through profile list pages
                 */
                // Set<String> cvUrls = new HashSet<>(); // Apenas utilizado se se optar por (B) [ver topo]
                for (int k = 1; k <= listPagesCount; k++) {
                    /*
                     * Navigate to next profile list page
                     */
                    if (k > 1) {
                        Actions openPageSelectBox = new Actions(chrome);
                        openPageSelectBox.moveToElement(pageSelectBox).click().perform();
                        try {
                            Thread.sleep(2000);     // I cannot see condition that could be defined in this case
                        } catch (InterruptedException e) {
                            System.out.println("Did not sleep.");
                        }
                        WebElement pageListItem = chrome.findElement(By.cssSelector("span.select-box li[data-page='" + k + "']"));
                        Actions clickPageListItem = new Actions(chrome);
                        clickPageListItem.moveToElement(pageListItem).click().perform();
                        try {
                            Thread.sleep(3500);     // I cannot see condition that could be defined in this case
                        } catch (InterruptedException e) {
                            System.out.println("Did not sleep.");
                        }
                    }
                    /*
                     * Go to each profile and retrieve the CV Url
                     */
                    int profilesCount = chrome.findElements(By.cssSelector("ul#results_list li")).size();
                    System.out.println("Profiles in page: " + profilesCount);
                    for (int m = 1; m <= profilesCount; m++) {
                        /*
                         * These are the variables that we want to record in the DB. Listing for initialization and reset purposes
                         */
                        String tutorType = "";
                        String tutorFullName = "";
                        String tutorFirstName = "";
                        String tutorLastName = "";
                        String profileUrl = "";
                        String cvUrl = "";

                        /*
                         * Get profile Url for DB and to assess if profile has been crawled
                         */
                        String profileUrlElementSelector = String.format("ul#results_list li:nth-child(%d) a", m);      // Needs to be out of function
                        profileUrl = wait.until(new Function<>() {
                            public String apply(WebDriver chrome) {
                                WebElement profileUrlElement = chrome.findElement(By.cssSelector(profileUrlElementSelector));
                                return profileUrlElement.getAttribute("href");
                            }
                        });
                        if (profilesCrawled.contains(profileUrl)) {
                            System.out.println("Profile already crawled: " + profileUrl);
                        } else {
                            profilesCrawled.add(profileUrl);

                            /*
                             * Get profile type (tutor or study centre) for DB
                             */
                            tutorType = profileUrl.substring(profileUrl.indexOf("op=") + 3, profileUrl.indexOf("&id="));

                            /*
                             * Open profile page on new window
                             */
                            String openProfileButtonSelector = String.format("ul#results_list li:nth-child(%d) button.button.submit-btn.search-profile-button", m);     // Needs to be out of function
                            Boolean openedProfile = wait.until(new Function<>() {
                                public Boolean apply(WebDriver chrome) {
                                    WebElement openProfileButton = chrome.findElement(By.cssSelector(openProfileButtonSelector));
                                    Actions openProfile = new Actions(chrome);
                                    openProfile.moveToElement(openProfileButton).keyDown(Keys.SHIFT).click(openProfileButton).keyUp(Keys.SHIFT).build().perform();

                                    /*
                                     * Focus chrome driver on new window
                                     */
                                    Set<String> tabs_Set = chrome.getWindowHandles();
                                    List<String> tabs_List = new ArrayList<String>(tabs_Set);
                                    chrome.switchTo().window(tabs_List.get(1));

                                    return true;
                                }
                            });

                            if (openedProfile) {
                                /*
                                 * Wait for page to load
                                 */
                                new WebDriverWait(chrome, 15).until(
                                        webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));

                                /*
                                 * Get tutor full name and, if it's a person, split it to get first and last name, all for DB
                                 */
                                try {
                                    tutorFullName = chrome.findElement(By.tagName("h1")).getText();
                                    if (tutorType.equals("explicador")) {
                                        String[] tutorFullNameArray = tutorFullName.split(" ");
                                        Integer numberOfNames = tutorFullNameArray.length;
                                        tutorFirstName = tutorFullNameArray[0];
                                        String maybeTutorLastName = tutorFullNameArray[numberOfNames - 1];
                                        if (!maybeTutorLastName.equals(tutorFirstName)) {
                                            tutorLastName = maybeTutorLastName;
                                        }
                                    }
                                } catch (Exception e) {
                                    System.err.println(e.getMessage());
                                }

                                try {
                                    String cvOnClick = chrome.findElement(By.cssSelector("button.button.warning")).getAttribute("onclick");
                                    cvUrl = cvOnClick.substring(cvOnClick.indexOf("('") + 2, cvOnClick.indexOf("','"));
                                    // cvUrls.add(cvUrl); // Apenas utilizado se se optar por (B) [ver topo]
                                } catch (Exception e) {
                                    System.err.println(e.getMessage());
                                }
                                chrome.close();
                                chrome.switchTo().window(profileListWindow);
                            } else {
                                System.out.println("Could not open profile page.");
                            }

                            /*
                             * Insert tutor data into database
                             */
                            recordData = new String[]{tutorType, tutorFullName, tutorFirstName, tutorLastName, profileUrl, cvUrl, "Colocar aqui email"};
                            SQLiteJDBC.insertToDB("tutors", queryID, recordData);
                        }
                    }
                }
                chrome.close();
                System.out.println("Profiles crawled: " + profilesCrawled);
             }
         }
    }
}