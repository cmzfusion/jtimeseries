package com.od.jtimeseries.ui.timeserious;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 01/07/11
 * Time: 09:08
 */
public class LookAndFeelManager {

    public void installLookAndFeel() {

        boolean success = false;

        if  (System.getProperty("os.name").startsWith("Windows ") &&
     System.getProperty("os.version").compareTo("5.1") >= 0){
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                success = true;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        //size of new server dialog is incorrect with Nimbus
        //        if ( ! success) {
        //             try {
        //                UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        //                success = true;
        //            } catch (Exception e1) {
        //                e1.printStackTrace();
        //            }
        //        }

        if ( ! success) {
             try {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                success = true;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}
