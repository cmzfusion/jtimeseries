package com.od.jtimeseries.ui.timeserious.frame;

import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.uicontext.AbstractUIRootContext;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.jtimeseries.ui.uicontext.ImportExportTransferHandler;
import com.od.jtimeseries.util.identifiable.Identifiable;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 21/03/11
 * Time: 17:08
 */
public class DesktopPaneTransferHandler extends ImportExportTransferHandler {

    private DesktopContext desktopContext;

    public DesktopPaneTransferHandler(AbstractUIRootContext rootContext, DesktopContext desktopContext, JDesktopPane desktopPane) {
        super(rootContext, new IdentifiableListActionModel());
        this.desktopContext = desktopContext;
    }

    protected Identifiable getTargetIdentifiableForDropOrPaste(TransferSupport supp) {
        return desktopContext;
    }
}
