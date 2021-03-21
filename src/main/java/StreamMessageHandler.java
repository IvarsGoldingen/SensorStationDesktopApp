import java.util.ArrayList;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.MessageEvent;

public class StreamMessageHandler implements EventHandler {
	CurrentDataCallback mainCallback;
	
	public StreamMessageHandler(CurrentDataCallback callback) {
		super();
		mainCallback = callback;
	}
	
	@Override
	public void onMessage(String event, MessageEvent messageEvent) throws Exception {
		// TODO Auto-generated method stub
		//Create json object
		JsonParser jsonParser = new JsonParser();
	    JsonElement jsonElement = jsonParser.parse(messageEvent.getData().toString());
	    JsonObject jsonObject = jsonElement.getAsJsonObject();
	    
	    /*if the the current received event has 2 changed values the json object will hold
	     * within it  another json object named data
	     * otherwise jsonObject will hold the single value within it*/
	    if (jsonObject.get("data").isJsonObject()) {
	    	JsonObject jsonDataObject = jsonObject.getAsJsonObject("data");
	    	//Get keys for all the data that has changed
	        ArrayList<String> objectKeys =new ArrayList<String>(jsonDataObject.keySet());
	        for (int i = 0; i < objectKeys.size();i++) {
	        	//Go through all of the received keys and send the changed data to main
	        	String key = objectKeys.get(i);
	        	extractAndForward(key, jsonDataObject, false);
	        }
	    } else {
	    	//If only one value has changed in the firebase SensorStation project,
	    	//the JSON will look something like this: 
	    	//{"path":"/LastUpdate","data":1616246597767}
	    	ArrayList<String> objectKeys =new ArrayList<String>(jsonObject.keySet());
	    	String key = jsonObject.get("path").getAsString();
	    	//Remove the forward slash from the SensorStation object name
	    	String modifiedKey = key.replaceFirst("/", "");
	    	extractAndForward(modifiedKey, jsonObject, true);
	    }
	}
	
	//boolean extractData - true means that the data is to be extracted from the "data" key
	  private void extractAndForward(String key, JsonObject jsonObject, boolean extractData) {
		  	if (key.equals(SensorStation.CO2_KEY)) {
		  		if (extractData) {
		  			retunrResultsToMain(SensorStation.NUM_CO2_KEY, jsonObject.get("data").getAsInt());
		  		} else {
		  			retunrResultsToMain(SensorStation.NUM_CO2_KEY, jsonObject.get(key).getAsInt());
		  		}
			} else if (key.equals(SensorStation.TVOC_KEY)){
				if (extractData) {
					retunrResultsToMain(SensorStation.NUM_TVOC_KEY, jsonObject.get("data").getAsInt());
		  		} else {
		  			retunrResultsToMain(SensorStation.NUM_TVOC_KEY, jsonObject.get(key).getAsInt());
		  		}
			}else if (key.equals(SensorStation.T1_KEY)){
				if (extractData) {
					retunrResultsToMain(SensorStation.NUM_T1_KEY, jsonObject.get("data").getAsDouble());
		  		} else {
		  			retunrResultsToMain(SensorStation.NUM_T1_KEY, jsonObject.get(key).getAsDouble());
		  		}
			}else if (key.equals(SensorStation.T2_KEY)){
				if (extractData) {
					retunrResultsToMain(SensorStation.NUM_T2_KEY, jsonObject.get("data").getAsDouble());
		  		} else {
		  			retunrResultsToMain(SensorStation.NUM_T2_KEY, jsonObject.get(key).getAsDouble());
		  		}
			}else if (key.equals(SensorStation.PRESSURE_KEY)){
				if (extractData) {
					retunrResultsToMain(SensorStation.NUM_PRESSURE_KEY, jsonObject.get("data").getAsDouble());
		  		} else {
		  			retunrResultsToMain(SensorStation.NUM_PRESSURE_KEY, jsonObject.get(key).getAsDouble());
		  		}
			}else if (key.equals(SensorStation.HUMIDITY_KEY)){
				if (extractData) {
					retunrResultsToMain(SensorStation.NUM_HUMIDITY_KEY, jsonObject.get("data").getAsDouble());
		  		} else {
		  			retunrResultsToMain(SensorStation.NUM_HUMIDITY_KEY, jsonObject.get(key).getAsDouble());
		  		}
			}else if (key.equals(SensorStation.LAST_UPDATE_KEY)){
				if (extractData) {
					retunrResultsToMain(SensorStation.NUM_LAST_UPDATE_KEY, jsonObject.get("data").getAsLong());
		  		} else {
		  			retunrResultsToMain(SensorStation.NUM_LAST_UPDATE_KEY, jsonObject.get(key).getAsLong());
		  		}
			}else if (key.equals(SensorStation.DHT_VALID_KEY)){
				if (extractData) {
					retunrResultsToMain(SensorStation.NUM_DHT_VALID_KEY, jsonObject.get("data").getAsBoolean());
		  		} else {
		  			retunrResultsToMain(SensorStation.NUM_DHT_VALID_KEY, jsonObject.get(key).getAsBoolean());
		  		}
			}else if (key.equals(SensorStation.SGP_VALID_KEY)){
				if (extractData) {
					retunrResultsToMain(SensorStation.NUM_SGP_VALID_KEY, jsonObject.get("data").getAsBoolean());
		  		} else {
		  			retunrResultsToMain(SensorStation.NUM_SGP_VALID_KEY, jsonObject.get(key).getAsBoolean());
		  		}
			}
	  }
	
	  //******************METHODS FOR RETURNING DIFFERENT DATA TO MAIN
	  public void retunrResultsToMain(int key, int data) {
		  if (mainCallback != null) {
			  mainCallback.updateData(key, data);
		  }
	  }
	  
	  public void retunrResultsToMain(int key, double data) {
		  if (mainCallback != null) {
			  mainCallback.updateData(key, data);
		  }
	  }
	  
	  public void retunrResultsToMain(int key, long data) {
		  if (mainCallback != null) {
			  mainCallback.updateData(key, data);
		  }
	  }
	  
	  public void retunrResultsToMain(int key, boolean data) {
		  if (mainCallback != null) {
			  mainCallback.updateData(key, data);
		  }
	  }
	
	//***************UNUSED METHODS FROM EVENTHANDLER
	@Override
	public void onComment(String comment) throws Exception {
		System.out.println("onComment");
	}

	@Override
	public void onError(Throwable t) {
		System.out.println("onError: " + t);
	}
	
	@Override
	public void onOpen() throws Exception {
		System.out.println("onOpen");
	}

	@Override
	public void onClosed() throws Exception {
		System.out.println("onClosed");
	}

}
