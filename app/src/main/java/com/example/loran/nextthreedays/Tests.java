package com.example.loran.nextthreedays;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Tests {
	/*public static void main(String[] args) throws IOException {
		ArrayList<Event> eventsList = insertDate(4, 24, 15);
	}*/

//	protected static ArrayList<Event> insertDate(int month, int day, int year)
    protected static ArrayList<Event> insertDate(String url)
			throws IOException {

		// The url will be changed depends on the input date.
		//String url = "http://nextthreedays.com/mobile/AjaxGetDayEvents.cfm?Date="
		//		+ month + "/" + day + "/" + year + "&c=&t=";

		// Create and get the document
		Document doc = Jsoup.connect(url).get();

		// replace the line with this bar ( | )
		String temp = doc.html().replace ("<hr>", "|");
		doc = Jsoup.parse(temp);

		// replace the break line with dollar sign.
		temp = doc.html().replace("<br>", ">>");
		doc = Jsoup.parse(temp);

		String text = doc.body().text();
		String[] events = text.split("\\|");
		
		ArrayList<Event> list = new ArrayList<Event>();
		
		for (int i = 1; i < events.length; i++) {
			String[] infos = events[i].split(">>");
			
			Event e = new Event(infos[0], infos[1], infos[2], infos[3], infos[4], infos[5]);
			list.add(e);
			
			//System.out.println("---------------------------------------------------------------");
			//System.out.println(e.toString(false));
		}
		return list;
	}

}