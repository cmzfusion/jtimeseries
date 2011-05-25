package com.od.jtimeseries.ui.timeserious.frame;

import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 25/05/11
* Time: 19:15
*/
class ShowAboutDialogAction extends AbstractAction {

    private JFrame ownerFrame;{ putValue(NAME, "About"); }

    public ShowAboutDialogAction(JFrame ownerFrame) {
        this.ownerFrame = ownerFrame;
    }

    public void actionPerformed(ActionEvent e) {
        AboutDialog d = new AboutDialog(
            ownerFrame,
            "TimeSerious",
            "1.0",
            "JTimeseries Project htp://www.jtimeseries.com",
            "(c) Object Definitions Ltd. LGPL",
            ImageUtils.SPLASH_SCREEN.getImage()
        );
        d.pack();
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }
}
