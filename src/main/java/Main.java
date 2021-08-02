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
 * 1. sava logs to local database
 * 		read data from local database
 * 		Save data from main not DataHelper
 * 		Draw logs from database
 * 2.1 Finish implementing console
 * 2. Read data from withings reports
 * 3. Have some kind of status log window
 * 4. Do not exit app when closing graph
 * 		put your ChartPanel in a JFrame
 * 		https://stackoverflow.com/questions/5522575/how-can-i-update-a-jfreecharts-appearance-after-its-been-made-visible
 * */
public class Main {

	private static CurrentDataCallback fireBaseCallbacks;
	private static UICallbacks btnsCallback;
	// Current sensor data
	private static SensorStation currentData;
	// UI class
	private static UI ui;
	// Takes care of getting data from Firebase
	private static FirebaseHelper firebase;
	//UI for building graphs
	static Graph graph2;
	//List of log data
	private static ArrayList<LogItem> logs = null;
	//List of daily data
	private static ArrayList<DailyAveragesItem> dailyLogs = null;

	public static void main(String[] args) {
		turnOffLoggers();
		System.out.println("Sensor station start");
		createUI();
		// This object will be populated with data from the stream
		currentData = new SensorStation();
		connectToFirebase();
		firebase.startStream();
		firebase.getLogs();
		//
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
					JOptionPane.showMessageDialog(null, "Logs are not yet downloaded");
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
		};
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
					System.out.println("Stream sent unknown double");
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
				dailyLogs = DataHelper.getAveragePerDay(logs);
			}
		};
	}

	private static void turnOffLoggers() {
		Set<String> loggers = new HashSet<>(Arrays.asList("org.apache.http", "groovyx.net.http", "io.netty",
				"reactor.netty", "org.springframework", "com.launchdarkly.eventsource.EventSource"));
		for (String log : loggers) {
			Logger logger = (Logger) LoggerFactory.getLogger(log);
			logger.setLevel(Level.ERROR);
			logger.setAdditive(false);
		}
	}
	
	private static void createLogItemGraphs(ArrayList<LogItem> logs) {
		Graph graph = new Graph("Recent data");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm");
		graph.drawGraph((List <Object>)(List<?>)logs, dateFormat, 2);
		//graph.drawLogItems(logs);
		graph.pack( );                
		graph.setVisible(true); 
	}
	
	private static void createDailyItemGraphs(ArrayList<DailyAveragesItem> logs) {
		Graph graph = new Graph("Daily averages data");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM");
		graph.drawGraph((List <Object>)(List<?>)logs, dateFormat, 14);
		//graph.drawDailyItems(logs);
		graph.pack( );                
		graph.setVisible(true); 
	}
}
