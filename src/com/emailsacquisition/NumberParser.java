package com.emailsacquisition;

import java.io.*;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.text.PDFTextStripper;

import java.util.regex.*;

public class NumberParser {

    public static void main(String[] args) {

        PDDocument pd;

        try {

            File input = new File("file.pdf");

            StringBuilder sb = new StringBuilder();
            pd = PDDocument.load(input);
            PDFTextStripper stripper = new PDFTextStripper();

            sb.append(stripper.getText(pd));

            Pattern p = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

            Matcher m = p.matcher(sb);

            DBConnector db = new DBConnector();

            while (m.find()) {

                db.writeMail(m.group());

            }

            if (pd != null) {
                pd.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}