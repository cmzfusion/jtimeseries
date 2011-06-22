/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
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
package com.od.jtimeseries.ui.util;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 30/04/11
 * Time: 09:07
 */
public class CascadeLocationCalculator {

    private int xOffset;
    private int yOffset;

    public CascadeLocationCalculator(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    /**
     * @return a new location based on the current location and the offsets.
     * If the top left corner would fall outside the visible rectangle, start again at the top left
     * @param currentLocation last location used
     * @param visibleRect visible area available for cascade
     * @param width, width of new rectangle
     * @param height, height of new rectangle
     */
    public Rectangle getNextLocation(Rectangle currentLocation, Rectangle visibleRect, int width, int height) {
          int newXLocation = currentLocation.x + xOffset;
          newXLocation = newXLocation > visibleRect.x + visibleRect.getWidth() ? visibleRect.x : newXLocation;
          int newYLocation = currentLocation.y + yOffset;
          newYLocation = newYLocation >  visibleRect.y + visibleRect.getHeight() ? visibleRect.getBounds().y : newYLocation;
          return new Rectangle(newXLocation, newYLocation, width, height);
      }

}
