package com.groceryview;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.lang.Math;

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
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Total Paid for Groceries",
                "Date",
                "Total Paid",
                totalPaidDataset,
                false,
                true,
                false
        );
        return chart;
    }

    // public static JFreeChart createItemsHistogram (List<String> itemNames) {
    //             // Map strings to unique integers
    //     Map<String, Integer> stringToInt = new HashMap<>();
    //     int nextInt = 0;
    //     for (String value : itemNames) {
    //         if (!stringToInt.containsKey(value)) {
    //             stringToInt.put(value, nextInt++);
    //         }
    //     }

    //     // Encode strings into integers
    //     double[] encodedValues = new double[itemNames.size()];
    //     for (int i = 0; i < itemNames.size(); i++) {
    //         encodedValues[i] = stringToInt.get(itemNames.get(i));
    //     }

    //     // Create a histogram dataset
    //     HistogramDataset dataset = new HistogramDataset();
    //     dataset.addSeries("Frequency", encodedValues, stringToInt.size());

    //     // Create the chart
    //     JFreeChart histogram = ChartFactory.createHistogram(
    //             "String Value Histogram", // Chart title
    //             "String Values",          // X-axis label
    //             "Frequency",              // Y-axis label
    //             dataset                   // Dataset
    //     );

    //     // Customize x-axis labels
    //     CategoryPlot plot = histogram.getCategoryPlot();
    //     CategoryAxis xAxis = new CategoryAxis("String Values");
    //     for (Map.Entry<String, Integer> entry : stringToInt.entrySet()) {
    //         xAxis.addCategoryLabel(entry.getValue(), entry.getKey());
    //     }
    //     xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90); // Rotate labels if needed
    //     plot.setDomainAxis(xAxis);

    //     // Optional: Customize renderer for better visualization
    //     BarRenderer renderer = (BarRenderer) plot.getRenderer();
    //     renderer.setDrawBarOutline(false);
    //     return plot;
    // }
    
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
