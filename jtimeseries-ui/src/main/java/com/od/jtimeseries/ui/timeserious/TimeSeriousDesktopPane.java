package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.ui.config.VisualizerConfiguration;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.timeserious.action.TimeSeriousVisualizerActionFactory;
import com.od.jtimeseries.ui.visualizer.TimeSeriesVisualizer;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeEvent;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeListener;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeListenerAdapter;
import com.od.swing.util.AwtSafeListener;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26-Mar-2010
 * Time: 17:49:07
 */
public class TimeSeriousDesktopPane extends JDesktopPane {

    private JFrame parentFrame;
    private TimeSeriesServerDictionary timeSeriesServerDictionary;
    private DisplayNameCalculator displayNameCalculator;
    private SeriesSelectionPanel mainSelectionPanel;
    private DesktopContext desktopContext;

    public TimeSeriousDesktopPane(JFrame parentFrame, TimeSeriesServerDictionary timeSeriesServerDictionary, DisplayNameCalculator displayNameCalculator, SeriesSelectionPanel mainSelectionPanel, DesktopContext desktopContext, TimeSeriousRootContext rootContext) {
        this.parentFrame = parentFrame;
        this.timeSeriesServerDictionary = timeSeriesServerDictionary;
        this.displayNameCalculator = displayNameCalculator;
        this.mainSelectionPanel = mainSelectionPanel;
        this.desktopContext = desktopContext;
        addListeners();
        setTransferHandler(new DesktopPaneTransferHandler(rootContext, desktopContext, this,parentFrame));
    }

    private void addListeners() {
        desktopContext.addTreeListener(
            AwtSafeListener.getAwtSafeListener(new ShowVisualizerTreeListener(),
            IdentifiableTreeListener.class)
        );

        parentFrame.addWindowListener(new InternalFrameDeactivatingWindowListener());
    }

    private void deactivateAllFrames() {
        for (JInternalFrame f : this.getAllFrames()) {
            try {
                f.setSelected(false);
            } catch (PropertyVetoException e1) {
                e1.printStackTrace();
            }
        }
    }

    private TimeSeriesVisualizer createVisualizer(String title) {
        return new TimeSeriesVisualizer(
            title,
            timeSeriesServerDictionary,
            displayNameCalculator
        );
    }

    public void setConfiguration(DesktopContext desktopContext) {
        //visualizer nodes should already have been created within the desktopContext
        //find those that should be shown, and show them
        List<VisualizerContext> nodes = desktopContext.findAll(VisualizerContext.class).getAllMatches();
        sortNodesByZPosition(nodes);
        for ( VisualizerContext n : nodes) {
            if ( n.isShown() ) {
                showVisualizerForNode(n);
            }
        }
    }

    private void showVisualizerForNode(VisualizerContext n) {
        VisualizerConfiguration c = n.getConfiguration();
        //here we are showing the visualizer for an existing visualizerNode rather
        //than creating a new visualizer, so no need to check the name
        TimeSeriesVisualizer v = createVisualizer(c.getTitle());
        configureAndShowVisualizerFrame(c, v, n);
    }

    private void sortNodesByZPosition(List<VisualizerContext> nodes) {
        //sort by z index, so we display them in the right order
        Collections.sort(nodes, new Comparator<VisualizerContext>() {
            public int compare(VisualizerContext o1, VisualizerContext o2) {
                return ((Integer)o2.getZPosition()).compareTo(o1.getZPosition());
            }
        });
    }

    private VisualizerInternalFrame configureAndShowVisualizerFrame(VisualizerConfiguration c, TimeSeriesVisualizer visualizer, VisualizerContext visualizerNode) {
        visualizer.setSelectorActionFactory(new TimeSeriousVisualizerActionFactory(
            visualizer.getSelectionActionModel(),
            mainSelectionPanel
        ));

        VisualizerInternalFrame visualizerFrame = new VisualizerInternalFrame(visualizer, this, visualizerNode);
        visualizerFrame.setConfiguration(c);
        add(visualizerFrame);
        visualizerFrame.setVisible(true);
        if ( c != null) {
            try {
                visualizerFrame.setIcon(c.isIcon());
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            }
        }
        return visualizerFrame;
    }

    private class ShowVisualizerTreeListener extends IdentifiableTreeListenerAdapter {

        public void descendantChanged(IdentifiableTreeEvent contextTreeEvent) {
            for ( Identifiable c : contextTreeEvent.getNodes()) {
                if ( c instanceof VisualizerContext) {
                    VisualizerContext n = (VisualizerContext)c;
                    if ( n.isShown()) {
                        showVisualizerForNode(n);
                    }
                }
            }
        }

        public void descendantAdded(IdentifiableTreeEvent contextTreeEvent) {
            for ( Identifiable c : contextTreeEvent.getNodes()) {
                if ( c instanceof VisualizerContext) {
                    VisualizerContext n = (VisualizerContext)c;
                    if ( n.isShown()) {
                        showVisualizerForNode(n);
                    }
                }
            }
        }

        public void descendantRemoved(IdentifiableTreeEvent contextTreeEvent) {
            for ( Identifiable c : contextTreeEvent.getNodes()) {
                if ( c instanceof VisualizerContext) {
                    VisualizerContext n = (VisualizerContext)c;
                    n.setShown(false); //trigger VisualizerInternalFrame to dispose
                }
            }
        }
    }

    /**
     *  deactivating the activated internal frame when this desktop JFrame is deactivated
     *  will allow us to receive an activation event for the internal frame if the user clicks
     *  back to this desktop. otherwise we don't get an internal frame event -
     *  we need this event to update the global active visualizer action model
     */
    private class InternalFrameDeactivatingWindowListener extends WindowAdapter {
        public void windowDeactivated(WindowEvent e) {
             deactivateAllFrames();
        }
    }
}
