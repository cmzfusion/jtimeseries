package com.od.jtimeseries.server.summarystats;

import com.od.jtimeseries.JTimeSeries;
import com.od.jtimeseries.component.util.cache.LRUCache;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.server.serialization.SerializationException;
import com.od.jtimeseries.server.serialization.TestRoundRobinSerializer;
import com.od.jtimeseries.server.serialization.TimeSeriesSerializer;
import com.od.jtimeseries.server.timeseries.FilesystemTimeSeries;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.Item;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunctions;
import com.od.jtimeseries.timeseries.impl.RoundRobinTimeSeries;
import com.od.jtimeseries.util.time.Time;
import junit.framework.TestCase;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 01/05/12
 * Time: 07:07
 */
public class TestSummaryStatsCalculator extends TestCase {

    TimeSeriesContext rootContext;
    FilesystemTimeSeries testSeries;
    public final TimeSeriesSerializer serializer;

    public TestSummaryStatsCalculator() throws SerializationException {
        rootContext = JTimeSeries.createRootContext();
        serializer = TestRoundRobinSerializer.createTestSerializer();
        createTestSeries(serializer);
    }

    public void testAll() throws InterruptedException {
        defaultStats();  //create some stats
        todayOnlyStats();  //delete them, since the last timestamp not 'today'
    }

    private void defaultStats() throws InterruptedException {
        List<SummaryStatistic> statistics = new LinkedList<SummaryStatistic>();

        final CountDownLatch l = new CountDownLatch(2);
        DefaultSummaryStatistic meanStat = new DefaultSummaryStatistic("Test1", AggregateFunctions.MEAN()) {
            public void recalcSummaryStatistic(IdentifiableTimeSeries timeSeries) {
                super.recalcSummaryStatistic(timeSeries);
                l.countDown();
            }
        };
        statistics.add(meanStat);
        DefaultSummaryStatistic maxStat = new DefaultSummaryStatistic("Test2", AggregateFunctions.MAX()) {
            public void recalcSummaryStatistic(IdentifiableTimeSeries timeSeries) {
                super.recalcSummaryStatistic(timeSeries);
                l.countDown();
            }
        };
        statistics.add(maxStat);

        SummaryStatisticsCalculator calculator = new SummaryStatisticsCalculator(rootContext, Time.milliseconds(50), statistics);
        calculator.start();
        l.await();
        calculator.stop();

        assertEquals(2, (int) Integer.valueOf(testSeries.getProperty(meanStat.getSummaryStatProperty())));
        assertEquals(3, (int)Integer.valueOf(testSeries.getProperty(maxStat.getSummaryStatProperty())));
    }

    //test today only stats deleted if no items from today
    private void todayOnlyStats() throws InterruptedException {

        List<SummaryStatistic> statistics = new LinkedList<SummaryStatistic>();

        final CountDownLatch l = new CountDownLatch(2);
        DefaultSummaryStatistic meanStat = new TodayOnlySummaryStatistic("Test1", AggregateFunctions.MEAN()) {
            public void deleteSummaryStatistic(IdentifiableTimeSeries timeSeries) {
                super.deleteSummaryStatistic(timeSeries);
                l.countDown();
            }
        };
        statistics.add(meanStat);
        DefaultSummaryStatistic maxStat = new TodayOnlySummaryStatistic("Test2", AggregateFunctions.MAX()) {
            public void deleteSummaryStatistic(IdentifiableTimeSeries timeSeries) {
                super.deleteSummaryStatistic(timeSeries);
                l.countDown();
            }
        };
        statistics.add(maxStat);

        SummaryStatisticsCalculator calculator = new SummaryStatisticsCalculator(rootContext, Time.milliseconds(50), statistics);
        calculator.start();
        l.await();
        calculator.stop();

        assertNull(testSeries.getProperty(meanStat.getSummaryStatProperty()));
        assertNull(testSeries.getProperty(maxStat.getSummaryStatProperty()));
    }

    private void createTestSeries(TimeSeriesSerializer s) throws SerializationException {
        testSeries = new FilesystemTimeSeries("", "test1", "test1", s, new LRUCache<Identifiable, RoundRobinTimeSeries>(10), 10, Time.minutes(1), Time.minutes(1));
        rootContext.addChild(testSeries);

        testSeries.addItem(new Item(1, 1));
        testSeries.addItem(new Item(1, 2));
        testSeries.addItem(new Item(1, 3));
    }

}
