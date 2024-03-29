import java.awt.EventQueue;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import org.slf4j.LoggerFactory;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/*
 * App that receives sensor data stored in google firebase
 * */
/*
 * TODOs:
 * 
 * 2. Read data from withings reports
 * 2.1 Save in DB
 * 3. New Graph and DB etc for nightly averages. ompare these with withings data
 * 4. get averages only if necessary
 * 
 * */
public class Main {

	private static CurrentDataCallback fireBaseCallbacks;
	private static UiLogCallback logCallback;
	private static UICallbacks btnsCallback;
	//Database callback
	private static DBCallbacks dbCb;
	// Current sensor data
	private static SensorStation currentData;
	// UI class
	private static UI ui;
	// Takes care of getting data from Firebase
	private static FirebaseHelper firebase;
	//List of log data
	private static ArrayList<LogItem> logs = null;
	//List of daily data
	private static ArrayList<DailyAveragesItem> dailyLogs = null;
	//Database which stores daily averages items
	private static DbHelper db;
	//list of items from withings report
	private static ArrayList <SleepQualityRecord> qualityList;
	
	public static void main(String[] args) {
		turnOffLoggers();
		System.out.println("Sensor station start. Boo Boo is the best");
		createUI();
		ui.logLine("Start");
		//Create callback for logs
		createLogCallback();
		//Create callbacks for UI logging
		createUICallbacks();
		//Create callback for database
		createDbCb();
		//Database
		db = new DbHelper(logCallback, dbCb);
		
		Thread t1 = new Thread (db);
		t1.start();
		//TODO: get data with callback
		//dailyLogs = db.getAllItems();
		// This object will be populated with data from the stream
		currentData = new SensorStation();
		ui.logLine("Connecting to Firebase");
		connectToFirebase();
		firebase.startStream();
		firebase.getLogs();
	}
	
	//get This data when DB finishes loading
	private static void createDbCb() {
		dbCb = new DBCallbacks() {
			@Override
			public void returnDailyAvaragesList(ArrayList <DailyAveragesItem> list) {
				dailyLogs = list;
			}
		};
	}

