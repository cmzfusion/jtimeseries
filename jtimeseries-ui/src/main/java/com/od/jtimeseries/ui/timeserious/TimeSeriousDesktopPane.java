package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.ui.config.VisualizerConfiguration;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.event.TimeSeriousBusListener;
import com.od.jtimeseries.ui.event.TimeSeriousBusListenerAdapter;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.timeserious.action.TimeSeriousVisualizerActionFactory;
import com.od.jtimeseries.ui.config.ConfigAware;
import com.od.jtimeseries.ui.config.TimeSeriousConfig;
import com.od.jtimeseries.ui.visualizer.TimeSeriesVisualizer;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeEvent;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeListenerAdapter;
import com.od.swing.eventbus.UIEventBus;

import javax.swing.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26-Mar-2010
 * Time: 17:49:07
 */
public class TimeSeriousDesktopPane extends JDesktopPane implements ConfigAware {

    private JFrame parentFrame;
    private SeriesSelectionPanel mainSelectionPanel;
    private DesktopContext desktopContext;
    private DesktopVisualizerFactory desktopVisualizerFactory;

    public TimeSeriousDesktopPane(JFrame parentFrame, TimeSeriesServerDictionary timeSeriesServerDictionary, DisplayNameCalculator displayNameCalculator, SeriesSelectionPanel mainSelectionPanel, DesktopContext desktopContext) {
        this.parentFrame = parentFrame;
        this.mainSelectionPanel = mainSelectionPanel;
        this.desktopContext = desktopContext;
        addUiBusEventListener();
        addFrameListener();
        addDesktopListener();
        setTransferHandler(new DesktopPaneTransferHandler());
        desktopVisualizerFactory = new DesktopVisualizerFactory(this, desktopContext, timeSeriesServerDictionary, displayNameCalculator);
    }

    private void addDesktopListener() {
        desktopContext.addTreeListener(new IdentifiableTreeListenerAdapter() {
            public void descendantChanged(IdentifiableTreeEvent contextTreeEvent) {
                for ( Identifiable c : contextTreeEvent.getNodes()) {
                    if ( c instanceof VisualizerNode ) {
                        VisualizerNode n = (VisualizerNode)c;
                        if ( n.isShown()) {
                            showVisualizerForNode(n);
                        }
                    }
                }
            }
        });
    }

    private void addFrameListener() {
        parentFrame.addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                desktopContext.setFrameExtendedState(parentFrame.getExtendedState());
            }
        });

        parentFrame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                desktopContext.setFrameLocation(parentFrame.getBounds());
            }

            public void componentMoved(ComponentEvent e) {
                desktopContext.setFrameLocation(parentFrame.getBounds());
            }
        });
    }

    public VisualizerInternalFrame createNewVisualizer(String title) {
        title = desktopVisualizerFactory.checkVisualizerName(title);
        TimeSeriesVisualizer v = desktopVisualizerFactory.createVisualizer(title);
        VisualizerConfiguration c = TimeSeriesVisualizer.createVisualizerConfiguration(v);
        VisualizerNode node = createAndAddVisualizerNode(c, v);
        return configureAndShowVisualizerFrame(null, v, node);
    }

    public void importVisualizer(VisualizerConfiguration c) {
        TimeSeriesVisualizer visualizer = desktopVisualizerFactory.createVisualizer(c.getChartsTitle());
        VisualizerNode node = createAndAddVisualizerNode(c, visualizer);
        configureAndShowVisualizerFrame(c, visualizer, node);
    }

    private VisualizerNode createAndAddVisualizerNode(VisualizerConfiguration c, TimeSeriesVisualizer visualizer) {
        VisualizerNode n = new VisualizerNode(visualizer.getChartsTitle(), c);
        desktopContext.addChild(n);
        return n;
    }

    public void prepareConfigForSave(TimeSeriousConfig config) {
    }

    public void restoreConfig(TimeSeriousConfig config) {
        List<VisualizerNode> nodes = desktopContext.findAll(VisualizerNode.class).getAllMatches();
        sortNodesByZPosition(nodes);
        for ( VisualizerNode n : nodes) {
            if ( ! n.isHidden() ) {
                showVisualizerForNode(n);
            }
        }
    }

    private void showVisualizerForNode(VisualizerNode n) {
        VisualizerConfiguration c = n.getVisualizerConfiguration();
        TimeSeriesVisualizer v = desktopVisualizerFactory.createVisualizer(c.getChartsTitle());
        configureAndShowVisualizerFrame(c, v, n);
    }

    public void sortNodesByZPosition(List<VisualizerNode> nodes) {
        //sort by z index, so we display them in the right order
        Collections.sort(nodes, new Comparator<VisualizerNode>() {
            public int compare(VisualizerNode o1, VisualizerNode o2) {
                return ((Integer)o2.getZPosition()).compareTo(o1.getZPosition());
            }
        });
    }

    public List<ConfigAware> getConfigAwareChildren() {
        return Collections.emptyList();
    }

    private void addUiBusEventListener() {
        UIEventBus.getInstance().addEventListener(
            TimeSeriousBusListener.class,
            new TimeSeriousBusListenerAdapter() {

                public void visualizerImported(VisualizerConfiguration visualizerConfiguration) {
                    String title = visualizerConfiguration.getChartsTitle();
                    title = desktopVisualizerFactory.checkVisualizerName(title);
                    visualizerConfiguration.setChartsTitle(title);
                    importVisualizer(visualizerConfiguration);
                }
            }
        );
    }

    private VisualizerInternalFrame configureAndShowVisualizerFrame(VisualizerConfiguration c, TimeSeriesVisualizer visualizer, VisualizerNode visualizerNode) {
        visualizer.setSelectorActionFactory(new TimeSeriousVisualizerActionFactory(
            visualizer.getSelectionActionModel(),
            mainSelectionPanel
        ));

        VisualizerInternalFrame visualizerFrame = new VisualizerInternalFrame(visualizer, this, visualizerNode);
        if ( c != null) {
            TimeSeriesVisualizer.setVisualizerConfiguration(visualizer, c);
            if ( c.getFrameBounds() != null) {
                visualizerFrame.setBounds(c.getFrameBounds());
            }
        }
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



}
