import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DataHelper {
	
	private static final int MINUTE_MS = 1000 * 60;

	//A measurement is saved in the DB every 10 minutes
	//Have minimum precentage of measurements required for it to be worth to calculate average
	private static final long MEASUREMENTS_PER_DAY = (24*60)/10;
	private static final double MEASUREMENTS_REQUIRED = 0.8;
	private static final int MIN_NUMBER_OF_MEASUREMENTS = (int)((double)MEASUREMENTS_PER_DAY * MEASUREMENTS_REQUIRED);
	
	public DataHelper() {
		
	}
	
	//print out averages per day
	public static ArrayList<DailyAveragesItem> getAveragePerDay(ArrayList <LogItem> list, UiLogCallback logCallback){
		System.out.println("getAveragePerDay");
		logCallback.log("Starting averages calculation");
		Calendar newItemTime = Calendar.getInstance();
		Calendar previouseDayItemTime = Calendar.getInstance();
		boolean searchForFirstItem = true;
		ArrayList<DailyAveragesItem> dailysList = new ArrayList<DailyAveragesItem>();
		ArrayList<LogItem> currentDayList = new ArrayList<LogItem>();
		for (LogItem item: list) {
			//items come in order starting from oldes to newest
			//find first item which is after Midnight
			long epochTimeMs = item.getTime();
			newItemTime.setTimeInMillis(epochTimeMs);
			if (searchForFirstItem) {
				//Looking for start of next day
				int hour = newItemTime.get(Calendar.HOUR_OF_DAY);
				if (hour == 0) {
					//Item which starts a day found
					searchForFirstItem = false;
					currentDayList.add(item);
					previouseDayItemTime = (Calendar) newItemTime.clone();
					System.out.println("First item found at:");
					printOutDate(newItemTime);
				}
			} else {
				//First item already found
				if (newItemTime.get(Calendar.DAY_OF_MONTH) == previouseDayItemTime.get(Calendar.DAY_OF_MONTH) &&
						newItemTime.get(Calendar.MONTH) == previouseDayItemTime.get(Calendar.MONTH)) {
					//this item is still from current day
					currentDayList.add(item);
				} else {
					//The date of the new item is different
					//Check that the amount of items counted is correct and calculate average
					int numberOfItemsInCalculatedDay = currentDayList.size();
					if (numberOfItemsInCalculatedDay > MEASUREMENTS_PER_DAY) {
						System.out.println("ERROR: the items counted for the day is more than possible: " + numberOfItemsInCalculatedDay);
					} else if (numberOfItemsInCalculatedDay < MIN_NUMBER_OF_MEASUREMENTS) {
						System.out.println("ERROR: the items counted for the day is less than acceptable: " + numberOfItemsInCalculatedDay);
					} else {
						DailyAveragesItem daysAverages = DailyAveragesItem.getAveragesForDay(currentDayList);
						dailysList.add(daysAverages);			
					}
					previouseDayItemTime = (Calendar) newItemTime.clone();
					currentDayList.clear();
					//The item still needs to be added to next day
					currentDayList.add(item);
				}
				
			}
		}
		logCallback.log("Ending averages calculation");
		logCallback.log("Created " + dailysList.size() + " dailyLog items");
		System.out.println("Exiting average calculation");
		return dailysList;
	}
	
	
	private static final void printOutDate(Calendar date) {
		SimpleDateFormat  formmat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String formattedDate = formmat.format(date.getTime());
		System.out.println(formattedDate);
	}
}
