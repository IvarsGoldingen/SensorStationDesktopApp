import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.time.DateRange;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.ui.ApplicationFrame;

public class Graph extends ApplicationFrame implements ChangeListener {

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
	private int lastValue = SLIDER_INITIAL_VALUE;
	private long latestDateReceivedMs = 0;
	private long oldestDateReceivedMs = 9223372036854775807L;

	public Graph(String title) {
		super(title);
	}

	public void drawLogItemTest(List<LogItem> logItems) {
		// Create a series of neccessary log data
		final XYSeries co2Series = new XYSeries("CO2 [PPM]");
		final XYSeries tvocSeries = new XYSeries("TVOC [PPB]");
		final XYSeries pressureSeries = new XYSeries("Pressure [hPa]");
		final XYSeries t1Series = new XYSeries("T1 [C°]");
		final XYSeries t2Series = new XYSeries("T2 [C°]");
		final XYSeries humiditySeries = new XYSeries("RH [%]");
		for (LogItem item : logItems) {
			// Get the last item time so it is known from when to draw the graph
			long timeOfItem = item.getTime();
			if (timeOfItem > latestDateReceivedMs) {
				latestDateReceivedMs = timeOfItem;
			}
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

		// create dataSet
		final XYSeriesCollection thousandsDataset = new XYSeriesCollection();
		final XYSeriesCollection hundredsDataset = new XYSeriesCollection();
		final XYSeriesCollection temperature2Dataset = new XYSeriesCollection();
		// dataset.addSeries(co2Series);
		hundredsDataset.addSeries(t1Series);
		hundredsDataset.addSeries(t2Series);
		hundredsDataset.addSeries(humiditySeries);
		thousandsDataset.addSeries(co2Series);
		thousandsDataset.addSeries(tvocSeries);
		thousandsDataset.addSeries(pressureSeries);

		// create plots
		XYPlot plot = new XYPlot();
		plot.setDataset(HUNDREDS_PLOT_INDEX, hundredsDataset);// private static final int HUNDREDS_PLOT_INDEX = 0;
		plot.setDataset(THOUSANDS_PLOT_INDEX, thousandsDataset);// private static final int THOUSANDS_PLOT_INDEX = 1;
		// plot.setDataset(2, hundredsDataset);//

		// Format y axis with thousands range
		XYSplineRenderer thousandsRenderer = new XYSplineRenderer();
		plot.setRenderer(THOUSANDS_PLOT_INDEX, thousandsRenderer);
		thousandsRenderer.setSeriesPaint(0, Color.BLUE);
		thousandsRenderer.setSeriesPaint(1, Color.RED);
		thousandsRenderer.setSeriesPaint(2, Color.GREEN);
		thousandsRenderer.setSeriesStroke(THOUSANDS_PLOT_INDEX, new BasicStroke(1.0f));
		plot.setRangeAxis(THOUSANDS_PLOT_INDEX, new NumberAxis("CO2, TVOC, pressure"));
		thousandsRenderer.setDefaultShapesVisible(false);

		// Format y axis with hundreds range
		XYSplineRenderer hundredsRenderer = new XYSplineRenderer();
		plot.setRenderer(HUNDREDS_PLOT_INDEX, hundredsRenderer);
		hundredsRenderer.setSeriesPaint(0, Color.MAGENTA);
		hundredsRenderer.setSeriesPaint(1, Color.PINK);
		hundredsRenderer.setSeriesPaint(2, Color.ORANGE);
		// hundredsRenderer.setSeriesFillPaint(HUNDREDS_PLOT_INDEX, Color.RED);
		hundredsRenderer.setSeriesStroke(HUNDREDS_PLOT_INDEX, new BasicStroke(1.0f));
		plot.setRangeAxis(HUNDREDS_PLOT_INDEX, new NumberAxis("Temperature, humidity"));
		hundredsRenderer.setDefaultShapesVisible(false);

		// Set ranges on both Y axis
		NumberAxis hundredsAxis = (NumberAxis) plot.getRangeAxis(HUNDREDS_PLOT_INDEX);
		hundredsAxis.setRange(0, 100);
		NumberAxis thousandsAxis = (NumberAxis) plot.getRangeAxis(THOUSANDS_PLOT_INDEX);
		thousandsAxis.setRange(0, 2000);

		// Set time axis on X
		plot.setDomainAxis(new DateAxis("Time"));
		// Set ranges on X axis
		dateAxis = (DateAxis) plot.getDomainAxis();
		long startOfDateAxis = latestDateReceivedMs - (2 * DAY_MS);
		dateAxis.setRange(startOfDateAxis, latestDateReceivedMs);

		// Map the data to the appropriate axis
		plot.mapDatasetToRangeAxis(HUNDREDS_PLOT_INDEX, HUNDREDS_PLOT_INDEX);
		plot.mapDatasetToRangeAxis(THOUSANDS_PLOT_INDEX, THOUSANDS_PLOT_INDEX);

		// Set white background
		plot.setBackgroundPaint(Color.WHITE);

		// create a slider
		this.slider = new JSlider(0, 100, SLIDER_INITIAL_VALUE);
		this.slider.addChangeListener(this);

		// generate the chart
		JFreeChart chart = new JFreeChart("MyPlot", getFont(), plot, true);
		chart.setBackgroundPaint(Color.WHITE);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		chartPanel.setBackground(Color.WHITE);
		chartPanel.add(this.slider);
		add(chartPanel);
		pack();
		setTitle("Sensor station");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/*
	 * 
	 * private long latestDateReceivedMs = 0; private long oldestDateReceivedMs =
	 * 9223372036854775807L;
	 */

	@Override
	public void stateChanged(ChangeEvent e) {
		System.out.println("****************SLIDER DEBUG****************");
		// TODO clean this up

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

			System.out.println("displayedRange in H " + (displayedRange / 1000 / 60 / 60));
			long dataRange = latestDateReceivedMs - oldestDateReceivedMs;
			System.out.println("dataRange in H " + (dataRange / 1000 / 60 / 60));
			float howManyDisplaysFitInDataRange = (float) ((float) dataRange / (float) displayedRange);
			System.out.println("howManyDisplaysFitInDataRange " + howManyDisplaysFitInDataRange);
			// the slider gives 0 to 100
			// this needs to be scaled from 0 to howManyDisplaysFitInDataRange
			float positionInRange = (float) ((float) sliderValue / 100.0) * howManyDisplaysFitInDataRange;
			System.out.println("positionInRange " + positionInRange);

			// the slider represents the left side of the graph
			// so one display needs to be subtracted
			long adjustedDataRange = dataRange - displayedRange;
			float displayInRangeAdjusted = (float) ((float) adjustedDataRange / (float) displayedRange);
			float positionInRangeAdjusted = (float) ((float) sliderValue / 100.0) * displayInRangeAdjusted;
			float posInRange_0to1 = positionInRangeAdjusted / displayInRangeAdjusted;
			System.out.println("adjustedDataRange " + adjustedDataRange);
			System.out.println("displayInRangeAdjusted " + displayInRangeAdjusted);
			System.out.println("positionInRangeAdjusted " + positionInRangeAdjusted);
			System.out.println("posInRange_0to1 " + posInRange_0to1);

			newStart = oldestDateReceivedMs + ((long) (adjustedDataRange * posInRange_0to1));
			newFinish = newStart + displayedRange;

		}
		System.out.println("newStart " + newStart);
		System.out.println("newFinish " + newFinish);

		System.out.println("oldestDateReceivedMs " + oldestDateReceivedMs);
		System.out.println("latestDateReceivedMs " + latestDateReceivedMs);

//		if (sliderValue < lastValue) { // left
//			displayedMin = displayedMin - HOUR_MS;
//			displayedMax = displayedMax - HOUR_MS;
//		} else { // right
//			displayedMin = displayedMin + HOUR_MS;
//			displayedMax = displayedMax + HOUR_MS;
//		}
		DateRange range = new DateRange(newStart, newFinish);
		dateAxis.setRange(range);
	}

}
