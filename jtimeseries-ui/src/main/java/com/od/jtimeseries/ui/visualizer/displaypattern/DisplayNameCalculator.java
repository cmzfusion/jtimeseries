/**
 * Copyright (C) 2009 (nick @ objectdefinitions.com)
 *
 * This file is part of JTimeseries.
 *
 * JTimeseries is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimeseries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JTimeseries.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.od.jtimeseries.ui.visualizer.displaypattern;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.ui.timeseries.RemoteChartingTimeSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 01-Jun-2009
 * Time: 11:39:23
 *
 * Store a list of patterns to calculate displayName from contextPath for a TimeSeries
 * Implement logic to apply the patterns to a new series, or to all the series in TimeSeriesContext
 */
public class DisplayNameCalculator implements DisplayPatternDialog.DisplayPatternListener {

    private List<DisplayNamePattern> displayNamePatterns = new ArrayList<DisplayNamePattern>();
    private Map<DisplayNamePattern, Pattern> patternMap = new HashMap<DisplayNamePattern, Pattern>();
    private TimeSeriesContext rootContext;

    public DisplayNameCalculator(TimeSeriesContext rootContext) {
        this.rootContext = rootContext;
    }

    public void setDisplayName(RemoteChartingTimeSeries s) {
        String path = s.getPath();
        String displayName = s.getId();
        for (DisplayNamePattern p : patternMap.keySet()) {
            Matcher m = patternMap.get(p).matcher(path);
            if ( m.matches() ) {
                displayName = m.replaceAll(p.getReplacement());
                break;
            }
        }
        s.setDisplayName(displayName);
    }

    public void displayPatternsChanged(List<DisplayNamePattern> newPatterns, boolean applyNow) {
        displayNamePatterns = newPatterns;
        patternMap.clear();
        for ( DisplayNamePattern pattern : newPatterns) {
            if (pattern.isValid()) {
                patternMap.put(pattern, Pattern.compile(pattern.getPattern()));
            }
        }

        if ( applyNow ) {
            applyPatternsToAllTimeseries();
        }
    }

    public void applyPatternsToAllTimeseries() {
        List<IdentifiableTimeSeries> l = rootContext.findAllTimeSeries().getAllMatches();
        for ( IdentifiableTimeSeries i : l) {
            setDisplayName((RemoteChartingTimeSeries)i);
        }
    }

    public List<DisplayNamePattern> getDisplayNamePatterns() {
        return displayNamePatterns;
    }

    public void setDisplayNamePatterns(List<DisplayNamePattern> displayNamePatterns) {
        displayPatternsChanged(displayNamePatterns, false);
    }
}
