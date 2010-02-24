package com.od.jtimeseries.ui.selector.table;

import com.od.jtimeseries.ui.timeseries.RemoteChartingTimeSeries;
import com.od.jtimeseries.context.ContextProperties;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 24-Feb-2010
 * Time: 17:30:29
 */
public class SummaryStatsTableModel extends DynamicColumnsTableModel<RemoteChartingTimeSeries> {

    private List<String> propertyNamesList = new ArrayList<String>();
    private Map<String,String> propertyNameToStatName = new HashMap<String,String>();

    public SummaryStatsTableModel(BeanPerRowModel<RemoteChartingTimeSeries> wrappedModel) {
        super(wrappedModel);
        initialize();
    }

    protected boolean requiresStructureChange(int firstRow, int lastRow) {
        boolean result = false;
        int oldSize = propertyNamesList.size();

        Set<String> summaryProperties = getRequiredSummaryProps(firstRow, lastRow);
        if (summaryProperties.size() != oldSize) {
            propertyNamesList.clear();
            propertyNamesList.addAll(summaryProperties);
            result = true;
        }
        return result;
    }

    private Set<String> getRequiredSummaryProps(int firstRow, int lastRow) {
        Set<String> summaryProperties = new TreeSet<String>(propertyNamesList);
        for ( int row = firstRow; row <= lastRow; row++) {
            RemoteChartingTimeSeries s = getObject(row);
            for ( Object prop : s.getProperties().keySet()) {
                String propertyName = (String) prop;
                if (! propertyNameToStatName.containsKey(propertyName) && ContextProperties.isSummaryStatsProperty(propertyName)) {
                    addSummaryProperty(summaryProperties, propertyName);
                }
            }
        }
        return summaryProperties;
    }

    private void addSummaryProperty(Set<String> summaryProperties, String propertyName) {
        String statisticName = ContextProperties.parseStatisticName(propertyName);
        propertyNameToStatName.put(propertyName, statisticName);
        summaryProperties.add(propertyName);
    }

    protected Object getValueForDynamicColumn(int rowIndex, int extraColsIndex) {
        String propertyName = propertyNamesList.get(extraColsIndex);
        return getObject(rowIndex).getProperty(propertyName);
    }

    public int getDynamicColumnCount() {
        return propertyNamesList.size();
    }

    protected String getDynamicColumnName(int extraColsIndex) {
        return propertyNamesList.get(extraColsIndex);
    }

    protected Class<?> getDynamicColumnClass(int extraColsIndex) {
        return String.class;
    }
}
