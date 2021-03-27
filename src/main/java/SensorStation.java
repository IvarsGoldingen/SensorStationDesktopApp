public class SensorStation {
	// String keys for the variables in the database
	public static final String CO2_KEY = "CO2";
	public static final String TVOC_KEY = "TVOC";
	public static final String T1_KEY = "temperature";
	public static final String T2_KEY = "temperature2";
	public static final String PRESSURE_KEY = "pressure";
	public static final String HUMIDITY_KEY = "humidity";
	public static final String LAST_UPDATE_KEY = "LastUpdate";
	public static final String DHT_VALID_KEY = "DHT_valid";
	public static final String SGP_VALID_KEY = "SGP_valid";

	// iNT keys for the variables to use when passing values
	public static final byte NUM_CO2_KEY = 10;
	public static final byte NUM_TVOC_KEY = 11;
	public static final byte NUM_T1_KEY = 12;
	public static final byte NUM_T2_KEY = 13;
	public static final byte NUM_PRESSURE_KEY = 14;
	public static final byte NUM_HUMIDITY_KEY = 15;
	public static final byte NUM_LAST_UPDATE_KEY = 16;
	public static final byte NUM_DHT_VALID_KEY = 17;
	public static final byte NUM_SGP_VALID_KEY = 18;

	private static final int NO_DATA_PROVIDED = -1;
	private int CO2 = NO_DATA_PROVIDED;
	private double humidity = NO_DATA_PROVIDED;
	private int TVOC = NO_DATA_PROVIDED;
	private double temperature = NO_DATA_PROVIDED;
	private double temperature2 = NO_DATA_PROVIDED;
	private double pressure = NO_DATA_PROVIDED;
	private boolean DHT_valid = false;
	private boolean SGP_valid = false;
	private long LastUpdate = NO_DATA_PROVIDED;

	// empty constructor for FireBase
	public SensorStation() {

	}

	public SensorStation(double humidity, double temperature, int CO2, int TVOC, boolean DHT_valid, boolean SGP_valid,
			double temperature2, double pressure, long lastUpdate) {
		this.humidity = humidity;
		this.temperature = temperature;
		this.CO2 = CO2;
		this.TVOC = TVOC;
		this.DHT_valid = DHT_valid;
		this.SGP_valid = SGP_valid;
		this.temperature2 = temperature2;
		this.pressure = pressure;
		this.LastUpdate = lastUpdate;
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

	public boolean isDHT_valid() {
		return DHT_valid;
	}

	public void setDHT_valid(boolean DHT_valid) {
		this.DHT_valid = DHT_valid;
	}

	public boolean isSGP_valid() {
		return SGP_valid;
	}

	public void setSGP_valid(boolean SGP_valid) {
		this.SGP_valid = SGP_valid;
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

	public long getLastUpdate() {
		return LastUpdate;
	}

	public void setLastUpdate(long lastUpdate) {
		this.LastUpdate = lastUpdate;
	}
}
