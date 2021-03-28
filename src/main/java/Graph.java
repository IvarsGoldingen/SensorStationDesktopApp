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
    private int delta = 1000 * 60 * 60 * 24 * 1;
    
	public Graph(String title) {
		super(new BorderLayout());
		//super(title);
		// TODO Auto-generated constructor stub
	}

	public void createEmptyChart() {
		JFreeChart xylineChart = ChartFactory.createXYLineChart("Empty chart", "Category", "Score", null,
				PlotOrientation.VERTICAL, true, true, false);

		ChartPanel chartPanel = new ChartPanel(xylineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
		final XYPlot plot = xylineChart.getXYPlot();

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		plot.setRenderer(renderer);
		setContentPane(chartPanel);
	}

	public void drawLogItemTest(List<LogItem> logItems) {
		// Create a series of neccessary log data
		final XYSeries co2Series = new XYSeries("CO2");
		final XYSeries t1Series = new XYSeries("T1");
		for (LogItem item : logItems) {
			co2Series.add(item.getTime(), item.getCO2());
			t1Series.add(item.getTime(), item.getTVOC());
		}

		// create dataSet
		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(co2Series);
		dataset.addSeries(t1Series);

		JFreeChart xylineChart = ChartFactory.createTimeSeriesChart(
				"logData", 
				"Category", 
				"Score", 
				dataset,
				true, true, false);

		ChartPanel chartPanel = new ChartPanel(xylineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
		chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 4, 4, 4),
                BorderFactory.createEtchedBorder()
            );
        chartPanel.setBorder(border);
        add(chartPanel);
        
		final XYPlot plot = xylineChart.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setSeriesPaint(1, Color.GREEN);
		renderer.setSeriesStroke(0, new BasicStroke(4.0f));
		renderer.setSeriesStroke(1, new BasicStroke(3.0f));
		plot.setRenderer(renderer);
		setContentPane(chartPanel);
		
		JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));   
		
		this.slider = new JSlider(0, 100, SLIDER_INITIAL_VALUE);
        this.slider.addChangeListener(this);
        dashboard.add(this.slider);
        add(dashboard, BorderLayout.SOUTH);
	}

	public void createChart() {

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		int value = this.slider.getValue();
        long minimum = domainAxis.getMinimumDate().getTime();
        long maximum = domainAxis.getMaximumDate().getTime();
        if (value<lastValue) { // left
            minimum = minimum - delta;
            maximum = maximum - delta;
        } else { // right
            minimum = minimum + delta;
            maximum = maximum + delta;
        }
        DateRange range = new DateRange(minimum,maximum);
        domainAxis.setRange(range);
        lastValue = value;
	}

}
