package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.timeserious.TimeSeriousMainFrame;
import com.od.jtimeseries.ui.timeserious.config.ConfigUtils;
import com.od.jtimeseries.ui.timeserious.config.SavedConfig;
import com.od.jtimeseries.ui.util.JideInitialization;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26-Mar-2010
 * Time: 14:15:20
 *
 * Standalone UI for time series exploration
 */
public class TimeSerious {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                try {
                    UIManager.setLookAndFeel(WindowsLookAndFeel.class.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                JideInitialization.setupJide();
                JideInitialization.setupJideLookAndFeel();

//                ConfigUtils configUtils = new ConfigUtils();
//                SavedConfig savedConfig = configUtils.getSelectedConfig();
//

                TimeSeriousMainFrame t = new TimeSeriousMainFrame();
                t.setSize(new Dimension(1024,768));
                t.setVisible(true);
            }
        });
    }



}
