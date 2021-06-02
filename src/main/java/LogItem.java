
public class LogItem extends SensorData{
	protected long Time = NO_DATA_PROVIDED;

	// empty constructor for FireBase
	public LogItem() {

	}

	public LogItem(double humidity, double temperature, int CO2, int TVOC, double temperature2,
			double pressure, long Time) {
		super(humidity, temperature, CO2, TVOC, temperature2, pressure);
		this.Time = Time;
	}

	public long getTime() {
		return Time;
	}

	public void setTime(long time) {
		this.Time = time;
	}
}
