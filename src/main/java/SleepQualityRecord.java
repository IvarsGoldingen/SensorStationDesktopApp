import java.text.DecimalFormat;

public class SleepQualityRecord {
	final public static double MIN_SLEEP_HOURS = 7.0;
	final public static double SLEEP_HOURS_OVER_FOR_MAX_SCORE = 2.0;
	final public static double MAX_SCORE_FROM_SLEEP_LENGTH = 10.0;
	final public static double MIN_QUALITY_QOEFICIENT = 0.1;
	final public static double MAX_QUALITY_QOEFICIENT = 2.0;
	final public static double MAX_SCORE_FROM_SLEEP_QUALITY = 90.0;
	
	
	private DateOnly date = null;
	private double sleepQuality= 0;
	//Heart rate
	private int averageHR = 0;
	
	public SleepQualityRecord(DateOnly date, double sleepQuality, int averageHR) {
		this.date = date;
		this.sleepQuality = sleepQuality;
		this.averageHR = averageHR;
	}

	public DateOnly getDate() {
		return date;
	}

	public void setDate(DateOnly date) {
		this.date = date;
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
		DecimalFormat decForm = new DecimalFormat("#.##");
		return date + "\t Quality score: " + decForm.format(sleepQuality) + " HR: " + averageHR + "]";
	}
	
	
	
	
}
