import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;

import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;

import reactor.core.publisher.Mono;

public class FirebaseHelper {
	// Firebase constants
	private static final String FB_BASE_URL = "https://esp32test-ef408.firebaseio.com";
	private static final String FB_RT_DATA = "/SensorStation.json";
	private static final String FB_LOG_DATA = "/Log.json";

	CurrentDataCallback streamCallback;

	public FirebaseHelper(CurrentDataCallback callback) {
		streamCallback = callback;
	}

	/*
	 * Start live current sensor data stream from Firebase
	 */
	public void startStream() {
		// Pass the callback to the message handler so data can be received by main
		EventHandler eventHandler = new StreamMessageHandler(streamCallback);
		String url = FB_BASE_URL + FB_RT_DATA;
		EventSource.Builder builder = new EventSource.Builder(eventHandler, URI.create(url))
				.reconnectTime(Duration.ofMillis(3000));
		EventSource eventSource = builder.build();
		eventSource.start();
	}

	/*
	 * Get logs from Firebase
	 */
	public void getLogs() {
		WebClient webClient = WebClient.builder()
				.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)).baseUrl(FB_BASE_URL)
				.build();
		Mono<String> testString = webClient.get().uri(FB_LOG_DATA).retrieve().bodyToMono(String.class);
		testString.subscribe(result -> getLogsFromJsonString(result));
	}

	/*
	 * Convert JSON string to LogItem objects
	 */
	private void getLogsFromJsonString(String data) {
		JsonParser jp = new JsonParser();
		JsonElement jsonElement = jp.parse(data);
		// Create the parrent object
		JsonObject parentJsonObject = jsonElement.getAsJsonObject();
		Gson gson = new GsonBuilder().create();
		// Get all keys inside parentJson
		ArrayList<String> objectKeys = new ArrayList<String>(parentJsonObject.keySet());
		ArrayList<LogItem> logs = new ArrayList<LogItem>();
		// Loop through all the inner JSON objects and retreive LogItems
		for (String key : objectKeys) {
			LogItem logItem = gson.fromJson(parentJsonObject.get(key), LogItem.class);
			logs.add(logItem);
		}
		// Return data to main
		streamCallback.getLogData(logs);
	}
}
