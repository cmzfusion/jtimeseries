package com.od.jtimeseries.ui.util;

import com.jidesoft.wizard.WizardDialog;
import com.od.swing.progress.ProgressLayeredPane;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 15-Dec-2010
 * Time: 21:20:24
 * To change this template use File | Settings | File Templates.
 */
public class ProgressIndicatorWizard extends WizardDialog {
    private boolean progressPaneAdded;

    public ProgressIndicatorWizard(Frame owner, String title) throws HeadlessException {
        super(owner, title);
    }

    public void addProgressPane(float alphaTransparency, int animationIconSize, int fontSize) {
        if ( ! progressPaneAdded ) {
            Component c = ProgressIndicatorWizard.this.getContentPane().getComponent(0);
            ProgressIndicatorWizard.this.getContentPane().remove(c);
            ProgressLayeredPane progressPane = new ProgressLayeredPane(c, alphaTransparency, animationIconSize, fontSize);
            ProgressIndicatorWizard.this.getContentPane().add(progressPane);
            progressPaneAdded = true;
        }
    }
}
