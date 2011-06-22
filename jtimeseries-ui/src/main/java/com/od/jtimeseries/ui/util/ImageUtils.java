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

import com.od.swing.util.ImageIconCache;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 07-Jan-2009
 * Time: 10:31:53
 */
public class ImageUtils {

    public static final String PROGRESS_SERVER_IMAGE = "/images/server_client2_16x16.png";

    public static ImageIcon CONTEXT_ICON_16x16 = getImageIcon("/images/folder_cubes16x16.gif");
    public static ImageIcon SERIES_ICON_16x16 = getImageIcon("/images/document_chart16x16.png");
    public static ImageIcon FRAME_ICON_16x16 = getImageIcon("/images/chart16x16.png");
    public static ImageIcon ADD_ICON_16x16 = getImageIcon("/images/add16x16.png");
    public static ImageIcon REMOVE_ICON_16x16 = getImageIcon("/images/selection_delete16x16.png");
    public static ImageIcon CONNECT_ICON_16x16 = getImageIcon("/images/connect16x16.png");
    public static ImageIcon TIMESERIES_SERVER_OFFLINE_ICON_16x16 = getImageIcon("/images/server_client2_offline_16x16.png");
    public static ImageIcon TIMESERIES_SERVER_REFRESH_ICON_16x16 = getImageIcon("/images/server_client2_refresh_16x16.png");
    public static ImageIcon TIMESERIES_SERVER_REMOVE_ICON_16x16 = getImageIcon("/images/server_client2_remove_16x16.png");
    public static ImageIcon TIMESERIES_SERVER_RENAME_ICON_16x16 = getImageIcon("/images/server_client2_rename_16x16.png");
    public static ImageIcon TIMESERIES_SERVER_ICON_16x16 = getImageIcon("/images/server_client2_16x16.png");
    public static ImageIcon TIMESERIES_SERVER_ICON_24x24 = getImageIcon("/images/server_client2_24x24.png");
    public static ImageIcon ADD_SERVER_ICON_16x16 = getImageIcon("/images/server_client2_add_16x16.png");
    public static ImageIcon DOWNLOAD_16x16 = getImageIcon("/images/download16x16.png");
    public static ImageIcon CANCEL_16x16 = getImageIcon("/images/cancel16x16.png");
    public static ImageIcon DISPLAY_NAME_16x16 = getImageIcon("/images/displayName16x16.png");
    public static ImageIcon OK_16x16 = getImageIcon("/images/ok16x16.png");
    public static ImageIcon TABLE_COLUMN_ADD_16x16 = getImageIcon("/images/table_column_add_16x16.png");
    public static ImageIcon FIND_IN_MAIN_SELECTOR_16x16 = getImageIcon("/images/find_in_main_selector_16x16.png");
    public static ImageIcon DESKTOP_NEW_16x16 = getImageIcon("/images/window_new_16x16.png");
    public static ImageIcon DESKTOP_DELETE_16x16 = getImageIcon("/images/window_delete_16x16.png");
    public static ImageIcon DESKTOP_SHOW_16x16 = getImageIcon("/images/window_show_16x16.png");
    public static ImageIcon DESKTOP_16x16 = getImageIcon("/images/window_16x16.png");
    public static ImageIcon VISUALIZER_16x16 = getImageIcon("/images/visualizer_16x16.png");
    public static ImageIcon VISUALIZER_NEW_16x16 = getImageIcon("/images/visualizer_new_16x16.png");
    public static ImageIcon VISUALIZER_DELETE_16x16 = getImageIcon("/images/visualizer_delete_16x16.png");
    public static ImageIcon VISUALIZER_SHOW_16x16 = getImageIcon("/images/visualizer_show_16x16.png");
    public static ImageIcon VISUALIZER_ADD_TO_16x16 = getImageIcon("/images/visualizer_add_to_16x16.png");
    public static ImageIcon SETTINGS_16x16 = getImageIcon("/images/settings_16x16.png");
    public static ImageIcon SPLASH_SCREEN = ImageIconCache.getImageIcon("/images/seizmograph.jpg", 450, 360);



    public static ImageIcon getImageIcon(String name) {
        ImageIcon imageIcon = null;
        try {
            imageIcon = new ImageIcon(ImageUtils.class.getResource(name));
        }
        catch (Exception e) {
            System.err.println("Unable to load Icon: " + name);
        }
        return imageIcon;
    }
}
