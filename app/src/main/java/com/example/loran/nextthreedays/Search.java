package com.example.loran.nextthreedays;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Loran on 4/21/2015.
 */
public class Search {

    private final static String mobileSite = "http://www.nextthreedays.com/mobile/mobilewebsite.cfm?";
    private Date date;
    private City city;
    private ArrayList<Type> types;
    private ArrayList<Event> events;

    public void Search(Date date, City city, ArrayList<Type> types) {
        this.date = date;
        this.city = city;
        this.types = types;
        performSearch();
    }

    private void performSearch() {
        //TODO KICK OFF SEARCH WITH PROVIDED PARAMETERS
        //TODO generate appropriate URL from fields
    }


}
