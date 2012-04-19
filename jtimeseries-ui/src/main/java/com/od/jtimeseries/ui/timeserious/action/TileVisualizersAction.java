package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.timeserious.frame.TimeSeriousDesktopPane;
import com.od.jtimeseries.ui.timeserious.frame.VisualizerInternalFrame;
import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 22/06/11
 * Time: 12:54
 *
 * Tile visualizers, and hide the series selector panels amd controls
 *
 * Subsequent presses toggle between 1 column to MAX_COLS columns
 */
public class TileVisualizersAction extends AbstractArrangeInternalFrameAction {

    private TimeSeriousDesktopPane desktopPane;
    private int lastCols = -1;
    private final int MAX_COLS = 8;  //never exceed this number of cols even if there are more frames

    public TileVisualizersAction(TimeSeriousDesktopPane desktopPane) {
        super("Tile Visualizers", ImageUtils.TILE_VISUALIZERS_ICON_16x16);
        super.putValue(SHORT_DESCRIPTION, "Tile the visualizer windows in this desktop");
        this.desktopPane = desktopPane;
    }

    public void actionPerformed(ActionEvent e) {
        tileInternalFrames(desktopPane);
    }

    private void tileInternalFrames(JDesktopPane desk) {
           // How many frames do we have?
        JInternalFrame[] allframes = desk.getAllFrames();
        int count = allframes.length;
        if (count == 0) return;

        // Determine the necessary grid size
        int cols = getNextColumnCount(count);
        int rows = (int)Math.ceil(count / (float)cols);

        // Define some initial values for size & location.
        Dimension size = desk.getSize();

        int w = size.width / cols;
        int h = size.height / rows;
        int x = 0;
        int y = 0;

        // Iterate over the frames, deiconifying any iconified frames and then
        // relocating & resizing each.
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols && ((i * cols) + j < count); j++) {
                JInternalFrame f = allframes[(i * cols) + j];
                deiconify(f);
                desk.getDesktopManager().resizeFrame(f, x, y, w, h);
                hideSelector(f);
                hideControls(f);
                x += w;
            }
            y += h; // start the next row
            x = 0;
        }
    }

    private int getNextColumnCount(int frameCount) {
        int result;
        if ( lastCols == -1) {
            result = getStartingColumnCount(frameCount);
        } else {
            result = lastCols + 1;
            int max = Math.min(frameCount, MAX_COLS);
            lastCols = ++lastCols % max;
        }
        return result;
    }

    private int getStartingColumnCount(int frameCount) {
        //try to find a sensible starting configuration which favours width over height
        //a square grid would be sqrt frameCount, desired scaling is 1.5/1 so /1.5 first
        float f = frameCount / 1.5f;
        double s = Math.sqrt(f);
        lastCols = (int) Math.ceil(s);
        return lastCols;
    }

    private void hideControls(JInternalFrame f) {
        if ( f instanceof VisualizerInternalFrame) {
            ((VisualizerInternalFrame)f).setChartControlsVisible(false);
        }
    }

    private void hideSelector(JInternalFrame f) {
        if ( f instanceof VisualizerInternalFrame) {
            ((VisualizerInternalFrame)f).setSelectorHidden();
        }
    }
}
