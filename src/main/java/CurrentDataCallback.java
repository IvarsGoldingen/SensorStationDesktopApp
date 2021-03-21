//Interface for returning incomming data from the Stream to the main class
public interface CurrentDataCallback {
	void updateData(int key, int data);
	void updateData(int key, double data);
	void updateData(int key, long data);
	void updateData(int key, boolean data);
}
