package com.example.loran.nextthreedays;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Loran on 4/1/2015.
 * SEE:
 * http://www.vogella.com/tutorials/JavaNetworking/article.html
 */
public class Webpage {

    BufferedReader in;
    public Webpage(String urlText) {
        in  = null;
        try {
            URL url = new URL(urlText);
            in = new BufferedReader(new InputStreamReader(url.openStream()));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void parse() {

        String inputLine;
        try {
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
