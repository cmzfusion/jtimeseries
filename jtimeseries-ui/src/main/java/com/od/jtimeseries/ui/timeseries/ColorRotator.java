/**
 * Copyright (C) 2009 (nick @ objectdefinitions.com)
 *
 * This file is part of JTimeseries.
 *
 * JTimeseries is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimeseries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JTimeseries.  If not, see <http://www.gnu.org/licenses/>.
 */
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
