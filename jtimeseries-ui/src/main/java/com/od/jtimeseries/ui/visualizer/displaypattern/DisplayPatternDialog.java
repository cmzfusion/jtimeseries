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

import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 01-Jun-2009
 * Time: 10:49:24
 */
public class DisplayPatternDialog extends JDialog {

    private List<DisplayPatternListener> displayPatternListeners = new ArrayList<DisplayPatternListener>();
    private DisplayPatternTable table;
    private static final int DIALOG_WIDTH = 400;

    public DisplayPatternDialog(List<DisplayNamePattern> patterns) {
        setTitle("Edit Display Name Patterns");
        setAlwaysOnTop(true);
        setModal(false);
        setSize(DIALOG_WIDTH, 600);
        addComponents(patterns);
    }

    public void addDisplayPatternListener(DisplayPatternListener l) {
        displayPatternListeners.add(l);
    }

    public void removeDisplayPatternListener(DisplayPatternListener l) {
        displayPatternListeners.remove(l);
    }

    private void addComponents(List<DisplayNamePattern> patterns) {
        table = new DisplayPatternTable(patterns);

        JButton applyNowButton = new JButton(new ApplyNowAction());
        JButton okButton = new JButton(new OkAction());
        JButton cancelButton = new JButton(new CancelAction());

        JComponent descriptionPanel = createDescriptionPanel();

        Box buttonBox = Box.createHorizontalBox();
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(applyNowButton);
        buttonBox.add(Box.createHorizontalStrut(5));
        buttonBox.add(okButton);
        buttonBox.add(cancelButton);
        buttonBox.setBorder(new EmptyBorder(5,5,5,5));

        setLayout(new BorderLayout());
        add(descriptionPanel, BorderLayout.NORTH);
        add(table, BorderLayout.CENTER);
        add(buttonBox, BorderLayout.SOUTH);
    }

    private JComponent createDescriptionPanel() {
        JPanel p = new JPanel();
        JLabel label = new JLabel("<html><font color='blue'>This dialog allows you to set up regular expressions to calculate the display name " +
                "for your timeseries from the series 'path'.<br><br>The expressions are automatically applied when downloading new series. " +
                "The apply button will apply the expressions to the existing series in your view.</font></html>");
        label.setPreferredSize(new Dimension(DIALOG_WIDTH - 15, 95));
        p.add(label);
        return p;
    }

    private class OkAction extends AbstractAction {
        public OkAction() {
            super("OK", ImageUtils.OK_16x16);
        }

        public void actionPerformed(ActionEvent e) {
            for ( DisplayPatternListener l : displayPatternListeners) {
                l.displayPatternsChanged(table.getDisplayPatterns(), false);
            }
            dispose();
        }
    }

    private class ApplyNowAction extends AbstractAction {
        public ApplyNowAction() {
            super("Apply Now", ImageUtils.DISPLAY_NAME_16x16);
        }

        public void actionPerformed(ActionEvent e) {
            for ( DisplayPatternListener l : displayPatternListeners) {
                l.displayPatternsChanged(table.getDisplayPatterns(), true);
            }
        }
    }

    private class CancelAction extends AbstractAction {
        public CancelAction() {
            super("Cancel", ImageUtils.CANCEL_16x16);
        }

        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }

    public static interface DisplayPatternListener {
        void displayPatternsChanged(List<DisplayNamePattern> newPatterns, boolean applyNow);
    }
}
