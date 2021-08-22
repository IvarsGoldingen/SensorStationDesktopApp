import javax.persistence.Entity;


/*
 * Data from night time only
 * */
@Entity(name="Sensor_station_nightly")
public class NightAveragesItem extends DailyAveragesItem {
	private double sleepQuality= 0;
	//Heart rate
	private int averageHR = 0;
	
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
}
