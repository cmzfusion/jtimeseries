package com.od.jtimeseries.ui.util;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18-Nov-2009
 * Time: 17:17:36
 *
 * Apply jtimeseries jide license.
 * Can be disabled by calling setUseJTimeSeriesJideLicense, if you wish to use another jide license
 */
public class JTimeseriesJideLicense {

    private static boolean useJTimeSeriesJideLicense = true;
    private static boolean isApplied;

    public synchronized static void applyLicense() {
        if ( useJTimeSeriesJideLicense && ! isApplied ) {
            com.jidesoft.utils.Lm.verifyLicense("Nick Ebbutt", "jtimeseries", "nP8LYFIkFfoYrF3WhUkPzFJPe9bigQ3");
            isApplied = true;
        }
    }

    public synchronized static void setUseJTimeSeriesJideLicense(boolean useJTimeSeriesJideLicense) {
        JTimeseriesJideLicense.useJTimeSeriesJideLicense = useJTimeSeriesJideLicense;
    }
}
