package com.od.jtimeseries.ui.timeseries;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 26-Feb-2010
 * Time: 22:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ColorRotator {

    private int lastColor;

    private Color[] seriesColors = new Color[] {
            Color.BLUE.darker(),
            Color.GREEN.darker(),
            Color.RED.darker(),
            Color.BLACK,
            Color.GRAY,
            Color.CYAN.darker(),
            Color.DARK_GRAY,
            Color.MAGENTA,
            Color.ORANGE,
            Color.YELLOW.darker(),
            Color.PINK
    };

    public Color getNextColor() {
        return seriesColors[lastColor++ % seriesColors.length];
    }
}
