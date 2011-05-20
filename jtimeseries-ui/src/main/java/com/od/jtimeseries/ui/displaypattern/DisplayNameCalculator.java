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
package com.od.jtimeseries.ui.displaypattern;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.config.ConfigAware;
import com.od.jtimeseries.ui.config.DisplayNamePattern;
import com.od.jtimeseries.ui.config.DisplayNamePatternConfig;
import com.od.jtimeseries.ui.config.TimeSeriousConfig;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.util.NamedExecutors;
import com.od.swing.util.UIUtilities;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.Executor;
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
public class DisplayNameCalculator implements DisplayNamePatternDialog.DisplayPatternListener, ConfigAware {

    private static final Executor displayNameUpdateExecutor = NamedExecutors.newSingleThreadExecutor("DisplayNameCalculator");

    private List<DisplayNamePattern> displayNamePatterns = new ArrayList<DisplayNamePattern>();
    private Map<DisplayNamePattern, Pattern> patternMap = new HashMap<DisplayNamePattern, Pattern>();

    //allow contexts to be garbage collected
    private List<WeakReference<TimeSeriesContext>> rootContexts = new LinkedList<WeakReference<TimeSeriesContext>>();

    public void addRootContext(TimeSeriesContext rootContext) {
        rootContexts.add(new WeakReference<TimeSeriesContext>(rootContext));
    }

    public void updateDisplayNames(final List<UIPropertiesTimeSeries> l) {
        Runnable runnable = new Runnable() {
            public void run() {
                for ( UIPropertiesTimeSeries ts : l) {
                    calculateAndUpdateDisplayName(ts);
                }
            }
        };
        displayNameUpdateExecutor.execute(runnable);
    }

    public void displayPatternsChanged(List<DisplayNamePattern> newPatterns, boolean applyNow) {
        displayNamePatterns = newPatterns;
        patternMap.clear();
        for ( DisplayNamePattern pattern : newPatterns) {
            if (pattern.isValid()) {
                patternMap.put(pattern, Pattern.compile(pattern.getPattern()));
            }
            pattern.setFailed(! pattern.isValid());
        }

        if ( applyNow ) {
            applyPatternsToAllTimeseries();
        }
    }

    private void applyPatternsToAllTimeseries() {
        Iterator<WeakReference<TimeSeriesContext>> i = rootContexts.iterator();
        while(i.hasNext()) {
            WeakReference<TimeSeriesContext> s = i.next();
            TimeSeriesContext c = s.get();
            if ( c != null) {
                List<UIPropertiesTimeSeries> l = c.findAll(UIPropertiesTimeSeries.class).getAllMatches();
                updateDisplayNames(l);
            } else {
                i.remove();
            }
        }
    }

    private void calculateAndUpdateDisplayName(final UIPropertiesTimeSeries s) {
        String path = s.getPath();
        String displayName = s.getId();
        for (DisplayNamePattern p : patternMap.keySet()) {
            try {
                Matcher m = patternMap.get(p).matcher(path);
                if ( m.matches() ) {
                    displayName = m.replaceAll(p.getReplacement());
                    break;
                }
            } catch (Throwable e) {
                p.setFailed(true);
            }
        }

        if ( ! s.getDisplayName().equals(displayName)) {
            updateDisplayName(s, displayName);
        }
    }

    private void updateDisplayName(final UIPropertiesTimeSeries s, final String displayName) {
        UIUtilities.runInDispatchThread(
            new Runnable() {
                public void run() {
                    //UIPropertiesTimeSeries property should always be set on event thread
                    //while jide bean table model is in use
                    s.setDisplayName(displayName);
                }
            }
        );
    }

    public DisplayNamePatternConfig getDisplayNamePatternConfig() {
        return new DisplayNamePatternConfig(displayNamePatterns);
    }

    public void setDisplayNamePatternConfig(DisplayNamePatternConfig c) {
        displayPatternsChanged(c.getDisplayNamePatterns(), false);
    }

    public void prepareConfigForSave(TimeSeriousConfig config) {
        config.setDisplayNamePatterns(getDisplayNamePatternConfig());
    }

    public void restoreConfig(TimeSeriousConfig config) {
        setDisplayNamePatternConfig(config.getDisplayNamePatterns());
    }

    public List<ConfigAware> getConfigAwareChildren() {
        return Collections.emptyList();
    }

    public void clearConfig() {
        displayNamePatterns.clear();
        patternMap.clear();
    }
}
