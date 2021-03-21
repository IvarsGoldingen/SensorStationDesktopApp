import java.awt.EventQueue;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;

/*
 * App that receives sensor data stored in google firebase
 * */
public class Main {
	
	private static CurrentDataCallback streamCallback;
	//Current sensor data
	private static SensorStation currentData;
	//UI class
	private static UI ui;
	
	public static void main(String[] args) {
		System.out.println("Sensor station start");
		createUI();
		//This object will be populated with data from the stream
		currentData = new SensorStation();
		startStream();
		while(true);
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
	
	//Start streaming live data from google firebase
	private static void startStream() {
		createStreamCallbacks();
		EventHandler eventHandler = new StreamMessageHandler(streamCallback);
		String url = String.format("https://esp32test-ef408.firebaseio.com/SensorStation.json");
	    EventSource.Builder builder = new EventSource.Builder(eventHandler, URI.create(url))
	        .reconnectTime(Duration.ofMillis(3000));
	    EventSource eventSource = builder.build();
	    eventSource.start();
	}
	
	//Data received from stream will processed here
		private static void createStreamCallbacks() {
			streamCallback = new CurrentDataCallback() {

				@Override
				public void updateData(int key, int data) {
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

				@Override
				public void updateData(int key, double data) {
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

				@Override
				public void updateData(int key, long data) {
					if (key == SensorStation.NUM_LAST_UPDATE_KEY) {
						currentData.setLastUpdate(data);
					} else {
						System.out.println("Stream sent unknown long");
					}
					ui.updateUI(currentData);
				}

				@Override
				public void updateData(int key, boolean data) {
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
			};
		}
}