	private static void createUI() {
		createUICallbacks();
		ui = new UI(btnsCallback);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ui.setJFrameVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Start streaming live data from google firebase
	private static void connectToFirebase() {
		createFirebaseCallbacks();
		firebase = new FirebaseHelper(fireBaseCallbacks);
	}
	
	private static void createUICallbacks() {
		btnsCallback = new UICallbacks() {

			@Override
			public void openRecentGraphsButtonPressed() {
				// TODO Auto-generated method stub
				System.out.println("openRecentGraphsButtonPressed()");
				if (logs != null) {
					createLogItemGraphs(logs);
				} else {
					JOptionPane.showMessageDialog(null, "Logs are not yet downloaded. Feed BooBoo ");
				}
			}

			@Override
			public void openAveragesGraphsButtonPressed() {
				// TODO Auto-generated method stub
				System.out.println("openAveragesGraphsButtonPressed()");
				if (dailyLogs != null) {
					createDailyItemGraphs(dailyLogs);
				} else {
					JOptionPane.showMessageDialog(null, "Daily logs are not yet downloaded");
				}
			}

			@Override
			public void openNightGraphButtonPressed() {
				// TODO Auto-generated method stub
				System.out.println("openNightGraphButtonPressed()");
				if (db.isInitialized()) {
					drawNightGraph();
				} else {
					JOptionPane.showMessageDialog(null, "DB not yet initialized");
				}
				
			}
		};
	}
	
	private static void drawNightGraph() {
		/*
		 * 1. Check how many night items currently in DB. Get item with latest date.
		 * 2. If latest date is today then just draw
		 * 3. If not add latest data to night items
		 * 4. Add data from withings reports to finish the data
		 * 5. When tested instead of copying averages data calculate new ones for night
		 * */
		ArrayList<NightAveragesItem> list = db.getAllItems(NightAveragesItem.class);
		if (list == null) {
			createNightItemDB();
			System.out.println("List is null");
		} else {
			int listSize = list.size();
			if (listSize == 0) {
				createNightItemDB();
				
			} else {
				System.out.println("List size: " + list.size());
			}
			
		}
	}
	
	private static void createNightItemDB() {
		ArrayList <NightAveragesItem> nightItems = new ArrayList<NightAveragesItem>();
		createQualityListFromReport();
		//getFirstDailyItem
		DailyAveragesItem firstItem = dailyLogs.get(0);
		//get id of appropriate withings item
		DateOnly firstItemDate = firstItem.getDate();
		int idOfFirstReportItem = findFirstSleepRecord(firstItemDate);
		int reportsCounter = idOfFirstReportItem;
		int sizeOfQualityList = qualityList.size();
		for (DailyAveragesItem dailyItem: dailyLogs) {
			//for each daily item create a night item
			DateOnly timeOfDailyItem = dailyItem.getDate();
			int innerCntr = reportsCounter;
			while (true) {
				SleepQualityRecord sameDayQualityRe = qualityList.get(innerCntr);
				if (timeOfDailyItem.equals(sameDayQualityRe.getDate())) {
					NightAveragesItem itemToAdd = new NightAveragesItem(dailyItem,
							sameDayQualityRe.getSleepQuality(),
							sameDayQualityRe.getAverageHR());
					nightItems.add(itemToAdd);
					break;
				}
				innerCntr++;
				if (innerCntr >= sizeOfQualityList) {
					System.out.println("Unable to find night data on " + timeOfDailyItem);
					break;
				}
			}
			reportsCounter++;
		}
		
		for (NightAveragesItem item: nightItems) { 
			System.out.println(item);
			System.out.println("______________________________________________");
		}
		
		System.out.println("Number of daily items: " + dailyLogs.size());
		System.out.println("Number of night items: " + nightItems.size());
		db.saveItem(nightItems.get(0));
		//db.saveItems(NightAveragesItem.class, nightItems);
	}
	
	
	
	//get list of items from withings file
	private static void createQualityListFromReport() {
		WithingsReportReader reader = new WithingsReportReader();
		qualityList = reader.getWithingsData(); 
		for (SleepQualityRecord rec: qualityList) {
			System.out.println(rec);
		}
	}
	
	private static int findFirstSleepRecord(DateOnly itemTime) {
		for (int i =0; i < qualityList.size(); i++) {
			SleepQualityRecord sleepRecord = qualityList.get(i);
			if (sleepRecord != null) {
				DateOnly recordTime = sleepRecord.getDate();
				if (recordTime.equals(itemTime)) {
					return i;
				}
			}
		}		
		return -1;
	}
	
	

	// Data received from stream will processed here
	private static void createFirebaseCallbacks() {
		fireBaseCallbacks = new CurrentDataCallback() {
			// callback from stream with current sensor data
			@Override
			public void updateStreamData(int key, int data) {
				// TODO Auto-generated method stub
				switch (key) {
				case SensorStation.NUM_CO2_KEY:
					currentData.setCO2(data);
					break;
				case SensorStation.NUM_TVOC_KEY:
					currentData.setTVOC(data);
					break;
				default:
					System.out.println("Stream sent unknown int");
					break;
				}
				ui.updateUI(currentData);
			}
			// callback from stream with current sensor data
			@Override
			public void updateStreamData(int key, double data) {
				// TODO Auto-generated method stub
				switch (key) {
				case SensorStation.NUM_T1_KEY:
					currentData.setTemperature(data);
					break;
				case SensorStation.NUM_T2_KEY:
					currentData.setTemperature2(data);
					break;
				case SensorStation.NUM_PRESSURE_KEY:
					currentData.setPressure(data);
					break;
				case SensorStation.NUM_HUMIDITY_KEY:
					currentData.setHumidity(data);
					break;
				default:
					System.out.println("Stream sent unknown double. Kiss your Boo!");
					break;
				}
				ui.updateUI(currentData);
			}

			// callback from stream with current sensor data
			@Override
			public void updateStreamData(int key, long data) {
				if (key == SensorStation.NUM_LAST_UPDATE_KEY) {
					currentData.setLastUpdate(data);
				} else {
					System.out.println("Stream sent unknown long");
				}
				ui.updateUI(currentData);
			}

			// callback from stream with current sensor data
			@Override
			public void updateStreamData(int key, boolean data) {
				switch (key) {
				case SensorStation.NUM_DHT_VALID_KEY:
					currentData.setDHT_valid(data);
					break;
				case SensorStation.NUM_SGP_VALID_KEY:
					currentData.setSGP_valid(data);
					break;
				default:
					System.out.println("Stream sent unknown bool");
					break;
				}
			}

			// callback from firebase with log data
			@Override
			public void getLogData(ArrayList<LogItem> fbLogs) {
				logs = fbLogs;
				ui.logLine("Received " + logs.size() + " items from Firebase");
				System.out.println("Received this many logItems:" + logs.size());
				//TODO: get averages only if necessary
				db.saveItems(DataHelper.getAveragePerDay(logs, logCallback));
			}
		};
	}
	
	
	private static void createLogCallback() {
		logCallback = new UiLogCallback() {
			@Override
			public void log(String text) {
				ui.logLine(text);
			}
		};
	}

	private static void turnOffLoggers() {
		Set<String> loggers = new HashSet<>(Arrays.asList(
				"org.apache.http",
				"groovyx.net.http", 
				"io.netty",
				"reactor.netty", 
				"org.springframework", 
				"com.launchdarkly.eventsource.EventSource",
				"org.hibernate"
				));
		for (String log : loggers) {
			Logger logger = (Logger) LoggerFactory.getLogger(log);
			logger.setLevel(Level.ERROR);
			logger.setAdditive(false);
		}
	}
	
	private static void createLogItemGraphs(ArrayList<LogItem> logs) {
		Graph graph = new Graph("Recent data", logCallback);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm");
		graph.drawGraph((List <Object>)(List<?>)logs, dateFormat, 2, true, false);
		graph.pack( );                
		graph.setVisible(true); 
	}
	
	private static void createDailyItemGraphs(ArrayList<DailyAveragesItem> logs) {
		Graph graph = new Graph("Daily averages data", logCallback);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM");
		graph.drawGraph((List <Object>)(List<?>)logs, dateFormat, 14, false, true);
		graph.pack( );                
		graph.setVisible(true); 
	}
}
