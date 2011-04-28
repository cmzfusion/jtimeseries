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
package com.od.jtimeseries.ui.util;

import com.jidesoft.grid.AbstractJideCellEditor;
import com.jidesoft.grid.BooleanCheckBoxCellEditor;
import com.jidesoft.grid.CellEditorManager;
import com.jidesoft.plaf.LookAndFeelFactory;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18-Nov-2009
 * Time: 17:17:36
 *
 * Apply jtimeseries jide license.
 * Can be disabled by calling setUseJTimeSeriesJideLicense, if you wish to use another jide license
 */
public class JideInitialization {

    private static boolean useJTimeSeriesJideLicense = true;
    private static volatile boolean isApplied;
    private static volatile boolean isCellEditingInitialized;

    public synchronized static void applyLicense() {
        if ( useJTimeSeriesJideLicense && ! isApplied ) {
            com.jidesoft.utils.Lm.verifyLicense("Nick Ebbutt", "jtimeseries", "nP8LYFIkFfoYrF3WhUkPzFJPe9bigQ3");
            isApplied = true;
        }
    }

    public synchronized static void setUseJTimeSeriesJideLicense(boolean useJTimeSeriesJideLicense) {
        JideInitialization.useJTimeSeriesJideLicense = useJTimeSeriesJideLicense;
    }

    public static void setupJide() {
        setupCellEditorManager();
    }

    //not currently used, think the latest swing windows l&f may look better
    public static void setupJideLookAndFeel() {
        LookAndFeelFactory.installJideExtension(LookAndFeelFactory.OFFICE2007_STYLE);
        LookAndFeelFactory.setDefaultStyle(LookAndFeelFactory.OFFICE2007_STYLE);
    }

    private static void setupCellEditorManager() {
        if ( ! isCellEditingInitialized ) {
            //setup single click editing on the BooleanCheckBoxCellEditor but 2 otherwise
            CellEditorManager.addCellEditorCustomizer(new CellEditorManager.CellEditorCustomizer() {
            public void customize(CellEditor cellEditor) {
                    if ( cellEditor instanceof BooleanCheckBoxCellEditor) {
                        ((BooleanCheckBoxCellEditor)cellEditor).setClickCountToStart(1);
                    } else if (cellEditor instanceof DefaultCellEditor) {
                        ((DefaultCellEditor)cellEditor).setClickCountToStart(2);
                    } else if (cellEditor instanceof AbstractJideCellEditor) {
                        ((AbstractJideCellEditor)cellEditor).setClickCountToStart(2);
                    }
                }
            });
            isCellEditingInitialized = true;
        }
    }
}
