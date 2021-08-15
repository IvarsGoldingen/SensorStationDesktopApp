import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.time.DateRange;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Graph extends JFrame implements ChangeListener {
	//Legend titles
	private final static String T1_SERIES_NAME = "T1 [C°]";
	private final static String T2_SERIES_NAME = "T2 [C°]";
	private final static String CO2_SERIES_NAME = "CO2 [PPM]";
	private final static String TVOC_SERIES_NAME = "TVOC [PPB]";
	private final static String RH_SERIES_NAME = "RH [%]";
	private final static String PRESSURE_SERIES_NAME = "Pressure [hPa]";
	
	//Types of items that can be drawn
	private static final int ITEM_TYPE_UNKNOWN = -1;
	private static final int ITEM_TYPE_LOG = 1;
	private static final int ITEM_TYPE_DAILY_AVG = 2;
	
	// one day (milliseconds, seconds, minutes, hours, days)
	private static final int DAY_MS = 1000 * 60 * 60 * 24 * 1;
	private static final int HOUR_MS = 1000 * 60 * 60;

	// indexes of datasets
	private static final int HUNDREDS_PLOT_INDEX = 0;
	private static final int THOUSANDS_PLOT_INDEX = 1;

	// For slider
	private static int SLIDER_INITIAL_VALUE = 100;
	private JSlider slider;
	private DateAxis dateAxis;
	private long latestDateReceivedMs = 0;
	private long oldestDateReceivedMs = 9223372036854775807L;

	private ChartPanel chartPanel;
	private JFreeChart chart;
	private XYPlot plot;

	// Series for garph. Created here so can be manipulated later
	private XYSeries co2Series;
	private XYSeries tvocSeries;
	private XYSeries pressureSeries;
	private XYSeries t1Series;
	private XYSeries t2Series;
	private XYSeries humiditySeries;

	// Datasets for garph. Created here so can be manipulated later
	private XYSeriesCollection thousandsDataset;
	private XYSeriesCollection hundredsDataset;
	private XYSplineRenderer hundredsRenderer;
	private XYSplineRenderer thousandsRenderer;

	// for toggle buttons to show or hide series on Graph
	private boolean thousandsSeriesVisible = true;
	private boolean hudredsSeriesVisible = true;
	
	//How many days will be shown when pressing zoom to end
	private int zoomToEndDays = 2;
	//Console log
	UiLogCallback logCB;

	public Graph(String title, UiLogCallback logCB) {
		super(title);
		this.logCB = logCB;
	}

	//If smooth lines will be rounded
	public void drawGraph(List <Object> items, 
			SimpleDateFormat dateFormat, 
			int zoomToEndDays, 
			boolean drawSmooth,
			boolean showPoints){
		this.zoomToEndDays = zoomToEndDays;
		createSeries((List <Object>)(List<?>)items);
		createDatasets();
		// create plots
		plot = new XYPlot();
		// Set datasets to plot
		plot.setDataset(HUNDREDS_PLOT_INDEX, hundredsDataset);
		plot.setDataset(THOUSANDS_PLOT_INDEX, thousandsDataset);
		// Set white background
		plot.setBackgroundPaint(Color.WHITE);
		formatAxis(drawSmooth, showPoints);
		setYaxisRanges();
		formatTimeAxis(dateFormat);
		// Map the data to the appropriate axis
		plot.mapDatasetToRangeAxis(HUNDREDS_PLOT_INDEX, HUNDREDS_PLOT_INDEX);
		plot.mapDatasetToRangeAxis(THOUSANDS_PLOT_INDEX, THOUSANDS_PLOT_INDEX);
		// create a slider
		this.slider = new JSlider(0, 100, SLIDER_INITIAL_VALUE);
		this.slider.addChangeListener(this);
		// generate the chart
		chart = new JFreeChart("MyPlot", getFont(), plot, true);
		chart.setBackgroundPaint(Color.WHITE);
		chartPanel = new ChartPanel(chart);
		chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		chartPanel.setBackground(Color.WHITE);
		chartPanel.add(this.slider);
		add(chartPanel);
		createLegendClickListener();
		// Create visual controls
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.add(createShowAllThousandsSeriesButton());
		panel.add(createShowAllHundredsSeriesButton());
		panel.add(createZoom());// Create zoom button
		panel.add(createZoomToEnd());
		add(panel, BorderLayout.SOUTH);
		pack();
		setTitle("Sensor station");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	

	private void createLegendClickListener() {
		chartPanel.addChartMouseListener(new ChartMouseListener() {
			@Override
			public void chartMouseClicked(ChartMouseEvent e) {
				ChartEntity ce = e.getEntity();
				if (ce instanceof XYItemEntity) {
					// Item pressed
				} else if (ce instanceof LegendItemEntity) {
					LegendItemEntity item = (LegendItemEntity) ce;
					Comparable pressedSeriesName = item.getSeriesKey();
					if (pressedSeriesName.equals(T1_SERIES_NAME) || pressedSeriesName.equals(T2_SERIES_NAME)
							|| pressedSeriesName.equals(RH_SERIES_NAME)) {
						// series is on the hundreds axis:
						hundredsRenderer.setSeriesVisible(hundredsDataset.getSeriesIndex(pressedSeriesName), false);
					} else if (pressedSeriesName.equals(CO2_SERIES_NAME) || pressedSeriesName.equals(TVOC_SERIES_NAME)
							|| pressedSeriesName.equals(PRESSURE_SERIES_NAME)) {
						// series is on the thousands axis:
						thousandsRenderer.setSeriesVisible(thousandsDataset.getSeriesIndex(pressedSeriesName), false);
					} else {
						System.out.println("ERROR UNKNOWN SERIES PRESSED");
					}
				} else {
					// empty space pressed
				}
			}

			@Override
			public void chartMouseMoved(ChartMouseEvent e) {
			}
		});
	}
	
	private void createSeries(List<Object> items) {
		initSensorDataSeries();
		Object firstItem = items.get(0);
		int itemType = ITEM_TYPE_UNKNOWN;
		if (firstItem instanceof DailyAveragesItem) {
			logCB.log("GRAPH: Drawing averages items");
			itemType = ITEM_TYPE_DAILY_AVG;
		} else if(firstItem instanceof LogItem) {
			logCB.log("GRAPH: Drawing regular items");
			itemType = ITEM_TYPE_LOG;
		} else {
			logCB.log("GRAPH: Error: unknown item");
			System.out.println("GRAPH: Error: unknown item");
		}

		for (Object item : items) {
			// Get the last item time so it is known from when to draw the graph
			long timeOfItem = getItemTime(item, itemType);
			addSensorSeriesData(item,timeOfItem);
		}
	}
	
	private void addSensorSeriesData(Object item, long timeOfItem) {
		//System.out.println("Time: " + timeOfItem);
		SensorData sensorData = (SensorData)item;
		co2Series.add(timeOfItem, sensorData.getCO2());
		t1Series.add(timeOfItem, sensorData.getTemperature());
		t2Series.add(timeOfItem, sensorData.getTemperature2());
		tvocSeries.add(timeOfItem, sensorData.getTVOC());
		pressureSeries.add(timeOfItem, sensorData.getPressure());
		humiditySeries.add(timeOfItem, sensorData.getHumidity());
	}
	
	//Get time from item depening on what typpe of item it is
	private long getItemTime(Object item, int objectType) {
		long timeOfItem = 0; 
		if (objectType == ITEM_TYPE_DAILY_AVG) {
			DailyAveragesItem castedItem = (DailyAveragesItem)item;
			DateOnly dateOfItem = castedItem.getDate();
			timeOfItem = dateOfItem.getEpochMs();
			if (timeOfItem > latestDateReceivedMs) {
				latestDateReceivedMs = timeOfItem;
			}
			//Oldest item for slider
			if (timeOfItem < oldestDateReceivedMs) {
				oldestDateReceivedMs = timeOfItem;
			}
		} else if (objectType == ITEM_TYPE_LOG) {
			LogItem castedItem = (LogItem)item;
			timeOfItem = castedItem.getTime();
			if (timeOfItem > latestDateReceivedMs) {
				latestDateReceivedMs = timeOfItem;
			}
			//Oldest item for slider
			if (timeOfItem < oldestDateReceivedMs) {
				oldestDateReceivedMs = timeOfItem;
			}
		} else {
			System.out.println("Graph - ERROR: unknown type");
		}
		return timeOfItem;
	}

	
	private void initSensorDataSeries() {
		// Create a series of neccessary log data
		co2Series = new XYSeries(CO2_SERIES_NAME);
		tvocSeries = new XYSeries(TVOC_SERIES_NAME);
		pressureSeries = new XYSeries(PRESSURE_SERIES_NAME);
		t1Series = new XYSeries(T1_SERIES_NAME);
		t2Series = new XYSeries(T2_SERIES_NAME);
		humiditySeries = new XYSeries(RH_SERIES_NAME);
	}

	private void createDatasets() {
		// create dataSet
		thousandsDataset = new XYSeriesCollection();
		hundredsDataset = new XYSeriesCollection();
		hundredsDataset.addSeries(t1Series);
		hundredsDataset.addSeries(t2Series);
		hundredsDataset.addSeries(humiditySeries);
		thousandsDataset.addSeries(co2Series);
		thousandsDataset.addSeries(tvocSeries);
		thousandsDataset.addSeries(pressureSeries);
	}

	//If smooth lines will be rounded
	private void formatAxis(boolean drawSmooth, boolean showPoints) {
		int precission = 0;
		if (drawSmooth) {
			precission = 10;
		} else {
			precission = 1;
		}
		// Format y axis with thousands range
		thousandsRenderer = new XYSplineRenderer(precission);
		plot.setRenderer(THOUSANDS_PLOT_INDEX, thousandsRenderer);
		thousandsRenderer.setSeriesPaint(0, Color.BLUE);
		thousandsRenderer.setSeriesPaint(1, Color.RED);
		thousandsRenderer.setSeriesPaint(2, Color.GREEN);
		thousandsRenderer.setSeriesStroke(THOUSANDS_PLOT_INDEX, new BasicStroke(1.0f));
		plot.setRangeAxis(THOUSANDS_PLOT_INDEX, new NumberAxis("CO2, TVOC, pressure"));
		thousandsRenderer.setDefaultShapesVisible(showPoints);
		// Format y axis with hundreds range
		hundredsRenderer = new XYSplineRenderer(precission);
		plot.setRenderer(HUNDREDS_PLOT_INDEX, hundredsRenderer);
		hundredsRenderer.setSeriesPaint(0, Color.MAGENTA);
		hundredsRenderer.setSeriesPaint(1, Color.PINK);
		hundredsRenderer.setSeriesPaint(2, Color.ORANGE);
		// hundredsRenderer.setSeriesFillPaint(HUNDREDS_PLOT_INDEX, Color.RED);
		hundredsRenderer.setSeriesStroke(HUNDREDS_PLOT_INDEX, new BasicStroke(1.0f));
		plot.setRangeAxis(HUNDREDS_PLOT_INDEX, new NumberAxis("Temperature, humidity"));
		hundredsRenderer.setDefaultShapesVisible(showPoints);
	}

	private void setYaxisRanges() {
		//Set ranges on both Y axis
		NumberAxis hundredsAxis = (NumberAxis) plot.getRangeAxis(HUNDREDS_PLOT_INDEX);
		hundredsAxis.setRange(0, 100);
		NumberAxis thousandsAxis = (NumberAxis) plot.getRangeAxis(THOUSANDS_PLOT_INDEX);
		thousandsAxis.setRange(0, 2000);
	}

	private void formatTimeAxis(SimpleDateFormat dateFormat) {
		DateAxis dateAxis = new DateAxis("Time");
		dateAxis.setDateFormatOverride(dateFormat);
		// Set time axis on X
		plot.setDomainAxis(dateAxis);
		zoomGraphToEnd();
	}

	private void zoomGraphToEnd() {
		// Set ranges on X axis
		dateAxis = (DateAxis) plot.getDomainAxis();
		long startOfDateAxis = latestDateReceivedMs - ((long)zoomToEndDays * (long)DAY_MS);
		dateAxis.setRange(startOfDateAxis, latestDateReceivedMs);
	}
	
	private JButton createZoomToEnd() {
		final JButton auto = new JButton(new AbstractAction("ZOOM TO END") {
			@Override
			public void actionPerformed(ActionEvent e) {
				zoomGraphToEnd();
			}
		});
		return auto;
	}

	private JButton createZoom() {
		final JButton auto = new JButton(new AbstractAction("SHOW ALL") {
			@Override
			public void actionPerformed(ActionEvent e) {
				chartPanel.restoreAutoBounds();
			}
		});
		return auto;
	}

	private JButton createShowAllHundredsSeriesButton() {
		final JButton auto = new JButton(new AbstractAction("TOGGLE ALL HUNDREDS SERIES") {
			@Override
			public void actionPerformed(ActionEvent e) {
				hudredsSeriesVisible = !hudredsSeriesVisible;
				int numberOfSeries = hundredsDataset.getSeriesCount();
				for (int i = 0; i < numberOfSeries; i++) {
					hundredsRenderer.setSeriesVisible(i, hudredsSeriesVisible);
				}
			}
		});
		return auto;
	}

	private JButton createShowAllThousandsSeriesButton() {
		final JButton auto = new JButton(new AbstractAction("TOGGLE ALL THOUSANDS SERIES") {
			@Override
			public void actionPerformed(ActionEvent e) {
				thousandsSeriesVisible = !thousandsSeriesVisible;
				int numberOfSeries = thousandsDataset.getSeriesCount();
				for (int i = 0; i < numberOfSeries; i++) {
					thousandsRenderer.setSeriesVisible(i, thousandsSeriesVisible);
				}
			}
		});
		return auto;
	}

	@Override
	// Slidebar moved
	public void stateChanged(ChangeEvent e) {
		long newStart = 0;
		long newFinish = 0;
		long displayedMin = dateAxis.getMinimumDate().getTime();
		long displayedMax = dateAxis.getMaximumDate().getTime();
		long displayedRange = displayedMax - displayedMin;
		int sliderValue = this.slider.getValue();
		if (sliderValue == 0) {
			newStart = oldestDateReceivedMs;
			newFinish = newStart + displayedRange;
		} else if (sliderValue == 100) {
			newStart = latestDateReceivedMs - displayedRange;
			newFinish = latestDateReceivedMs;
		} else {
			long dataRange = latestDateReceivedMs - oldestDateReceivedMs;
			float howManyDisplaysFitInDataRange = (float) ((float) dataRange / (float) displayedRange);
			// the slider gives 0 to 100
			// this needs to be scaled from 0 to howManyDisplaysFitInDataRange
			float positionInRange = (float) ((float) sliderValue / 100.0) * howManyDisplaysFitInDataRange;
			// the slider represents the left side of the graph
			// so one display needs to be subtracted
			long adjustedDataRange = dataRange - displayedRange;
			float displayInRangeAdjusted = (float) ((float) adjustedDataRange / (float) displayedRange);
			float positionInRangeAdjusted = (float) ((float) sliderValue / 100.0) * displayInRangeAdjusted;
			float posInRange_0to1 = positionInRangeAdjusted / displayInRangeAdjusted;
			newStart = oldestDateReceivedMs + ((long) (adjustedDataRange * posInRange_0to1));
			newFinish = newStart + displayedRange;
		}
		DateRange range = new DateRange(newStart, newFinish);
		dateAxis.setRange(range);
	}

	private void createRecentsSeries(List<LogItem> logItems) {
		initSensorDataSeries();
		for (LogItem item : logItems) {
			// Get the last item time so it is known from when to draw the graph
			long timeOfItem = item.getTime();
			if (timeOfItem > latestDateReceivedMs) {
				latestDateReceivedMs = timeOfItem;
			}
			//Oldest item for slider
			if (timeOfItem < oldestDateReceivedMs) {
				oldestDateReceivedMs = timeOfItem;
			}
			co2Series.add(timeOfItem, item.getCO2());
			t1Series.add(timeOfItem, item.getTemperature());
			t2Series.add(timeOfItem, item.getTemperature2());
			tvocSeries.add(timeOfItem, item.getTVOC());
			pressureSeries.add(timeOfItem, item.getPressure());
			humiditySeries.add(timeOfItem, item.getHumidity());
		}
	}
	
	/*
	public void drawDailyItems(List<DailyAveragesItem> items)
	{
		zoomToEndDays = 14;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM");
		createSeries((List <Object>)(List<?>)items);
		createDatasets();
		// create plots
		plot = new XYPlot();
		// Set datasets to plot
		plot.setDataset(HUNDREDS_PLOT_INDEX, hundredsDataset);
		plot.setDataset(THOUSANDS_PLOT_INDEX, thousandsDataset);
		// Set white background
		plot.setBackgroundPaint(Color.WHITE);
		formatAxis();
		setYaxisRanges();
		formatTimeAxis(dateFormat);
		// Map the data to the appropriate axis
		plot.mapDatasetToRangeAxis(HUNDREDS_PLOT_INDEX, HUNDREDS_PLOT_INDEX);
		plot.mapDatasetToRangeAxis(THOUSANDS_PLOT_INDEX, THOUSANDS_PLOT_INDEX);
		// create a slider
		this.slider = new JSlider(0, 100, SLIDER_INITIAL_VALUE);
		this.slider.addChangeListener(this);
		// generate the chart
		chart = new JFreeChart("MyPlot", getFont(), plot, true);
		chart.setBackgroundPaint(Color.WHITE);
		chartPanel = new ChartPanel(chart);
		chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		chartPanel.setBackground(Color.WHITE);
		chartPanel.add(this.slider);
		add(chartPanel);
		createLegendClickListener();
		// Create visual controls
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.add(createShowAllThousandsSeriesButton());
		panel.add(createShowAllHundredsSeriesButton());
		panel.add(createZoom());// Create zoom button
		panel.add(createZoomToEnd());
		add(panel, BorderLayout.SOUTH);
		pack();
		setTitle("Sensor station");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	public void drawLogItems(List<LogItem> logItems) {
		zoomToEndDays = 2;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm");
		createSeries((List <Object>)(List<?>)logItems);
		createDatasets();
		// create plots
		plot = new XYPlot();
		// Set datasets tu plot
		plot.setDataset(HUNDREDS_PLOT_INDEX, hundredsDataset);
		plot.setDataset(THOUSANDS_PLOT_INDEX, thousandsDataset);
		// Set white background
		plot.setBackgroundPaint(Color.WHITE);
		formatAxis();
		setYaxisRanges();
		formatTimeAxis(dateFormat);
		// Set ranges on X axis
		// Map the data to the appropriate axis
		plot.mapDatasetToRangeAxis(HUNDREDS_PLOT_INDEX, HUNDREDS_PLOT_INDEX);
		plot.mapDatasetToRangeAxis(THOUSANDS_PLOT_INDEX, THOUSANDS_PLOT_INDEX);
		// create a slider
		this.slider = new JSlider(0, 100, SLIDER_INITIAL_VALUE);
		this.slider.addChangeListener(this);
		// generate the chart
		chart = new JFreeChart("MyPlot", getFont(), plot, true);
		chart.setBackgroundPaint(Color.WHITE);
		chartPanel = new ChartPanel(chart);
		chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		chartPanel.setBackground(Color.WHITE);
		chartPanel.add(this.slider);
		add(chartPanel);
		createLegendClickListener();
		// Create visual controls
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.add(createShowAllThousandsSeriesButton());
		panel.add(createShowAllHundredsSeriesButton());
		panel.add(createZoom());// Create zoom button
		panel.add(createZoomToEnd());
		add(panel, BorderLayout.SOUTH);
		pack();
		setTitle("Sensor station");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	*/
}
