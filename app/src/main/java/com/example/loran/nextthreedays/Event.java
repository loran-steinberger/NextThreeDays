package com.example.loran.nextthreedays;

/**
 * Created by Loran on 4/16/2015.
 */
public class Event {

    final String title;
    final String location;
    final String time;
    final String price;
    final String category;
    final String description;

    public Event(String title, String location, String time, String price, String category, String description) {
        this.title = title;
        this.location = location;
        this.time = time;
        this.price = price;
        this.category = category;
        this.description = description;
    }

    public String toString() {
        return getTitle() + "\n" +
                getLocation() + "\n" +
                getTime() + "\n" +
                getPrice() + "\n" +
                getCategory() + "\n" +
                getDescription();
    }

    public String getTitle() {
        return title;
    }
    public String getLocation() {
        return location;
    }
    public String getTime() {
        return time;
    }
    public String getPrice() {
        return price;
    }
    public String getCategory() {
        return category;
    }
    public String getDescription() {
        return description;
    }
}
