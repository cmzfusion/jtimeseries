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
import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 01-Jun-2009
* Time: 11:14:29
*/
public class EditDisplayNamePatternsAction extends AbstractAction {

    private Component componentToPositionDialog;
    private DisplayNameCalculator displayNameCalculator;
    private List<DisplayPatternDialog.DisplayPatternListener> displayPatternListeners = new ArrayList<DisplayPatternDialog.DisplayPatternListener>();

    public EditDisplayNamePatternsAction(TimeSeriesContext rootContext, Component componentToPositionDialog, DisplayNameCalculator displayNameCalculator) {
        super("Display Name Patterns", ImageUtils.DISPLAY_NAME_16x16);
        this.componentToPositionDialog = componentToPositionDialog;

        //the calculator stores the list of patterns and is responsible for applying them to timeseries
        this.displayNameCalculator = displayNameCalculator;
    }

    public void addDisplayPatternListener(DisplayPatternDialog.DisplayPatternListener l) {
        displayPatternListeners.add(l);
    }

    public void actionPerformed(ActionEvent e) {
        DisplayPatternDialog d = new DisplayPatternDialog(getDisplayNamePatterns());

        d.addDisplayPatternListener(displayNameCalculator);
        for ( DisplayPatternDialog.DisplayPatternListener l : displayPatternListeners ) {
            d.addDisplayPatternListener(l);
        }

        d.setLocationRelativeTo(componentToPositionDialog);
        d.setVisible(true);
    }

    public List<DisplayNamePattern> getDisplayNamePatterns() {
        return displayNameCalculator.getDisplayNamePatterns();
    }

    public void setDisplayNamePatterns(List<DisplayNamePattern> patterns) {
        displayNameCalculator.setDisplayNamePatterns(patterns);
    }

    public DisplayNameCalculator getDisplayNameCalculator() {
        return displayNameCalculator;
    }
}
