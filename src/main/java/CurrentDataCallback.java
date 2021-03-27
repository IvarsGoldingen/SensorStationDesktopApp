import java.util.ArrayList;

//Interface for returning incomming data from the Stream to the main class
public interface CurrentDataCallback {
	void updateStreamData(int key, int data);
	void updateStreamData(int key, double data);
	void updateStreamData(int key, long data);
	void updateStreamData(int key, boolean data);
	void getLogData(ArrayList <LogItem> logs);
}
