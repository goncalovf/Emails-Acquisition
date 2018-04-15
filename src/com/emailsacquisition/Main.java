package com.emailsacquisition;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        System.setProperty("jsse.enableSNIExtension", "false"); // https://stackoverflow.com/questions/7615645/ssl-handshake-alert-unrecognized-name-error-since-upgrade-to-java-1-7-0
        Set<String> profilesCrawled = new HashSet<>();
        Set<String> cvUrls = new HashSet<>();
        String[] locations = {"lisboa"};
        String[] subjects = {"Matemática"};
        for (String location : locations) {
            for (String subject : subjects) {
                String currentUrl = "https://www.explicas.me/index.php?op=explicadores&" + "local=" + location + "&disciplina=" + subject;
                Spider.get_profile_pages_urls(currentUrl);
                // for ( String profileUrl : profileUrls ) {
                //     if (profilesCrawled.contains(profileUrl)) {
                //         System.out.println("Profile already crawled.");
                //         continue;
                //     } else {
                //         profilesCrawled.add(profileUrl);
                //         // String cvUrl = Spider.get_cv_URL(profileUrl);
                //         // cvUrls.add(cvUrl);
                //     }
                // }

                // Hawk.print_results_title( webpage );
            }
        }
    }
}