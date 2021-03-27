import java.awt.EventQueue;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
 * 4. create graphs from logs
 * 5. sava logs to local database
 * 6. Read data from withings reports
 * */
public class Main {

	private static CurrentDataCallback fireBaseCallbacks;
	// Current sensor data
	private static SensorStation currentData;
	// UI class
	private static UI ui;
	// Takes care of getting data from Firebase
	private static FirebaseHelper firebase;

	public static void main(String[] args) {
		turnOffLoggers();
		System.out.println("Sensor station start");
		createUI();
		// This object will be populated with data from the stream
		currentData = new SensorStation();
		connectToFirebase();
		firebase.startStream();
		firebase.getLogs();
		while (true)
			;
	}

	private static void createUI() {
		ui = new UI();
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
			public void getLogData(ArrayList<LogItem> logs) {
				System.out.println("Received this many logItems:" + logs.size());
				/*
				 * for (LogItem item: logs) { //Loop through log items }
				 */
			}
		};
	}

	private static void turnOffLoggers() {
		Set<String> loggers = new HashSet<>(Arrays.asList("org.apache.http", "groovyx.net.http", "io.netty",
				"reactor.netty", "org.springframework", "com.launchdarkly.eventsource.EventSource"));
		for (String log : loggers) {
			Logger logger = (Logger) LoggerFactory.getLogger(log);
			logger.setLevel(Level.INFO);
			logger.setAdditive(false);
		}
	}
}
