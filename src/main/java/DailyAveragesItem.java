import java.util.ArrayList;
import java.util.Calendar;

//Object that contains averages of different data per day
public class DailyAveragesItem extends SensorData {

	private DateOnly date = null;
	// Number of items from whhihc the average was calculated
	private int numberOfItems = 0;

	public DailyAveragesItem(double humidity, double temperature, int CO2, int TVOC, double temperature2,
			double pressure, DateOnly date, int numberOfItems) {
		super(humidity, temperature, CO2, TVOC, temperature2, pressure);
		this.date = date;
		this.numberOfItems = numberOfItems;
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

		return new DailyAveragesItem(humidityAverage, temperatureAverage, (int) co2Average, (int) tvocAverage,
				temperature2Average, pressureAverage,
				new DateOnly(calendarTimeOfitem.get(Calendar.DAY_OF_MONTH), calendarTimeOfitem.get(Calendar.MONTH) + 1, // January
																														// is
																														// 0
						calendarTimeOfitem.get(Calendar.YEAR)),
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

	public String toString() {
		String formattedTemperature = String.format("%.2f", temperature);
		String formattedTemperature2 = String.format("%.2f", temperature2);
		String formattedHumidity = String.format("%.2f", humidity);
		String formattedPressure = String.format("%.2f", pressure);

		String itemString = null;
		itemString = "Averages on " + date.toString() + ":\n" + "T1: " + formattedTemperature + " °C\n" + "T2: "
				+ formattedTemperature2 + " °C\n" + "RH: " + formattedHumidity + " %\n" + "P: " + formattedPressure
				+ " hPa\n" + "CO2: " + CO2 + " PPM\n" + "TVOC: " + TVOC + " PPB\n" + "from " + numberOfItems
				+ " items.";
		return itemString;
	}

}
