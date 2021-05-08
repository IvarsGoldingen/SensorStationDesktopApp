import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
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
import org.jfree.data.time.DateRange;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.ui.ApplicationFrame;

public class Graph extends ApplicationFrame implements ChangeListener {

	//For slider
	private static int SLIDER_INITIAL_VALUE = 100;
    private JSlider slider;
    private DateAxis domainAxis;
    private int lastValue = SLIDER_INITIAL_VALUE;
	
    // one day (milliseconds, seconds, minutes, hours, days)
    private static int DAY_MS = 1000 * 60 * 60 * 24 * 1;
    private static int HOUR_MS = 1000 * 60 * 60;
    
    DateAxis dateAxis;
    
	public Graph(String title) {
		super(title);
		//super(title);
	}

	public void createEmptyChart() {
		JFreeChart xylineChart = ChartFactory.createXYLineChart("Empty chart", "Category", "Score", null,
				PlotOrientation.VERTICAL, true, true, false);

		ChartPanel chartPanel = new ChartPanel(xylineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
		final XYPlot plot = xylineChart.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		plot.setRenderer(renderer);
		setContentPane(chartPanel);
	}

	public void drawLogItemTest(List<LogItem> logItems) {
		// Create a series of neccessary log data
		final XYSeries co2Series = new XYSeries("CO2");
		final XYSeries t1Series = new XYSeries("T1");
		long latestDateReceivedMs = 0;
		for (LogItem item : logItems) {
			//Get the last item time so it is known from when to draw the graph
			long timeOfItem = item.getTime();
			if (timeOfItem > latestDateReceivedMs) {
				latestDateReceivedMs = timeOfItem;
			}
			
			co2Series.add(timeOfItem, item.getCO2());
			t1Series.add(timeOfItem, item.getTemperature());
		}

		// create dataSet
		final XYSeriesCollection dataset = new XYSeriesCollection();
		//dataset.addSeries(co2Series);
		dataset.addSeries(t1Series);

		JFreeChart xylineChart = ChartFactory.createTimeSeriesChart(
				"logData", 
				"TIME", 
				"DATA", 
				dataset,
				true, true, false);
		

		ChartPanel chartPanel = new ChartPanel(xylineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
		chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 4, 4, 4),
                BorderFactory.createEtchedBorder()
            );
        chartPanel.setBorder(border);
        add(chartPanel);
        
		final XYPlot plot = xylineChart.getXYPlot();
		
		//Set ranges on axis
		dateAxis = (DateAxis)plot.getDomainAxis();
		//One day set
		long startOfDateAxis = latestDateReceivedMs - DAY_MS;
		dateAxis.setRange(startOfDateAxis, latestDateReceivedMs);
		
		NumberAxis valueAxis = (NumberAxis)plot.getRangeAxis();
		valueAxis.setRange(0, 100);
		
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setSeriesPaint(1, Color.GREEN);
		renderer.setSeriesStroke(0, new BasicStroke(1.0f));
		renderer.setSeriesStroke(1, new BasicStroke(1.0f));
		//Disable shapes of datapoints
		renderer.setDefaultShapesVisible(false);
		plot.setRenderer(renderer);
		setContentPane(chartPanel);
		
		JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));   
		
		this.slider = new JSlider(0, 100, SLIDER_INITIAL_VALUE);
        this.slider.addChangeListener(this);
        dashboard.add(this.slider);
        add(dashboard, BorderLayout.SOUTH);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		int value = this.slider.getValue();
        long minimum = dateAxis.getMinimumDate().getTime();
        long maximum = dateAxis.getMaximumDate().getTime();
        if (value<lastValue) { // left
            minimum = minimum - HOUR_MS;
            maximum = maximum - HOUR_MS;
        } else { // right
            minimum = minimum + HOUR_MS;
            maximum = maximum + HOUR_MS;
        }
        DateRange range = new DateRange(minimum,maximum);
        dateAxis.setRange(range);
        lastValue = value;
	}

}
