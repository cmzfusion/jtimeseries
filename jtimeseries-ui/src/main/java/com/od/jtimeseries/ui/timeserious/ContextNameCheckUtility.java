package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.util.identifiable.Identifiable;
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

    /**
     * @return String valid name, or null if User cancelled request for new name
     */
    public static String getNameFromUser(Component parent, Identifiable targetContext, String text, String title, String defaultName) {
        Object userInput = JOptionPane.showInputDialog(SwingUtilities.windowForComponent(parent), text, title, JOptionPane.QUESTION_MESSAGE, null, null, defaultName);
        String result = null;
        if ( userInput != null) {
            result = userInput.toString();
            result = result.trim();
            result = result.length() == 0 ? defaultName : result;
            result = checkName(parent, targetContext, result);
        }
        return result;
    }

    /**
     * Check the suggested name is valid, and not a duplicate of an existing identifiable within this targetContext
     * If required, prompt the user for an alternative name
     * @return String valid name, or null if User cancelled request for new name
     */
    public static String checkName(Component parentComponent, Identifiable targetContext, String name) {
        String nameProblem = IdentifiablePathUtils.checkId(name);
        if ( nameProblem != null) {
            name = getNameFromUser(parentComponent, targetContext, nameProblem + ", please correct the name", "Invalid Name", name);
        } else if ( targetContext.contains(name) ) {
            name = getNameFromUser(parentComponent, targetContext, "Duplicate name, please choose another", "Duplicate Name", name + "_copy");
        }
        return name;
    }

}
