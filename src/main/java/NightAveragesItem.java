import javax.persistence.Entity;


/*
 * Data from night time only
 * */
@Entity(name="Sensor_station_nightly")
public class NightAveragesItem extends DailyAveragesItem {
	private double sleepQuality= 0;
	//Heart rate
	private int averageHR = 0;
	
	
	
	public NightAveragesItem() {
		super();
	}

	public NightAveragesItem(DailyAveragesItem dailyItem, double sleepQuality, int averageHR) {
		super(dailyItem.getHumidity(),
				dailyItem.getTemperature(),
				dailyItem.getCO2(),
				dailyItem.getTVOC(),
				dailyItem.getTemperature2(),
				dailyItem.getPressure(),
				dailyItem.getDate(),
				dailyItem.getNumberOfItems());
		this.sleepQuality = sleepQuality;
		this.averageHR = averageHR;
		this.daily_item_id = this.daily_item_id + 100000000;
	}

	public double getSleepQuality() {
		return sleepQuality;
	}

	public void setSleepQuality(double sleepQuality) {
		this.sleepQuality = sleepQuality;
	}

	public int getAverageHR() {
		return averageHR;
	}

	public void setAverageHR(int averageHR) {
		this.averageHR = averageHR;
	}

	@Override
	public String toString() {
		String s = super.toString();
		return s + "\n" + 
				"SleepQuality: " + sleepQuality + "\nAverage HR: " + averageHR;
	}
	
	
}
