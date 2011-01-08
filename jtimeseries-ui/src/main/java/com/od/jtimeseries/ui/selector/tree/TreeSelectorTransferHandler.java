package com.od.jtimeseries.ui.selector.tree;

import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.selector.shared.SeriesTransferable;

import javax.swing.*;
import java.awt.datatransfer.Transferable;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: Nick
* Date: 08/01/11
* Time: 10:05
* To change this template use File | Settings | File Templates.
*/
public class TreeSelectorTransferHandler extends TransferHandler {

    private IdentifiableListActionModel selectionsModel;

    public TreeSelectorTransferHandler(IdentifiableListActionModel selectionsModel) {
        this.selectionsModel = selectionsModel;
    }

    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    public Transferable createTransferable(JComponent c) {
        return new SeriesTransferable(selectionsModel.getSelected());
    }

    public void exportDone(JComponent c, Transferable t, int action) {
//                if (action == MOVE) {
//                    c.removeSelection();
//                }
    }

    public boolean canImport(TransferSupport supp) {
        // Check for String flavor
        return supp.isDataFlavorSupported(SeriesTransferable.LIST_OF_IDENTIFIABLE_FLAVOR);
    }

    public boolean importData(TransferSupport supp) {
        if (!canImport(supp)) {
            return false;
        }

        // Fetch the Transferable and its data
        Transferable t = supp.getTransferable();
        List data = null;
        try {
            data = (List)t.getTransferData(SeriesTransferable.LIST_OF_IDENTIFIABLE_FLAVOR);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Object l : data) {
            System.out.println("imported " + l);
        }
        return true;
    }


}
