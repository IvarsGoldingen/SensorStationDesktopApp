import java.util.Calendar;

import javax.persistence.Embeddable;

//Needed so data from this object can be saved in a DB table which is created from an object which use this one
@Embeddable
public class DateOnly {
	protected static final int NO_DATA_PROVIDED = -1;
	private int day = NO_DATA_PROVIDED;
	private int month = NO_DATA_PROVIDED;
	private int year = NO_DATA_PROVIDED;
	
	public DateOnly(int day, int month, int year) {
		this.day = day;
		this.month = month;
		this.year = year;
	}
	public DateOnly (Calendar cal) {
		this.day = cal.get(Calendar.DAY_OF_MONTH); 
		this.month = cal.get(Calendar.MONTH) + 1; // January is 0 in calendar
		this.year = cal.get(Calendar.YEAR);
	}
	
	
	public DateOnly() {
		// TODO Auto-generated constructor stub
	}

	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	
	public String toString() {
		String formattedYear = String.format("%02d", year);
		String formattedMonth = String.format("%02d", month);
		String formattedDay = String.format("%02d", day);
		return formattedYear + "." + formattedMonth + "." + formattedDay;
	}
	
	public long getEpochMs() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, (month - 1), day);//-1 because for Calendar Jan is 0
		return calendar.getTimeInMillis();
	}
	
	public static DateOnly getDateOnlyFromCal(Calendar cal) {
		return new DateOnly(cal.get(Calendar.DAY_OF_MONTH), 
				cal.get(Calendar.MONTH) + 1, // January is 0
				cal.get(Calendar.YEAR));
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DateOnly) {
			DateOnly ob = (DateOnly)obj;
			if (this.day == ob.getDay() &&
					this.month == ob.getMonth() &&
					this.year == ob.getYear()) {
				return true;
			}
		} 
		return false;
	}
	
	
}
