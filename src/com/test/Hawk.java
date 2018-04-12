package com.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.awt.*;
import java.net.URI;

public class Hawk {

    public static Document connect_to( String currentUrl ) {
        try {
            Document webpage = Jsoup.connect( currentUrl ).get();
            System.out.println("Connected to " + currentUrl);
            return webpage;
        } catch ( Exception noConnection ) {
            System.out.println("Could not connect to " + currentUrl);
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
                noConnection.printStackTrace();
            }
        } catch ( Exception URISyntaxException) {
            System.out.println("Something wrong with url: " + currentUrl);
        }

    }

    public static String get_file_URL( Document webpage ) {
        try {
            Elements cvButton = webpage.getElementsByClass("button warning");
            String onClickString = cvButton.attr("onclick");
            String fileURL = onClickString.substring(onClickString.indexOf("('") + 2, onClickString.indexOf("','"));
            System.out.println("Found file with link to " + fileURL);
            return fileURL;
        } catch ( Exception noButton ) {
            System.out.println("Could not find button.");
        }
        return "Error";
    }
}