package com.od.jtimeseries.ui.util;

import com.jidesoft.grid.CellEditorManager;
import com.jidesoft.grid.BooleanCheckBoxCellEditor;
import com.jidesoft.grid.AbstractJideCellEditor;
import com.jidesoft.grid.ColorCellEditor;

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
    private static boolean isApplied;

    public synchronized static void applyLicense() {
        if ( useJTimeSeriesJideLicense && ! isApplied ) {
            com.jidesoft.utils.Lm.verifyLicense("Nick Ebbutt", "jtimeseries", "nP8LYFIkFfoYrF3WhUkPzFJPe9bigQ3");
            isApplied = true;
        }
    }

    public synchronized static void setUseJTimeSeriesJideLicense(boolean useJTimeSeriesJideLicense) {
        JideInitialization.useJTimeSeriesJideLicense = useJTimeSeriesJideLicense;
    }

    public void setupJide() {
        setupCellEditorManager();
    }

    private void setupCellEditorManager() {
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
    }
}
