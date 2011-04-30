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
