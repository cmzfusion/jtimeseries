package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.visualizer.TimeSeriesVisualizer;
import com.od.jtimeseries.util.identifiable.IdentifiablePathUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 30/03/11
 * Time: 06:13
 */
public class DesktopVisualizerFactory {

    private JDesktopPane desktopPane;
    private DesktopContext desktopContext;
    private TimeSeriesServerDictionary timeSeriesServerDictionary;
    private DisplayNameCalculator displayNameCalculator;

    public DesktopVisualizerFactory(JDesktopPane desktopPane, DesktopContext desktopContext, TimeSeriesServerDictionary timeSeriesServerDictionary, DisplayNameCalculator displayNameCalculator) {
        this.desktopPane = desktopPane;
        this.desktopContext = desktopContext;
        this.timeSeriesServerDictionary = timeSeriesServerDictionary;
        this.displayNameCalculator = displayNameCalculator;
    }

    public TimeSeriesVisualizer createVisualizer(String title) {
        return new TimeSeriesVisualizer(
            title,
            timeSeriesServerDictionary,
            displayNameCalculator
        );
    }

    public String checkVisualizerName(String name) {
        String nameProblem = IdentifiablePathUtils.checkId(name);
        if ( nameProblem != null) {
            name = DesktopVisualizerFactory.getVisualizerNameFromUser(desktopPane, nameProblem + ", please correct the name", "Invalid Name", name);
            name = checkVisualizerName(name);
        } else if ( desktopContext.contains(name) ) {
            name = DesktopVisualizerFactory.getVisualizerNameFromUser(desktopPane, "Duplicate name, please choose another", "Duplicate Name", name + "_copy");
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
