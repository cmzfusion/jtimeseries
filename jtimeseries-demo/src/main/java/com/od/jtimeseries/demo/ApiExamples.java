package com.od.jtimeseries.demo;

import com.od.jtimeseries.JTimeSeries;
import com.od.jtimeseries.capture.function.CaptureFunctions;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.httpd.JTimeSeriesHttpd;
import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.source.ValueRecorder;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.time.TimePeriod;

import java.io.IOException;
import java.util.List;

import static com.od.jtimeseries.capture.function.CaptureFunctions.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 12/12/11
 * Time: 07:52
 */
public class ApiExamples {

    public ApiExamples() throws IOException {

        //Create a root context, which is a factory for everything else
        TimeSeriesContext context = JTimeSeries.createRootContext();

        //This creates a ValueRecorder a Capture for the Median values every 30s and a TimeSeries within the context
        ValueRecorder v = context.createValueRecorderSeries(
            "ExampleValueRecorder",
            "Sampling some values in my application",
            MEDIAN(Time.seconds(30))
        );

        //At some point later, on this Thread or another, record some values ...
        v.newValue(10);
        v.newValue(20);


        Counter counter = context.createCounterSeries(
            "ExampleCounter",
            "Maintaining a count in my application",
            TOTAL_COUNT(Time.minutes(1)),  //record the latest value of the counter every 1 minute
            RAW_VALUES  //record a value every time the count changes
        );

        //At some point later, on this Thread or another ...
        counter.incrementCount();
        counter.decrementCount();
        counter.incrementCount(10);

        //To find the TimeSeries where MEDIAN values from the ValueRecorder are being recorded
        TimeSeries s = context.findTimeSeries(v).getFirstMatch();
        System.out.println("Items in my TimeSeries: " + s.size());
        System.out.println("Last item value " + s.getLatestItem().doubleValue());

        List<? extends TimeSeries> l = context.findTimeSeries(counter).getAllMatches();

        //Create a HTTP server which allows me to visualize TimeSeries data in a browser, or via TimeSerious UI
        JTimeSeriesHttpd httpd = new JTimeSeriesHttpd(8080, context);
        httpd.start();
        //now I can fire up my browser and point it to http://localhost:8080 to navigate and see the timeseries charts
        //for the Counter and ValueRecorder data collected above!

    }

}
