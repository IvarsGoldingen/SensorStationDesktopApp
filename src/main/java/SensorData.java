
public class SensorData {
	protected static final int NO_DATA_PROVIDED = -1;
	protected int CO2 = NO_DATA_PROVIDED;
	protected double humidity = NO_DATA_PROVIDED;
	protected int TVOC = NO_DATA_PROVIDED;
	protected double temperature = NO_DATA_PROVIDED;
	protected double temperature2 = NO_DATA_PROVIDED;
	protected double pressure = NO_DATA_PROVIDED;

	public SensorData() {
		
	}
	
	public SensorData(double humidity, double temperature, int CO2, int TVOC, double temperature2, double pressure) {
		this.humidity = humidity;
		this.temperature = temperature;
		this.CO2 = CO2;
		this.TVOC = TVOC;
		this.temperature2 = temperature2;
		this.pressure = pressure;
	}

	public int getCO2() {
		return CO2;
	}

	public void setCO2(int CO2) {
		this.CO2 = CO2;
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

	public void setTVOC(int TVOC) {
		this.TVOC = TVOC;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
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
