package mobile;

public class Event {

	private String place;
	private String time;
	private String price;
	private String location;
	private String description;
	
	public Event(String place, String time, String price, String location, String description)
	{
		this.place = place;
		this.time = time;
		this.price = price;
		this.location = location;
		this.description = description;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String toString(boolean b)
	{
		if (b)
			return place + " " + time + " " + price + " " + location + " " + description;
		else
			return place + "\n" + time + "\n" + price + "\n" + location + "\n" + description;
	}
}
