package com.od.jtimeseries.ui.selector.table;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.*;
import java.text.SimpleDateFormat;
import java.awt.*;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
* User: nick
* Date: 21-Feb-2010
* Time: 18:35:05
* To change this template use File | Settings | File Templates.
*/
class TimeRenderer extends DefaultTableCellRenderer {

    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        Date d = (Date) value;
        if ( d != null) {
            setText(timeFormat.format(d));
        }
        return this;
    }
}
