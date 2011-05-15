package com.od.jtimeseries.ui.selector.shared;

import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import javax.swing.*;
import java.awt.datatransfer.Transferable;
import java.lang.ref.WeakReference;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: Nick
* Date: 08/01/11
* Time: 10:05
*
*/
public class NoImportsSelectorTransferHandler extends TransferHandler {

    private static final LogMethods logMethods = LogUtils.getLogMethods(NoImportsSelectorTransferHandler.class);

    private AbstractUIRootContext rootContext;
    private IdentifiableListActionModel selectionsModel;
    public WeakReference<LocalSelectionsTransferData> d;

    public NoImportsSelectorTransferHandler(AbstractUIRootContext rootContext, IdentifiableListActionModel selectionsModel) {
        this.rootContext = rootContext;
        this.selectionsModel = selectionsModel;
    }

    public int getSourceActions(JComponent c) {
        return rootContext.getSourceActions(selectionsModel);
    }

    public Transferable createTransferable(JComponent c) {
        IdentifiableListActionModel snapshotSelections = new IdentifiableListActionModel();
        snapshotSelections.setSelected(selectionsModel.getSelected());
        LocalSelectionsTransferData transferData = new LocalSelectionsTransferData(snapshotSelections, new LocalSelectionsTransferData.TransferListener() {
            public void transferComplete(LocalSelectionsTransferData d, int actionType) {
                rootContext.doExport(d.getSelections(), actionType);
            }
        });
        d = new WeakReference<LocalSelectionsTransferData>(transferData);
        return new IdentifiableTransferable(transferData);
    }

    public void exportDone(JComponent c, Transferable t, int action) {
        //For cut copy actions, as opposed to drag drop, this method is called when the
        //cut takes place (instead of being deferred until paste takes place which is the
        //expected behaviour in windows explorer trees, for example)
        //The solution is to pass a reference to the local handler in the
        //LocalSelectionsTransferData, so that we can manually call back to perform
        //the cut/export in the source handler after the paste.
        //But the destination's transfer handler has no way to know whether the source
        //action was cut or paste - so we also set the action on the LocalSelectionsTransferData here
        LocalSelectionsTransferData transferData = d.get();
        if ( transferData != null) {
            transferData.setAction(action);
        }
    }

    public boolean canImport(TransferSupport supp) {
        return false;
    }

    public boolean importData(TransferSupport supp) {
        return false;
    }

    protected void doImport(List<Identifiable> data, Identifiable target) {
    }

    protected AbstractUIRootContext getRootContext() {
        return rootContext;
    }

    public IdentifiableListActionModel getSelectionsModel() {
        return selectionsModel;
    }
}
