package com.od.jtimeseries.ui.timeserious;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 28/04/11
 * Time: 14:06
 * To change this template use File | Settings | File Templates.
 */
public interface PeerDesktop {

    int getExtendedState();

    Rectangle getBounds();

    ContextNameCheckUtility getNameCheckUtility();
}
