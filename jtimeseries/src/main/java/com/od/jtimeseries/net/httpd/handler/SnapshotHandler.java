package com.od.jtimeseries.net.httpd.handler;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.identifiable.FindCriteria;
import com.od.jtimeseries.identifiable.QueryResult;
import com.od.jtimeseries.net.httpd.NanoHTTPD;
import com.od.jtimeseries.net.httpd.response.NanoHttpResponse;
import com.od.jtimeseries.net.httpd.xml.AttributeName;
import com.od.jtimeseries.net.httpd.xml.ElementName;
import com.od.jtimeseries.net.httpd.xml.HttpParameterName;
import com.od.jtimeseries.net.httpd.xml.XmlValue;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 20/12/11
 * Time: 19:41
 *
 * A snapshot of the latest values from all timeseries under a given tree node
 */
public class SnapshotHandler extends AbstractHandler {

   public static final String SNAPSHOT_POSTFIX = "seriessnapshot";
   public final static String SNAPSHOT_XSL_RESOURCE = System.getProperty("JTimeSeriesTimeseriesSnapshotXslResource", "seriessnapshot.xsl");

   private FindCriteria<IdentifiableTimeSeries> findCriteria;

   public SnapshotHandler(TimeSeriesContext rootContext) {
       this(rootContext, FindCriteria.FIND_ALL);
   }

   public SnapshotHandler(TimeSeriesContext rootContext, FindCriteria<IdentifiableTimeSeries> findCriteria) {
       super(rootContext);
       this.findCriteria = findCriteria;
   }

   public NanoHttpResponse createResponse(String uri, String method, Properties header, Properties params) {
       NanoHttpResponse result;
       TimeSeriesContext context = findContextForRequest(uri);
       if ( context == null) {
           result = createNotFoundResponse(uri);
       } else {
           setSearchCriteria(params);
           result = new SeriesSnapshotResponse(context);
       }
       return result;
   }

    private void setSearchCriteria(Properties parms) {
        if ( parms.containsKey(HttpParameterName.substringSearch)) {
            //wrap the existing criteria and delegate to it, to perform and extra substring search
            String substring = parms.get(HttpParameterName.substringSearch).toString();
            findCriteria = new FindBySubstringSearchCriteria(findCriteria, substring);
        }
    }

    private void writeSnapshotResponse(PrintWriter pw, TimeSeriesContext context) {
       pw.write("<?xml version=\"1.0\"?>");
       pw.write("\n<?xml-stylesheet type=\"text/xsl\" href=\"/");
       pw.write(SNAPSHOT_XSL_RESOURCE);
       pw.write("\"?>");
       pw.write("\n<timeSeries>");

       //find all timeseries whcih we want to include in the index
       QueryResult<IdentifiableTimeSeries> series = context.findAll(
           IdentifiableTimeSeries.class, findCriteria
       );
       for (IdentifiableTimeSeries t : series.getAllMatches()) {
           appendSeriesSnapshot(pw, t);
       }
       pw.write("\n</timeSeries>");
   }

   protected void appendSeriesSnapshot(PrintWriter pw, IdentifiableTimeSeries s) {
       pw.write("\n<");
       pw.write(ElementName.series.toString());
       pw.write(" ");
       pw.write(AttributeName.parentPath.toString());
       pw.write("=\"");
       pw.write(encodeXml(s.getParentPath()));
       pw.write("\"");
       pw.write(" ");
       pw.write(AttributeName.id.toString());
       pw.write("=\"");
       pw.write(encodeXml(s.getId()));
       pw.write("\"");
       pw.write(" ");
       pw.write(AttributeName.latestItemTimestamp.toString());
       pw.write("=\"");
       long latestTimestamp = s.getLatestTimestamp();
       pw.write(latestTimestamp == -1 ? XmlValue.NaN.name() : String.valueOf(latestTimestamp));
       pw.write("\"");
       pw.write(" ");
       writeDatetimeAttribute(pw, latestTimestamp, new Date());
       pw.write(AttributeName.latestItemValue.toString());
       pw.write("=\"");
       TimeSeriesItem latestItem = s.getLatestItem();
       writeDoubleValueOrNaN(pw, latestItem == null ? Double.NaN : latestItem.doubleValue());
       pw.write("\"");
       pw.write("/>");
   }

    private class SeriesSnapshotResponse extends NanoHttpResponse {

       private TimeSeriesContext context;

       public SeriesSnapshotResponse(TimeSeriesContext context) {
           super(NanoHTTPD.HTTP_OK, "text/xml");
           this.context = context;
       }

       public void writeResponseBody(OutputStream out, PrintWriter pw) {
           writeSnapshotResponse(pw, context);
       }
   }

   private class FindBySubstringSearchCriteria implements FindCriteria<IdentifiableTimeSeries> {

       private FindCriteria<IdentifiableTimeSeries> delegateCriteria;
       private String substring;

       public FindBySubstringSearchCriteria(FindCriteria<IdentifiableTimeSeries> delegateCriteria, String substring) {

           this.delegateCriteria = delegateCriteria;
           this.substring = substring;
       }

       public boolean matchesCriteria(IdentifiableTimeSeries identifiable) {
           return delegateCriteria.matchesCriteria(identifiable) &&
                   identifiable.getPath().contains(substring);
       }
   }

}

