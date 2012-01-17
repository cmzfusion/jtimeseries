package com.od.jtimeseries.server;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.identifiable.FindCriteria;
import com.od.jtimeseries.net.httpd.DefaultHandlerFactory;
import com.od.jtimeseries.net.httpd.handler.HttpHandler;
import com.od.jtimeseries.net.httpd.handler.SnapshotHandler;
import com.od.jtimeseries.server.timeseries.FilesystemTimeSeries;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 17/01/12
 * Time: 13:01
 * To change this template use File | Settings | File Templates.
 */
public class ServerHttpHandlerFactory extends DefaultHandlerFactory {

    public ServerHttpHandlerFactory(TimeSeriesContext rootContext) {
        super(rootContext);
    }

    public HttpHandler getHandler(String uri, String method, Properties header, Properties params) {

        if ( uri.endsWith(SnapshotHandler.SNAPSHOT_POSTFIX)) {
            return new SnapshotHandler(getRootContext(), new ServerSeriesFindCriteria());
        } else {
            return super.getHandler(uri, method, header, params);
        }
    }

    /**
     * A server optimisation to include in the index only series which have the latest value in memory
     * we don't want to trigger deserialization if this not the case, it could be very expensive
     *
     * TODO - we should save/read the last value in the header information, so that deserialization
     * is never necessary to read the latest value
     */
    private static class ServerSeriesFindCriteria implements FindCriteria<IdentifiableTimeSeries> {

        public boolean matchesCriteria(IdentifiableTimeSeries identifiable) {
            return identifiable instanceof FilesystemTimeSeries &&
                    ((FilesystemTimeSeries)identifiable).isLastItemInMemory();
        }
    }
}
