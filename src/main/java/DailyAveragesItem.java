import java.util.ArrayList;
import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.Id;

//Object that contains averages of different data per day
@Entity(name="Sensor_station")
public class DailyAveragesItem extends SensorData {
	
	@Id
	private int daily_item_id = -1;
	private DateOnly date = null;
	// Number of items from whhihc the average was calculated
	private int numberOfItems = 0;

	public DailyAveragesItem() {
		//Default constructor for Hybernate
	}
	
	public DailyAveragesItem(double humidity, 
			double temperature, 
			int CO2, 
			int TVOC, 
			double temperature2,
			double pressure, 
			DateOnly date, 
			int numberOfItems) {
		super(humidity, temperature, CO2, TVOC, temperature2, pressure);
		this.date = date;
		this.numberOfItems = numberOfItems;
		calculateId();
	}

	// Get averahes item from list of log items
	// Items must be from the same day
	public static DailyAveragesItem getAveragesForDay(ArrayList<LogItem> list) {
		double humiditySum = 0;
		double temperatureSum = 0;
		double co2Sum = 0;
		double tvocSum = 0;
		double temperature2Sum = 0;
		double pressureSum = 0;

		for (LogItem item : list) {
			humiditySum += item.getHumidity();
			temperatureSum += item.getTemperature();
			co2Sum += item.getCO2();
			tvocSum += item.getTVOC();
			temperature2Sum += item.getTemperature2();
			pressureSum += item.getPressure();
		}

		int numerOfItems = list.size();

		double humidityAverage = humiditySum / numerOfItems;
		double temperatureAverage = temperatureSum / numerOfItems;
		double co2Average = co2Sum / numerOfItems;
		double tvocAverage = tvocSum / numerOfItems;
		double temperature2Average = temperature2Sum / numerOfItems;
		double pressureAverage = pressureSum / numerOfItems;

		// get date of items
		long timeOfItem = list.get(0).getTime();
		Calendar calendarTimeOfitem = Calendar.getInstance();
		calendarTimeOfitem.setTimeInMillis(timeOfItem);
		DateOnly itemDate = new DateOnly(calendarTimeOfitem);
		return new DailyAveragesItem(humidityAverage, temperatureAverage, (int) co2Average, (int) tvocAverage,
				temperature2Average, pressureAverage,
				itemDate,
				numerOfItems);
	}

	public DateOnly getDate() {
		return date;
	}

	public void setDate(DateOnly date) {
		this.date = date;
	}

	public int getNumberOfItems() {
		return numberOfItems;
	}

	public void setNumberOfItems(int numberOfItems) {
		this.numberOfItems = numberOfItems;
	}

	public int getDaily_item_id() {
		return daily_item_id;
	}

	public void setDaily_item_id(int daily_item_id) {
		this.daily_item_id = daily_item_id;
	}
	
	//Calculate ID of current object
	//Id is the number ade out of the date
	//If date is 2020.12.30 = 20201230
	private void calculateId() {
		this.daily_item_id = 
				(this.date.getYear() * 10000) +
				(this.date.getMonth() * 100) + 
				this.date.getDay();
	}

	public String toString() {
		String formattedTemperature = String.format("%.2f", temperature);
		String formattedTemperature2 = String.format("%.2f", temperature2);
		String formattedHumidity = String.format("%.2f", humidity);
		String formattedPressure = String.format("%.2f", pressure);

		String itemString = null;
		itemString = "Averages on " + date.toString() + ":\n" 
		+ "T1: " + formattedTemperature + " °C\n" 
				+ "T2: " + formattedTemperature2 + " °C\n" 
				+ "RH: " + formattedHumidity + " %\n" 
				+ "P: " + formattedPressure	+ " hPa\n" 
				+ "CO2: " + CO2 + " PPM\n"
				+ "TVOC: " + TVOC + " PPB\n"
				+ "ID: " + daily_item_id + "\n"
				+ "from " + numberOfItems + " items.";
		return itemString;
	}

}
