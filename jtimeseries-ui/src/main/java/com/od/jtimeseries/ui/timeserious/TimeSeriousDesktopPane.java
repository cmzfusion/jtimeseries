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
import com.od.jtimeseries.util.identifiable.IdentifiablePathUtils;
import com.od.swing.eventbus.UIEventBus;

import javax.swing.*;
import java.awt.*;
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

    private TimeSeriesServerDictionary timeSeriesServerDictionary;
    private DisplayNameCalculator displayNameCalculator;
    private SeriesSelectionPanel mainSelectionPanel;
    private DesktopContext desktopContext;

    public TimeSeriousDesktopPane(TimeSeriesServerDictionary timeSeriesServerDictionary, DisplayNameCalculator displayNameCalculator, SeriesSelectionPanel mainSelectionPanel, DesktopContext desktopContext) {
        this.timeSeriesServerDictionary = timeSeriesServerDictionary;
        this.displayNameCalculator = displayNameCalculator;
        this.mainSelectionPanel = mainSelectionPanel;
        this.desktopContext = desktopContext;
        addUiBusEventListener();
        setTransferHandler(new DesktopPaneTransferHandler());
    }

    public VisualizerInternalFrame createAndAddVisualizer(String title) {
        title = checkVisualizerName(title);
        TimeSeriesVisualizer v = createVisualizer(title);
        return configureAndShowVisualizerFrame(null, v);
    }

    public void createAndAddVisualizer(VisualizerConfiguration c) {
        TimeSeriesVisualizer visualizer = createVisualizer(c.getChartsTitle());
        configureAndShowVisualizerFrame(c, visualizer);
    }

    public void prepareConfigForSave(TimeSeriousConfig config) {
        config.setVisualizerConfigurations(getVisualizerConfigurations());
    }

    public void restoreConfig(TimeSeriousConfig config) {
        addVisualizers(config.getVisualizerConfigurations());
    }

    public List<ConfigAware> getConfigAwareChildren() {
        return Collections.emptyList();
    }

    public List<VisualizerConfiguration> getVisualizerConfigurations() {
        List<VisualizerConfiguration> l = new LinkedList<VisualizerConfiguration>();
        for ( JInternalFrame v : getVisualizerFramesByPosition()) {
            VisualizerInternalFrame vf = (VisualizerInternalFrame) v;
            VisualizerConfiguration c = TimeSeriesVisualizer.createVisualizerConfiguration(vf.getVisualizer());
            c.setIsIcon(v.isIcon());
            c.setFrameBounds(v.getBounds());
            l.add(c);
        }
        return l;
    }

    public void addVisualizers(List<VisualizerConfiguration> visualizerConfigurations) {
        for (VisualizerConfiguration c : visualizerConfigurations) {
            createAndAddVisualizer(c);
        }
    }

    private void addUiBusEventListener() {
        UIEventBus.getInstance().addEventListener(
            TimeSeriousBusListener.class,
            new TimeSeriousBusListenerAdapter() {

                public void visualizerImported(VisualizerConfiguration visualizerConfiguration) {
                    String title = visualizerConfiguration.getChartsTitle();
                    title = checkVisualizerName(title);
                    visualizerConfiguration.setChartsTitle(title);
                    createAndAddVisualizer(visualizerConfiguration);
                }

                public void visualizerShown(VisualizerConfiguration visualizerConfiguration) {
                    createAndAddVisualizer(visualizerConfiguration);
                }

                public void visualizerRemoved(VisualizerConfiguration c, VisualizerInternalFrame f) {
                    if ( f != null ) {
                        f.setVisible(false);
                        remove(f);
                    }
                }
            }
        );
    }

    private VisualizerInternalFrame configureAndShowVisualizerFrame(VisualizerConfiguration c, TimeSeriesVisualizer visualizer) {
        visualizer.setSelectorActionFactory(new TimeSeriousVisualizerActionFactory(
            visualizer.getSelectionActionModel(),
            mainSelectionPanel
        ));

        VisualizerInternalFrame visualizerFrame = new VisualizerInternalFrame(visualizer);
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

    //a list of frames by z position, so that when we load the config and add them they reappear with the
    //same z order
    private JInternalFrame[] getVisualizerFramesByPosition() {
        JInternalFrame[] jInternalFrames = getAllFrames();
        Arrays.sort(jInternalFrames, new Comparator<JInternalFrame>() {
                public int compare(JInternalFrame o1, JInternalFrame o2) {
                    return Integer.valueOf(getPosition(o2)).compareTo(getPosition(o1));
                }
            }
        );
        return jInternalFrames;
    }

    private TimeSeriesVisualizer createVisualizer(String title) {
        return new TimeSeriesVisualizer(
            title,
            timeSeriesServerDictionary,
            displayNameCalculator
        );
    }

    private String checkVisualizerName(String name) {
        String nameProblem = IdentifiablePathUtils.checkId(name);
        if ( nameProblem != null) {
            name = getVisualizerNameFromUser(this, nameProblem + ", please correct the name", "Invalid Name", name);
            name = checkVisualizerName(name);
        } else if ( desktopContext.contains(name) ) {
            name = getVisualizerNameFromUser(this, "Duplicate name, please choose another", "Duplicate Name", name + "_copy");
            name = checkVisualizerName(name);
        }
        return name;
    }


    public static String getVisualizerNameFromUser(Component parent, String text, String title,  String defaultName) {
        String name = JOptionPane.showInputDialog(parent, text, title, JOptionPane.QUESTION_MESSAGE, null, null, defaultName).toString();
        if ( name != null) {
            name = name.trim();
            name = name.length() == 0 ? "Visualizer" : name;
        }
        return name;
    }

}
