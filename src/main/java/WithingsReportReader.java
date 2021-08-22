import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class WithingsReportReader {
	final private static String PATH_TO_SLEEP_DATA = "D:/Ivars/Withings/sleep.csv";
	final private static int FROM_INDEX = 0;
	final private static int TO_INDEX = 1;
	final private static int LIGHT_SLEEP_INDEX = 2;
	final private static int DEEP_SLEEP_INDEX = 3;
	final private static int HEART_RATE_INDEX = 11;
	
	final private static double VALID_SLEEP_MIN = 4.0;
	final private static double VALID_SLEEP_MAX = 11.0;
	final private static int VALID_SLEEP_END_FROM = 5;
	final private static int VALID_SLEEP_END_TO = 11;
	
	public WithingsReportReader() {
		
	}
	
	public ArrayList <SleepQualityRecord> getWithingsData(){
		File file = new File(PATH_TO_SLEEP_DATA);
		
		if (file.isFile()) {
			FileReader fr = null;
			try {
				fr = new FileReader(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}	
			
			BufferedReader br = new BufferedReader(fr);
			
			String withingsReportLine;
			try {
				ArrayList <SleepQualityRecord> list = new ArrayList<SleepQualityRecord>();
				withingsReportLine = br.readLine();
				int cntr = 0;
				while (withingsReportLine != null ) {
					if (cntr > 0) {
						list.add(getSleepRecord(withingsReportLine));
					}
					cntr++;
					withingsReportLine = br.readLine();
				}
				br.close();
				return list;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} else {
			System.out.println("No file found");
			return null;
		}
	}
	
	private static SleepQualityRecord getSleepRecord(String withingsReportLine) {
		String [] data = withingsReportLine.split(",");
		
		DateOnly date = processTime(data[FROM_INDEX], data[TO_INDEX]);
		if (date == null) {
			//Record was not valid
			return null;
		}
		double sleepQuality = calculateSleepQuality(data[LIGHT_SLEEP_INDEX], data[DEEP_SLEEP_INDEX]);
		int sleepHR = Integer.parseInt(data[HEART_RATE_INDEX]);
		return new SleepQualityRecord(date, sleepQuality, sleepHR);
	}
	
	//Returns null if item not valid
		private static DateOnly processTime(String from, String to) {
			//Date left, time right. Ex: 2019-09-18T19:42:00+03:00
			Calendar fromCal = getCalendarFromString(from);
			Calendar toCal = getCalendarFromString(to);
			boolean timeIsValid = dateDataValid(fromCal, toCal);
			if (timeIsValid) {
				System.out.println("Time is valid");
				return new DateOnly(toCal);
			} else {
				System.out.println("Time is not valid");
				return null;
			}
		}
		
		private static Calendar getCalendarFromString(String s) {
			String [] dateTimeArr = s.split("T");
			String [] dateArr = dateTimeArr[0].split("-");
			int year = Integer.parseInt(dateArr[0]);
			int month = Integer.parseInt(dateArr[1]);
			int date = Integer.parseInt(dateArr[2]);
			String [] preTimeArr = dateTimeArr[1].split("\\+");
			String [] timeArr = preTimeArr[0].split(":");
			int hour = Integer.parseInt(timeArr[0]);
			int minute = Integer.parseInt(timeArr[1]);
			int second = Integer.parseInt(timeArr[2]);
			//System.out.println("Time: " + year + "."+ month + "."+ date + " "+ hour + ":"+ minute + ":"+ second);
			
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month - 1);//-1 because for Calendar Jan is 0
			cal.set(Calendar.DATE, date);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
			cal.set(Calendar.SECOND, second);
			return cal;
		}
		
		/*
		 * Check if the sleep record is valid for saving in DB
		 * Should be atleast 4 hours
		 * Should end in the current days between 5:00 and 10:00
		 * 
		 * */
		private static boolean dateDataValid(Calendar from, Calendar to) {
			long fromMillis = from.getTimeInMillis();
			long toMillis = to.getTimeInMillis();
			long sleepLenghtMillis = toMillis - fromMillis;
			double sleepLengthH = ((double)(sleepLenghtMillis/1000))/3600;
			System.out.println("Sleep lenght hours: " + sleepLengthH);
			
			int sleepEndHour = to.get(Calendar.HOUR_OF_DAY);
			
			if (sleepLengthH > VALID_SLEEP_MIN &&
					sleepLengthH < VALID_SLEEP_MAX &&
					sleepEndHour >= VALID_SLEEP_END_FROM &&
					sleepEndHour <= VALID_SLEEP_END_TO) //9:59 last valid
			{
				
				return true;
			}
			
			return false;
		}
		
		private static double calculateSleepQuality(String light, String deep) {
			double lightSleep = Integer.parseInt(light);
			double deepSleep = Integer.parseInt(deep);
			double total = lightSleep + deepSleep;
			double scoreFromLenght = 0;
			double scoreFromQuality = 0;
			double sleepQualityScore = 0;
			double total_h = ((double)total)/3600;
			double difference = total_h - SleepQualityRecord.MIN_SLEEP_HOURS;
			if (difference < 0) {
				scoreFromLenght = 0;
			} else if (difference > SleepQualityRecord.SLEEP_HOURS_OVER_FOR_MAX_SCORE) {
				scoreFromLenght = SleepQualityRecord.MAX_SCORE_FROM_SLEEP_LENGTH; 
			} else {
				//interpolate score
				scoreFromLenght = (difference/ SleepQualityRecord.SLEEP_HOURS_OVER_FOR_MAX_SCORE)  * SleepQualityRecord.MAX_SCORE_FROM_SLEEP_LENGTH;
			}
			//Calculate score from quality
			double qualityCoef = deepSleep/lightSleep;
			if (qualityCoef < SleepQualityRecord.MIN_QUALITY_QOEFICIENT) {
				scoreFromQuality = 0;
			} else if (qualityCoef > SleepQualityRecord.MAX_QUALITY_QOEFICIENT) {
				scoreFromQuality = SleepQualityRecord.MAX_SCORE_FROM_SLEEP_QUALITY;
			} else {
				//interpolate score
				scoreFromQuality = SleepQualityRecord.MAX_SCORE_FROM_SLEEP_QUALITY *((qualityCoef - SleepQualityRecord.MIN_QUALITY_QOEFICIENT) / (SleepQualityRecord.MAX_QUALITY_QOEFICIENT - SleepQualityRecord.MIN_QUALITY_QOEFICIENT) ) ;
			}
			sleepQualityScore = scoreFromLenght + scoreFromQuality;
			return sleepQualityScore;
		}
}
