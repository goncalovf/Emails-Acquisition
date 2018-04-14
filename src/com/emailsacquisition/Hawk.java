package com.emailsacquisition;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.awt.*;
import java.net.URI;

public class Hawk {

    public static Document parse_html( String url ) {
        try {
            Document webpage = Jsoup.connect(url).get();
            System.out.println("Connected to " + url);
            return webpage;
        } catch ( Exception noConnection ) {
            System.out.println("Could not connect to " + url);
            noConnection.printStackTrace();
            return null;
        }
    }

    public static void open_url( String currentUrl ) {
        Desktop desktop = Desktop.getDesktop();
        try {
            URI oURL = new URI(currentUrl);
            try {
                desktop.browse(oURL);
                System.out.println("Opened " + currentUrl);
            } catch ( Exception noConnection ) {
                System.out.println("Could not open " + currentUrl);
                // noConnection.printStackTrace();
            }
        } catch ( Exception URISyntaxException) {
            System.out.println("Something wrong with url: " + currentUrl);
        }

    }

    public static String get_cv_URL( String profileUrl ) {
        Document webpage = parse_html(profileUrl);
        try {
            Elements cvButton = webpage.getElementsByClass("button warning");
            String onClickString = cvButton.attr("onclick");
            try {
                String cvUrl = onClickString.substring(onClickString.indexOf("('") + 2, onClickString.indexOf("','"));
                System.out.println("Found file with link to " + cvUrl);
                return cvUrl;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } catch ( Exception e ) {
            System.out.println(e.getMessage());
        }
        return "Error";
    }

    public static void print_results_title( Document webpage ) {
        String resultsTitle = webpage.select("span#results-title").text();
        System.out.println(resultsTitle);
    }

}