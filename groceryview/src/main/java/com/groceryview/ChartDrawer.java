package com.groceryview;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.lang.Math;

import java.awt.Color;

/* Utility to create charts for receipt data */

public class ChartDrawer {

    public static TimeSeries makeTimeSeries (List<Date> dates, List<Float> totalPaid) {
        TimeSeries series = new TimeSeries("Total Paid for Groceries");
        assert dates.size() == totalPaid.size();
        for (int i = 0; i < dates.size(); i++) {
            // addOrUpdate will add a new value if the date is not already in the series
            System.out.println("Adding date: " + dates.get(i) + " with total paid: " + totalPaid.get(i));
            series.addOrUpdate(new Day(dates.get(i)), totalPaid.get(i));
        }
        return series;
    }

    public static JFreeChart createTotalPaidChart(TimeSeries totalPaidSeries) {
        TimeSeriesCollection totalPaidDataset = new TimeSeriesCollection(totalPaidSeries);
        JFreeChart totalPaidTimeSeriesChart = ChartFactory.createTimeSeriesChart(
                "Total Paid for Groceries",
                "Date",
                "Total Paid",
                totalPaidDataset,
                false,
                true,
                false
        );
        XYPlot plot = totalPaidTimeSeriesChart.getXYPlot();
        // changes chart foreground color
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.decode("#16821a"));
        plot.setRenderer(0, renderer); 
        return totalPaidTimeSeriesChart;
    }

    public static JFreeChart createItemFrequencyBarChart (List<String> itemNames) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        HashSet<String> uniqueNames = new HashSet<>();
        itemNames.forEach(uniqueNames::add);
        for (String uniqueName: uniqueNames) {
            int frequency = Collections.frequency(itemNames, uniqueName);
            dataset.addValue(frequency, "Frequency", uniqueName);
        }
        JFreeChart frequencyBarChart = ChartFactory.createBarChart(
            "Grocery item frequency in receipts",
            "Item name",
            "Frequency",
            dataset
        );
        // changes chart foreground color
        CategoryPlot plot = frequencyBarChart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        Color darkGreen = Color.decode("#16821a");        
        renderer.setSeriesPaint(0, darkGreen);
        return frequencyBarChart;
    }

    public static JFreeChart createItemFrequencyBarChart(List<String> itemNames, int numItems) {
        // Store item frequency in a map
        Map<String, Integer> frequencyMap = new HashMap<>();
        for (String item : itemNames) {
            frequencyMap.put(item, frequencyMap.getOrDefault(item, 0) + 1);
        }
        // Create new data structure where low frequency items are removed
        PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>(
                Comparator.comparingInt(Map.Entry::getValue)
        );
        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            pq.offer(entry);
            if (pq.size() > numItems) {
                pq.poll();
            }
        }
        // Extract top items and sort them by frequency in descending order
        List<Map.Entry<String, Integer>> topItems = new ArrayList<>(pq);
        topItems.sort((a, b) -> b.getValue() - a.getValue());
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : topItems) {
            dataset.addValue(entry.getValue(), "Frequency", entry.getKey());
        }
        JFreeChart frequencyBarChart = ChartFactory.createBarChart(
                "Top " + numItems + " Grocery Items by Frequency",
                "Item name",
                "Frequency",
                dataset
        );
        // changes chart foreground color
        CategoryPlot plot = frequencyBarChart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        Color darkGreen = Color.decode("#16821a");        
        renderer.setSeriesPaint(0, darkGreen);
        return frequencyBarChart;
    }
    
    // Generate example data for testing time series chart
    public static TimeSeries makeExampleChartData () {
        TimeSeries series = new TimeSeries("Total Paid for Groceries");
        ArrayList<Date> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        // Populate dateList with dates from the last month
        for (int i = 0; i < 10; i++) {
            calendar.setTime(calendar.getTime()); // Get the current date
            calendar.add(Calendar.DAY_OF_MONTH, -(10 - i));
            dateList.add(calendar.getTime());
        }
        // Populate the series with random data
        for (int i = 0; i < 10; i++) {
            series.add(new Day(dateList.get(i)), (float) (Math.random() * 80));
        }
        return series;
    }
}
