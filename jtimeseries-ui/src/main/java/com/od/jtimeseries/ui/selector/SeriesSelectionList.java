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
package com.od.jtimeseries.ui.selector;

import com.od.jtimeseries.ui.selector.shared.TitleLabelPanel;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06-Jan-2009
 * Time: 18:04:17
 */
public class SeriesSelectionList<E extends UIPropertiesTimeSeries> extends TitleLabelPanel implements SelectionManager<E> {

    private DefaultListModel listModel = new DefaultListModel();
    private JList seriesList;

    private List<TimeSeriesSelectorListener<E>> listeners = new ArrayList<TimeSeriesSelectorListener<E>>();

    public SeriesSelectionList() {
        setLayout(new BorderLayout());
        add(createTitleLabel("Selected Time Series"), BorderLayout.NORTH);
        seriesList = new JList(listModel);
        seriesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(seriesList),BorderLayout.CENTER);
        seriesList.setCellRenderer(new SeriesListCellRenderer());
    }

    public void addSelectionListener(ListSelectionListener l) {
        seriesList.addListSelectionListener(l);
    }

    public void addSelection(E s) {
        if ( s != null && ! listModel.contains(s)) {
            listModel.addElement(s);
            fireSeriesSelectionChanged();
        }
    }

    public void removeSelection(E s) {
        if ( s != null && listModel.contains(s)) {
            listModel.removeElement(s);
            fireSeriesSelectionChanged();
        }
    }

    @SuppressWarnings({"unchecked"})
    public void setSelectedTimeSeries(List<E> selections) {
        Set newSet = new HashSet(selections);
        Set oldSet = new HashSet(Arrays.asList(listModel.toArray()));
        //don't bother refreshing if the same selections
        if ( ! newSet.equals(oldSet)) {
            listModel.removeAllElements();
            for ( E s : selections) {
                listModel.addElement(s);
            }
            fireSeriesSelectionChanged();
        }
    }

    public List<E> getSelectedTimeSeries() {
        List<E> series = new ArrayList<E>();
        for(int loop=0; loop<listModel.size(); loop++) {
            series.add((E)listModel.getElementAt(loop));
        }
        return series;
    }

    public void addSelectionListener(TimeSeriesSelectorListener l) {
        listeners.add(l);
    }

    public void removeSelectionListener(TimeSeriesSelectorListener l) {
        listeners.remove(l);
    }

    private void fireSeriesSelectionChanged() {
        List<E> newSeries = getSelectedTimeSeries();
        List<TimeSeriesSelectorListener<E>> listeners = new ArrayList<TimeSeriesSelectorListener<E>>(this.listeners);
        for ( TimeSeriesSelectorListener<E> l : listeners )  {
            l.selectionChanged(newSeries);
        }
    }

    private class SeriesListCellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setIcon(ImageUtils.SERIES_ICON_16x16);
            setFont(getFont().deriveFont(Font.PLAIN));
            return this;
        }
    }
}
