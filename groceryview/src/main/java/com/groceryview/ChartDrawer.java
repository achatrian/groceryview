package com.groceryview;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.lang.Math;

/* Utility to create charts for receipt data */


public class ChartDrawer {

    public static TimeSeries makeTimeSeries (List<Date> dates, List<Float> totalPaid) {
        TimeSeries series = new TimeSeries("Total Paid for Groceries");
        assert dates.size() == totalPaid.size();
        for (int i = 0; i < dates.size(); i++) {
            series.add(new Day(dates.get(i)), totalPaid.get(i));
        }
        return series;
    }

    public static ChartPanel createTotalPaidChartPanel(TimeSeries totalPaidSeries) {
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
        return new ChartPanel(chart);
    }
    
    public static TimeSeries makeExampleChartData () {
        TimeSeries series = new TimeSeries("Total Paid for Groceries");
        ArrayList<Date> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        
        // Get the current date
        Date today = calendar.getTime();
        
        // Populate dateList with dates from the last month
        for (int i = 0; i < 10; i++) {
            calendar.setTime(today);
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
