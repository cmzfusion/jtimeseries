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
package com.od.jtimeseries.ui.download.panel;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.httpd.TimeSeriesIndexHandler;
import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.swing.action.ListSelectionActionModel;
import com.od.swing.action.ModelDrivenAction;
import com.od.swing.progress.ProgressIndicatorTaskListener;
import swingcommand.Task;
import swingcommand.TaskListenerAdapter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 13-Jan-2009
 * Time: 13:17:45
 */
public class SelectServerPanel extends AbstractDownloadWizardPanel {

    private static final LogMethods logMethods = LogUtils.getLogMethods(SelectServerPanel.class);

    private static final long HIDE_SERVERS_WITH_LAST_PING_OLDER_THAN_MILLIS = 1000 * 60 * 10;
    private JList knownServersList;
    private JTextField serverTextField = new JTextField();
    private ListSelectionActionModel<TimeSeriesServer> listSelectionActionModel = new ListSelectionActionModel<TimeSeriesServer>();
    private JButton downloadButton = new JButton(new DownloadAction());
    private LoadSeriesFromServerCommand loadSeriesFromServerCommand;

    public SelectServerPanel(WizardPanelListener panelListener, TimeSeriesServerDictionary serverDictionary, TimeSeriesContext destinationContext, DisplayNameCalculator displayNameCalculator) {
        super(panelListener);
        loadSeriesFromServerCommand = new LoadSeriesFromServerCommand(destinationContext, displayNameCalculator);
        loadSeriesFromServerCommand.addTaskListener(new TaskListenerAdapter<String>() {
            public void success(Task task) {
                getPanelListener().seriesLoaded();
            }
        });
        buildList(serverDictionary);
        doAddComponents();
    }

    private void doAddComponents() {
        Box titleBox = super.createTitlePanel("Select a Server");
        JScrollPane knownServersPane = createServersPane();
        JComponent urlTextFieldComponent = createUrlTextField();
        Box buttonBox = createButtonBox();

        Box b = Box.createVerticalBox();
        b.add(urlTextFieldComponent);
        b.add(buttonBox);

        setLayout(new BorderLayout());
        add(titleBox, BorderLayout.NORTH);
        add(knownServersPane, BorderLayout.CENTER);
        add(b, BorderLayout.SOUTH);
        addProgressTaskListener();
        addDoubleClickListener();
        addListSelectionListener();
        addTextFieldListener();
    }

    private void addTextFieldListener() {
        serverTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadSeriesFromTextFieldUrl();
            }
        });
    }

    private void addDoubleClickListener() {
        knownServersList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if ( e.getClickCount() == 2 && knownServersList.getSelectedIndex() != -1) {
                    downloadListSelections();
                }
            }
        });
    }

    private void addListSelectionListener() {
        knownServersList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if ( knownServersList.getSelectedIndex() != -1 ) {
                    listSelectionActionModel.setSelected((TimeSeriesServer)knownServersList.getSelectedValue());
                } else {
                    listSelectionActionModel.clearActionModelState();
                }
            }
        });
    }

    private JComponent createUrlTextField() {
        return horizontalBox(new JLabel("Specify Server URL:"), serverTextField);
    }

    private JScrollPane createServersPane() {
        JScrollPane knownServersPane = new JScrollPane(knownServersList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        knownServersPane.setBorder(new TitledBorder("Available Time Series Servers"));
        return knownServersPane;
    }

    private Box createButtonBox() {
        Box buttonBox = Box.createHorizontalBox();
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(downloadButton);
        return buttonBox;
    }

    private void addProgressTaskListener() {
        loadSeriesFromServerCommand.addTaskListener(
            new ProgressIndicatorTaskListener(
                "Loading Time Series",
                SelectServerPanel.this
            )
        );
    }

    private JComponent horizontalBox(JComponent... components) {
        Box b = Box.createHorizontalBox();
        for ( JComponent c: components) {
            b.add(c);
        }
        b.add(Box.createHorizontalGlue());
        return b;
    }

    private void buildList(TimeSeriesServerDictionary serverDictionary) {
        DefaultListModel defaultListModel = new DefaultListModel();
        List<TimeSeriesServer> servers = serverDictionary.getKnownTimeSeriesServer();
        for ( TimeSeriesServer s : servers) {
            if ( System.currentTimeMillis() - s.getLastAnnounceTimestamp() < HIDE_SERVERS_WITH_LAST_PING_OLDER_THAN_MILLIS) {
                defaultListModel.addElement(s);
            }
        }
        knownServersList = new JList(defaultListModel);
        knownServersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        knownServersList.setCellRenderer(new RemoteTimeSeriesServerListCellRenderer());
    }

    private class DownloadAction extends ModelDrivenAction<ListSelectionActionModel<TimeSeriesServer>> {

        public DownloadAction() {
            super(listSelectionActionModel, "Download Series from Server", ImageUtils.DOWNLOAD_16x16);
        }

        public void actionPerformed(ActionEvent e) {
            downloadListSelections();
        }
    }

    private void loadSeriesFromTextFieldUrl() {
        try {
            if ( serverTextField.getText().trim() != null) {
                URL url = new URL(serverTextField.getText().trim() + "/" + TimeSeriesIndexHandler.INDEX_POSTFIX);
                TimeSeriesServer server = null;
                try {
                    server = new TimeSeriesServer(
                        url.getHost(),
                        url.getPort(),
                        "Server at " + url.getHost() + ":" + url.getPort()
                    );
                } catch (UnknownHostException e) {
                    JOptionPane.showMessageDialog(this, "Cannot find address for host " + url.getHost(), "Server not found", JOptionPane.WARNING_MESSAGE);
                }
                if (server != null) {
                    loadSeriesFromServerCommand.execute(server);
                }
            }
        } catch (MalformedURLException e1) {
            logMethods.logError("Bad URL specified", e1);
        }
    }

    private void downloadListSelections() {
        final TimeSeriesServer server = (TimeSeriesServer)knownServersList.getSelectedValue();
        if ( server != null) {
            loadSeriesFromServerCommand.execute(server);
        }
    }

    public static class RemoteTimeSeriesServerListCellRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            TimeSeriesServer server = (TimeSeriesServer)value;
            setText(server.getDescription());
            setIcon(ImageUtils.TIMESERIES_SERVER_ICON_24x24);
            return this;
        }
    }

}
