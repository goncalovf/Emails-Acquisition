package com.emailsacquisition;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
 * O método get_cv_emails recebe uma query (um link URL com uma query de local e disciplina) e vai
 * a cada um dos resultados (explicadores), abre a respetiva página de perfil e obtém o link para
 * o CV.
 *
 * Não sei como queres obter o email a partir do link do CV. Ou seja, se queres:
 *
 * A) Obter o email um a um sempre que o método abrir a página de perfil de um explicador;
 * B) A partir de uma array com os links, correr um método próprio para obter os emails.
 *
 * [Suponho que sejam estas as opções mas, como sabes, sou novo nisto]
 *
 * Neste momento tenho pensado na opção (A). Isto porque estou a fazer um registo na BD sempre que
 * o método acaba de buscar o link para o CV no perfil do explicador. Mas é possível mudar suponho.
 *
 * Insere o teu código antes do « chrome.close(); » [linha 229]
 *
 * Obrigado ;)
 *
 ************************************************************************************************
 ************************************************************************************************/

public class Spider {
    public static void get_cv_emails(String currentUrl) {

    }

    /**
     *  get_profile_links works, but now we're using selenium to get profile urls since we have to use javascript to manipulate search criteria and list navigation
     *
     *  @deprecated use {@link #get_cv_emails(String CurrentUrl)} instead
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
