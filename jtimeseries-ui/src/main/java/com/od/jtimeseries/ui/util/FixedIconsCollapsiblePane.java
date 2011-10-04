package com.od.jtimeseries.ui.util;

import com.jidesoft.pane.CollapsiblePane;

import javax.swing.*;

/**
 *
 */
public class FixedIconsCollapsiblePane extends CollapsiblePane {

    //the up down icons are the wrong way around by default
    //(or are used the wrong way around)
    static {
        UIDefaults d = UIManager.getLookAndFeelDefaults();
        Icon down = d.getIcon("CollapsiblePane.downIcon");
        Icon up = d.getIcon("CollapsiblePane.upIcon");
        d.put("CollapsiblePane.downIcon", up);
        d.put("CollapsiblePane.upIcon", down);
    }

    public FixedIconsCollapsiblePane(String s) {
        super(s);
    }
}
