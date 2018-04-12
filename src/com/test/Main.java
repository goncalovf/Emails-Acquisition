package com.test;

import org.jsoup.nodes.Document;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        System.setProperty("jsse.enableSNIExtension", "false"); // https://stackoverflow.com/questions/7615645/ssl-handshake-alert-unrecognized-name-error-since-upgrade-to-java-1-7-0
        Set<String> profilesCrawled = new HashSet<String>();
        String[] locations = {"Porto"};
        String[] subjects = {"matematica"};
        for (String location : locations) {
            for (String subject : subjects) {
                String currentUrl = "https://www.explicas.me/index.php?op=explicadores&" + "local=" + location + "&disciplina=" + subject;
                Document webpage = Hawk.connect_to(currentUrl);
                List<String> profileUrls = Spider.get_profile_links( webpage );
                for ( String profileUrl : profileUrls ) {
                    if (profilesCrawled.contains(profileUrl)) {
                        continue;
                    } else {
                        profilesCrawled.add(profileUrl);
                        webpage = Hawk.connect_to(profileUrl);
                        String fileURL = Hawk.get_file_URL(webpage);
                    }
                }
            }
        }
    }
}