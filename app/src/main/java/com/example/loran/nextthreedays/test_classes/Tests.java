package mobile;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Tests {
	public static void main(String[] args) throws IOException {
		insertDate(4, 24, 15);
	}

	protected static void insertDate(int month, int day, int year)
			throws IOException {

		// create a document
		Document doc;

		// The url will be changed depends on the input date.
		String url = "http://nextthreedays.com/mobile/AjaxGetDayEvents.cfm?Date="
				+ month + "/" + day + "/" + year + "&c=&t=";

		// get the document
		doc = Jsoup.connect(url).get();

		// replace the line with this bar ( | )
		String temp = doc.html().replace("<hr>", "|");
		doc = Jsoup.parse(temp);

		// replace the break line with dollar sign.
		temp = doc.html().replace("<br>", ">>");
		doc = Jsoup.parse(temp);

		String[] events = doc.body().ownText().split("\\|");
		
		ArrayList<Event> list = new ArrayList();
		
		for (int i = 1; i < events.length; i++) {
			String[] infos = events[i].split(">>");
			
			Event e = new Event(infos[1], infos[2], infos[3], infos[4], infos[5]);
			list.add(e);
			//for (int j = 1; j < infos.length - 1; j++) 
				//System.out.println(j + " :" + infos[j]);
			
			System.out.println("**************");
			System.out.println(e.toString(false));		
		}
		//System.out.println("list.size() " + list.size() + "\nevents.length " + events.length);
		
	}

}
