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

    /**
     * @return String valid name, or null if User cancelled request for new name
     */
    public String getNameFromUser(Component parent, String text, String title, String defaultName) {
        Object userInput = JOptionPane.showInputDialog(parent, text, title, JOptionPane.QUESTION_MESSAGE, null, null, defaultName);
        String result = null;
        if ( userInput != null) {
            result = userInput.toString();
            result = result.trim();
            result = result.length() == 0 ? defaultName : result;
            result = checkName(result);
        }
        return result;
    }

    /**
     * Check the suggested name is valid, and not a duplicate of an existing identifiable within this context
     * If required, prompt the user for an alternative name
     * @return String valid name, or null if User cancelled request for new name
     */
    public String checkName(String name) {
        String nameProblem = IdentifiablePathUtils.checkId(name);
        if ( nameProblem != null) {
            name = getNameFromUser(desktopPane, nameProblem + ", please correct the name", "Invalid Name", name);
        } else if ( context.contains(name) ) {
            name = getNameFromUser(desktopPane, "Duplicate name, please choose another", "Duplicate Name", name + "_copy");
        }
        return name;
    }

}
