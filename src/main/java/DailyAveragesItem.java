import java.util.Date;

//Object that contains averages of different data per day
public class DailyAveragesItem {
	private static final int NO_DATA_PROVIDED = -1;
	private int CO2 = NO_DATA_PROVIDED;
	private double humidity = NO_DATA_PROVIDED;
	private int TVOC = NO_DATA_PROVIDED;
	private double temperature = NO_DATA_PROVIDED;
	Date date = null;
	private double temperature2 = NO_DATA_PROVIDED;
	private double pressure = NO_DATA_PROVIDED;
	
	
	public DailyAveragesItem(double humidity, double temperature, int CO2, int TVOC, Date date, double temperature2,
			double pressure) {
		this.humidity = humidity;
		this.temperature = temperature;
		this.CO2 = CO2;
		this.TVOC = TVOC;
		this.date = date;
		this.temperature2 = temperature2;
		this.pressure = pressure;
	}


	public int getCO2() {
		return CO2;
	}


	public void setCO2(int cO2) {
		CO2 = cO2;
	}


	public double getHumidity() {
		return humidity;
	}


	public void setHumidity(double humidity) {
		this.humidity = humidity;
	}


	public int getTVOC() {
		return TVOC;
	}


	public void setTVOC(int tVOC) {
		TVOC = tVOC;
	}


	public double getTemperature() {
		return temperature;
	}


	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}


	public Date getDate() {
		return date;
	}


	public void setDate(Date date) {
		this.date = date;
	}


	public double getTemperature2() {
		return temperature2;
	}


	public void setTemperature2(double temperature2) {
		this.temperature2 = temperature2;
	}


	public double getPressure() {
		return pressure;
	}


	public void setPressure(double pressure) {
		this.pressure = pressure;
	}
	
	
	
}
