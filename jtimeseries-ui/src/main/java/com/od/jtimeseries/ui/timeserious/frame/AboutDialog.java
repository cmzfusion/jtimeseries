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
package com.od.jtimeseries.ui.timeserious.frame;

import org.jfree.ui.about.AboutPanel;
import org.jfree.ui.about.SystemPropertiesPanel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * A dialog that displays information about the application.
 */
public class AboutDialog extends JDialog {

    /** The preferred size for the frame. */
    public static final Dimension PREFERRED_SIZE = new Dimension(800, 435);

    /** The default border for the panels in the tabbed pane. */
    public static final Border STANDARD_BORDER = BorderFactory.createEmptyBorder(5, 5, 5, 5);

    /** The application name. */
    private String application;

    /** The application version. */
    private String version;

    /** The copyright string. */
    private String copyright;

    /** Other info about the application. */
    private String info;

    /** The project logo. */
    private Image logo;

    public AboutDialog(Frame owner, String application, String version, String info, String copyright, Image logo) {
        super(owner);
        this.application = application;
        this.version = version;
        this.copyright = copyright;
        this.info = info;
        this.logo = logo;

        setLayout(new BorderLayout());
        JTabbedPane jTabbedPane = createTabs();
        add(jTabbedPane, BorderLayout.CENTER);
    }

    /**
     * Returns the preferred size for the about frame.
     *
     * @return the preferred size.
     */
    public Dimension getPreferredSize() {
        return PREFERRED_SIZE;
    }

    /**
     * Creates a tabbed pane containing an about panel and a system properties panel.
     *
     * @return a tabbed pane.
     */
    private JTabbedPane createTabs() {
        final JTabbedPane tabs = new JTabbedPane();
        final JPanel aboutPanel = createAboutPanel();
        aboutPanel.setBorder(AboutDialog.STANDARD_BORDER);
        tabs.add("About", aboutPanel);

        final JPanel systemPanel = new SystemPropertiesPanel();
        systemPanel.setBorder(AboutDialog.STANDARD_BORDER);
        tabs.add("System", systemPanel);
        return tabs;
    }

    private JPanel createAboutPanel() {
        return new AboutPanel(this.application, this.version, this.copyright, this.info,this.logo);
    }
}
