package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 12-Dec-2010
 * Time: 19:14:32
 * To change this template use File | Settings | File Templates.
 */
public class NewServerAction extends AbstractAction {

    public NewServerAction() {
        super("New Server", ImageUtils.ADD_SERVER_ICON_16x16);
        super.putValue(SHORT_DESCRIPTION, "Add a new server to connect and download series data");
    }

    public void actionPerformed(ActionEvent e) {
    }
}
