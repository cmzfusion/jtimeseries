package com.od.jtimeseries.ui.timeserious.frame;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 17/02/12
 * Time: 19:45
 */
public class BaseMenu extends JMenu {

    public BaseMenu(String s) {
        super(s);
        doInitialize();
    }

    private void doInitialize() {
        setOpaque(false);
    }
}
