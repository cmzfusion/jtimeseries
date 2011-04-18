package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.util.identifiable.IdentifiablePathUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 30/03/11
 * Time: 06:13
 */
public class ContextNameCheckUtility {

    private Component desktopPane;
    private TimeSeriesContext context;

    public ContextNameCheckUtility(Component desktopPane, TimeSeriesContext context) {
        this.desktopPane = desktopPane;
        this.context = context;
    }

    public static String getNameFromUser(Component parent, String text, String title, String defaultName) {
        String name = JOptionPane.showInputDialog(parent, text, title, JOptionPane.QUESTION_MESSAGE, null, null, defaultName).toString();
        if ( name != null) {
            name = name.trim();
            name = name.length() == 0 ? "Visualizer" : name;
        }
        return name;
    }

    /**
     * Check the suggested name is valid, and not a duplicate of an existing identifiable within this context
     * If required, prompt the user for an alternative name
     */
    public String checkName(String name) {
        String nameProblem = IdentifiablePathUtils.checkId(name);
        if ( nameProblem != null) {
            name = getNameFromUser(desktopPane, nameProblem + ", please correct the name", "Invalid Name", name);
            name = checkName(name);
        } else if ( context.contains(name) ) {
            name = getNameFromUser(desktopPane, "Duplicate name, please choose another", "Duplicate Name", name + "_copy");
            name = checkName(name);
        }
        return name;
    }

}
