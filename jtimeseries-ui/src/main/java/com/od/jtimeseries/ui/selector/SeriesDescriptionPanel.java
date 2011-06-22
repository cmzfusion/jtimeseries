/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
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
package com.od.jtimeseries.ui.selector;

import com.od.jtimeseries.ui.selector.shared.TitleLabelPanel;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 07-Jan-2009
 * Time: 11:10:10
 */
public class SeriesDescriptionPanel extends TitleLabelPanel {

    private JEditorPane description;

    public SeriesDescriptionPanel() {
        description = new JEditorPane();
        description.setContentType("text/html");
        description.setEditable(false);
        JScrollPane descriptionScrollPane = new JScrollPane(description);
        setLayout(new BorderLayout());
        add(descriptionScrollPane, BorderLayout.CENTER);
    }

    public void setSelectedSeries(UIPropertiesTimeSeries s) {
        if ( s != null) {
            description.setText("<html><body>" +
                    "<table><tr><th align='left'><font color='navy'>" + s.getId().trim() + "</font></th></tr>" +
                    "<tr><td><font color='navy'>" + s.getDescription().trim() + "</font></td></tr>" +
                    "</table><body></html>");

            description.setCaretPosition(0); //scroll to top
        }
    }
}
