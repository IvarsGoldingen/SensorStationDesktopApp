import java.util.Date;

//Object that contains averages of different data per day
public class DailyAveragesItem extends SensorData {	
	
	protected DateOnly date = null;
	
	public DailyAveragesItem(double humidity, 
			double temperature, 
			int CO2, 
			int TVOC, 
			double temperature2,
			double pressure,
			DateOnly date) {
		super(humidity, temperature, CO2, TVOC, temperature2, pressure);
		this.date = date;
	}

}
