package com.od.jtimeseries.ui.selector.table;

import com.jidesoft.grid.BeanTableModel;
import com.jidesoft.grid.ColorCellRenderer;
import com.od.jtimeseries.ui.timeseries.RemoteChartingTimeSeries;

import javax.swing.table.TableCellRenderer;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.beans.IntrospectionException;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 27-Feb-2010
 * Time: 15:49:53
 * To change this template use File | Settings | File Templates.
 */
public class TableModelCreator implements TableColumnManager.DefaultColumnSettings {

    private java.util.List<ColumnInfo> columns = new ArrayList<ColumnInfo>();

    private Map<String, Integer> columnWidthsByColumnName = new HashMap<String,Integer>();
    private String selectionText;

    public TableModelCreator(String selectionText) {
        this.selectionText = selectionText;
        buildColumnList();
    }

    public BeanPerRowModel<RemoteChartingTimeSeries> createTableModel() {
        String[] colConfigString = generateColumnConfigStringForBeanTableModel();
        BeanTableModel beanTableModel = null;
        try {
            beanTableModel = new BeanTableModel<RemoteChartingTimeSeries>(
                new ArrayList(),
                RemoteChartingTimeSeries.class,
                colConfigString
            );
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        BeanPerRowModel<RemoteChartingTimeSeries> modelWrapper = new BeanPerRowModel.JideBeanModelWrapper<RemoteChartingTimeSeries>(beanTableModel);
        PathTokenizingTableModel pathTokenizingTableModel = new PathTokenizingTableModel(modelWrapper);
        SummaryStatsTableModel summaryStatsTableModel = new SummaryStatsTableModel(pathTokenizingTableModel);
        int[] editableCols = new int[] {0, 1, 3, 4, 8};
        return new EditableColumnsTableModel<RemoteChartingTimeSeries>(summaryStatsTableModel, editableCols);
    }

    public int getDefaultColumnWidth(String columnName) {
        Integer i = columnWidthsByColumnName.get(columnName);
        return i == null ? 75 : i;
    }

    public TableCellRenderer getCellRenderer(String columnName) {
        TableCellRenderer result = null;
        for ( ColumnInfo i : columns) {
            if ( i.getDisplayName().equals(columnName)) {
                result = i.getCellRenderer();
            }
        }
        return result;
    }

    //jide BeanTableModel requires the propertyNames and column display names as a String[]
    private String[] generateColumnConfigStringForBeanTableModel() {
        java.util.List<String> colConfigStrings = new LinkedList<String>();
        for(ColumnInfo c : columns) {
            colConfigStrings.add(c.getPropertyName());
            colConfigStrings.add(c.getDisplayName());
        }
        String[] colConfigString = colConfigStrings.toArray(new String[colConfigStrings.size()]);
        return colConfigString;
    }


    //for each predetermined column, the bean property name RemoteChartingTimeSeries,
    //column name to display and a preferred width. We use this to create the BeanTableModel
    //and size the columns in ColumnModel. The underlying table model also generates some
    //columns dynamically from the tokens in the series path
    private void buildColumnList() {
        columns.add(new ColumnInfo("selected", selectionText, 65));
        columns.add(new ColumnInfo("displayName", "Display Name", 175));
        columns.add(new ColumnInfo("id", "Id", 75));
        columns.add(new ColumnInfo("maxDaysHistory", "Max Days", 100));
        columns.add(new ColumnInfo("refreshTimeSeconds", "Refresh(s)", 100));
        columns.add(new ColumnInfo("path", "Path", 100));
        columns.add(new ColumnInfo("URL", "URL", 100));
        columns.add(new ColumnInfo("lastRefreshTime", "Last Refresh", 50, new TimeRenderer()));
        columns.add(new ColumnInfo("color", "Colour", 50, new ColorCellRenderer() {{setColorValueVisible(false);}}));
        populateColumnWidthsMap();
    }

    private void populateColumnWidthsMap() {
        for ( ColumnInfo c : columns) {
            columnWidthsByColumnName.put(c.getDisplayName(), c.getDefaultWidth());
        }
    }

    private static class ColumnInfo {
        private String propertyName;
        private String displayName;
        private int defaultWidth;
        private TableCellRenderer cellRenderer;

        private ColumnInfo(String propertyName, String displayName, int defaultWidth) {
            this(propertyName, displayName, defaultWidth, null);
        }

        private ColumnInfo(String propertyName, String displayName, int defaultWidth, TableCellRenderer cellRenderer) {
            this.propertyName = propertyName;
            this.displayName = displayName;
            this.defaultWidth = defaultWidth;
            this.cellRenderer = cellRenderer;
        }

        public TableCellRenderer getCellRenderer() {
            return cellRenderer;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getDefaultWidth() {
            return defaultWidth;
        }
    }

}
