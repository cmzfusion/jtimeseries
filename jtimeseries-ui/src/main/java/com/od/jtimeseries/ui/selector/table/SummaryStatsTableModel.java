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

    public SummaryStatsTableModel(BeanPerRowModel<RemoteChartingTimeSeries> wrappedModel) {
        super(wrappedModel);
        initialize();
    }

    protected boolean updateRequiresStructureChange(int firstRow, int lastRow) {
        boolean result = false;
        int oldSize = propertyNamesList.size();

        addSummaryColumns(firstRow, lastRow);
        if (propertyNamesList.size() != oldSize) {
            result = true;
        }
        return result;
    }

    private void addSummaryColumns(int firstRow, int lastRow) {
        for ( int row = firstRow; row <= lastRow; row++) {
            RemoteChartingTimeSeries s = getObject(row);
            for ( Object prop : s.getProperties().keySet()) {
                String propertyName = (String) prop;
                if (! propertyNamesList.contains(propertyName)) {
                    if ( ContextProperties.isSummaryStatsProperty(propertyName) &&
                        ContextProperties.getSummaryStatsDataType(propertyName) == ContextProperties.SummaryStatsDataType.DOUBLE) {
                        addSummaryProperty(propertyName);
                    }
                }
            }
        }
    }

    private void addSummaryProperty(String propertyName) {
        propertyNamesList.add(propertyName);
        Collections.sort(propertyNamesList);
    }

    protected Object getValueForDynamicColumn(int rowIndex, int extraColsIndex) {
        String propertyName = propertyNamesList.get(extraColsIndex);
        String propertyValue = getObject(rowIndex).getProperty(propertyName);
        return propertyValue == null || "?".equals(propertyValue) ? null : Double.valueOf(propertyValue);
    }

    protected void doAddDynamicColumn(String columnName) {
        if ( ! propertyNamesList.contains(columnName)) {
            addSummaryProperty(columnName);
            fireTableStructureChanged();
        }
    }

    protected boolean isDynamicColumnInThisModel(String columnName) {
        return ContextProperties.isSummaryStatsProperty(columnName);
    }

    public int getDynamicColumnCount() {
        return propertyNamesList.size();
    }

    protected String getDynamicColumnName(int extraColsIndex) {
        return propertyNamesList.get(extraColsIndex);
    }

    protected Class<?> getDynamicColumnClass(int extraColsIndex) {
        return Double.class;
    }
}
