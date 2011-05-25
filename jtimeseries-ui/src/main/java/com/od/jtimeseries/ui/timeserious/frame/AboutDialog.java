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
