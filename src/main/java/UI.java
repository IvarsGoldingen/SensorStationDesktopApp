import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class UI extends JFrame {

	//UI elements
	//private JFrame frame;
	private  JTextField co2TV;
	private  JTextField tvocTV;
	private  JTextField T1tv;
	private JTextField T2tv;
	private JTextField rhTV;
	private JTextField pressureTv;
	private JTextField lastUpdateTV;
	private JTextArea logArea;
	
	//Format of readings
	private static DecimalFormat decForm = new DecimalFormat("#.##");
	//Callbacks to main
	private UICallbacks callbackToMain;
	
	public UI(UICallbacks callback) {
		callbackToMain = callback;
		initialize();
	}
	
	//Write data to log area
	public void logLine(String text) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
		Date date = new Date();
		String time = dateFormat.format(date);
		logArea.append(time + "\t" + text + '\n');
	}
	
	//Update ui with data from database
	public void updateUI(SensorStation data) {
		co2TV.setText(String.valueOf(data.getCO2()) + " PPM");
		tvocTV.setText(String.valueOf(data.getTVOC()) + " PPB");
		T1tv.setText(String.valueOf(decForm.format(data.getTemperature())) + " °C");
		T2tv.setText(String.valueOf(decForm.format(data.getTemperature2())) + " °C");
		rhTV.setText(String.valueOf(decForm.format(data.getHumidity())) + " RH");
		pressureTv.setText(String.valueOf(decForm.format(data.getPressure())) + " hPa");
		lastUpdateTV.setText(createTimeFromEpoch(data.getLastUpdate()));
	}
	
	//Create String time from Epoch time in millis
	private String createTimeFromEpoch(long time) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		String timeString = dateFormat.format(new Date(time));
		return timeString;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.setBounds(100, 100, 1035, 721);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 447, 544);
		this.getContentPane().add(panel);
		panel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JLabel lblNewLabel = new JLabel("CO2");
		lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel.add(lblNewLabel);
		
		co2TV = new JTextField();
		co2TV.setEditable(false);
		co2TV.setHorizontalAlignment(SwingConstants.CENTER);
		co2TV.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel.add(co2TV);
		co2TV.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("TVOC");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel.add(lblNewLabel_2);
		
		tvocTV = new JTextField();
		tvocTV.setEditable(false);
		tvocTV.setHorizontalAlignment(SwingConstants.CENTER);
		tvocTV.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel.add(tvocTV);
		tvocTV.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("TEMPERATURE 1");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel.add(lblNewLabel_1);
		
		T1tv = new JTextField();
		T1tv.setEditable(false);
		T1tv.setHorizontalAlignment(SwingConstants.CENTER);
		T1tv.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel.add(T1tv);
		T1tv.setColumns(10);
		
		JLabel lblNewLabel_3 = new JLabel("TEMPERATURE 2");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel.add(lblNewLabel_3);
		
		T2tv = new JTextField();
		T2tv.setEditable(false);
		T2tv.setHorizontalAlignment(SwingConstants.CENTER);
		T2tv.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel.add(T2tv);
		T2tv.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("RH");
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_4.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel.add(lblNewLabel_4);
		
		rhTV = new JTextField();
		rhTV.setEditable(false);
		rhTV.setHorizontalAlignment(SwingConstants.CENTER);
		rhTV.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel.add(rhTV);
		rhTV.setColumns(10);
		
		JLabel lblNewLabel_5 = new JLabel("PRESSURE");
		lblNewLabel_5.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_5.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel.add(lblNewLabel_5);
		
		pressureTv = new JTextField();
		pressureTv.setEditable(false);
		pressureTv.setHorizontalAlignment(SwingConstants.CENTER);
		pressureTv.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel.add(pressureTv);
		pressureTv.setColumns(10);
		
		JLabel lblNewLabel_6 = new JLabel("LAST UPDATE");
		lblNewLabel_6.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_6.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel.add(lblNewLabel_6);
		
		lastUpdateTV = new JTextField();
		lastUpdateTV.setEditable(false);
		lastUpdateTV.setHorizontalAlignment(SwingConstants.CENTER);
		lastUpdateTV.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel.add(lastUpdateTV);
		lastUpdateTV.setColumns(10);
		
		JButton btnNewButton = new JButton("DRAW RECENT GRAPH");
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				callbackToMain.openRecentGraphsButtonPressed();
			}
		});
		btnNewButton.setBounds(71, 554, 295, 59);
		getContentPane().add(btnNewButton);
		
		JButton btnDrawAveragesGraph = new JButton("DRAW AVERAGES GRAPH");
		btnDrawAveragesGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				callbackToMain.openAveragesGraphsButtonPressed();
			}
		});
		btnDrawAveragesGraph.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnDrawAveragesGraph.setBounds(71, 623, 295, 59);
		getContentPane().add(btnDrawAveragesGraph);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(457, 0, 564, 682);
		getContentPane().add(scrollPane);
		
		logArea = new JTextArea();
		scrollPane.setViewportView(logArea);
	}
	
	public void setJFrameVisible(boolean visible) {
		this.setVisible(true);
	}
}
