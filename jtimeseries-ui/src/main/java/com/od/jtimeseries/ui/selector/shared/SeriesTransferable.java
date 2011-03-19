package com.od.jtimeseries.ui.selector.shared;

import com.od.jtimeseries.ui.timeserious.VisualizerContext;
import com.od.jtimeseries.ui.timeserious.VisualizerNode;
import com.od.jtimeseries.ui.visualizer.VisualizerConfiguration;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.omg.PortableInterceptor.ObjectReferenceFactory;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
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

    private IdentifiableListActionModel selectionsModel;
    private List<File> files;

    public SeriesTransferable(IdentifiableListActionModel selectionsModel) {
        this.selectionsModel = selectionsModel;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { LIST_OF_IDENTIFIABLE_FLAVOR, DataFlavor.javaFileListFlavor };
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        boolean result = false;
        if (flavor.equals(LIST_OF_IDENTIFIABLE_FLAVOR)) {
            result = true;
        } else if ( flavor.equals(DataFlavor.javaFileListFlavor )) {
            result = selectionsModel.isSelectionLimitedToType(VisualizerContext.class);
        }
        return result;
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        Object result = null;
        if ( flavor == LIST_OF_IDENTIFIABLE_FLAVOR) {
            result = selectionsModel.getSelected();
        } else if ( flavor == DataFlavor.javaFileListFlavor ) {
            System.out.println("Creating file list");
            List<VisualizerNode> visualizerContexts = selectionsModel.getSelected(VisualizerNode.class);
            XStream x = new XStream(new DomDriver());
            files = new LinkedList<File>();
            for (VisualizerNode n : visualizerContexts) {
                VisualizerConfiguration c = n.getVisualizerConfiguration();

                File tmpDir = new File(System.getProperty("java.io.tmpdir"));
                File tmpFile = new File(tmpDir, "visualizer_" + c.getChartsTitle() + ".xml");
                PrintWriter p = new PrintWriter(tmpFile);
                String xml = x.toXML(c);
                p.write(xml);
                if ( p.checkError() ) {
                    throw new IOException("Failed to write file for data transfer");
                }
                p.close();
                files.add(tmpFile);
            }
            result = files;
        }
        return result;
    }

    public void cleanUpAfterExport() {
        System.out.println("Deleting file list");
        if ( files != null ) {
            for (File f : files) {
                f.delete();
            }
        }
    }
}
