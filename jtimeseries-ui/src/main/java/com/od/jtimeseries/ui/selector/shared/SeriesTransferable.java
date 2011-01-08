package com.od.jtimeseries.ui.selector.shared;

import com.od.jtimeseries.util.identifiable.Identifiable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 08/01/11
 * Time: 09:41
 * To change this template use File | Settings | File Templates.
 */
public class SeriesTransferable implements Transferable {

    private static final String listType = DataFlavor.javaJVMLocalObjectMimeType +
                       ";class=java.util.List";
    public static DataFlavor LIST_OF_IDENTIFIABLE_FLAVOR;

    static {
        try {
            LIST_OF_IDENTIFIABLE_FLAVOR = new DataFlavor(listType);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private List<Identifiable> data;

    public SeriesTransferable(List<Identifiable> data) {
        this.data = data;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { LIST_OF_IDENTIFIABLE_FLAVOR };
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(LIST_OF_IDENTIFIABLE_FLAVOR);
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return data;
    }
}
