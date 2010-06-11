package com.od.jtimeseries.agent;

import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.impl.DefaultIdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.impl.DefaultTimeSeriesFactory;
import com.od.jtimeseries.timeseries.impl.RoundRobinTimeSeries;
import com.od.jtimeseries.util.identifiable.Identifiable;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 11-Jun-2010
 * Time: 16:44:20
 */
public class RoundRobinSeriesFactory extends DefaultTimeSeriesFactory {

    private int maxSeriesSize;

    public RoundRobinSeriesFactory(int maxSeriesSize) {
        this.maxSeriesSize = maxSeriesSize;
    }

    public <E extends Identifiable> E createTimeSeries(Identifiable parent, String path, String id, String description, Class<E> classType, Object... parameters) {
        if ( classType.isAssignableFrom(IdentifiableTimeSeries.class)) {
            return (E)new DefaultIdentifiableTimeSeries(
                id,
                description,
                new RoundRobinTimeSeries(maxSeriesSize)
            );
        }
        return super.createTimeSeries(parent, path, id, description, classType, parameters);
    }

}
