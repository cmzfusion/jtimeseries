package com.od.jtimeseries.ui.timeserious.action;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.dialog.ButtonEvent;
import com.jidesoft.dialog.ButtonNames;
import com.jidesoft.dialog.PageList;
import com.jidesoft.wizard.DefaultWizardPage;
import com.od.jtimeseries.net.httpd.ElementName;
import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.ui.net.AbstractRemoteQuery;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.ui.util.ProgressIndicatorWizard;
import com.od.swing.progress.ProgressIndicatorTaskListener;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;
import swingcommand.BackgroundTask;
import swingcommand.SwingCommand;
import swingcommand.Task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 12-Dec-2010
 * Time: 19:14:32
 */
public class NewServerAction extends AbstractAction {

    private JFrame frame;
    private TimeSeriesServerDictionary serverDictionary;

    public NewServerAction(JFrame frame, TimeSeriesServerDictionary serverDictionary) {
        super("New Server", ImageUtils.ADD_SERVER_ICON_16x16);
        this.frame = frame;
        this.serverDictionary = serverDictionary;
        super.putValue(SHORT_DESCRIPTION, "Add a new server to connect and download series data");
    }

    public void actionPerformed(ActionEvent e) {
        NewServerWizard w = new NewServerWizard(frame);
        w.setLocationRelativeTo(frame);
        w.setVisible(true);
    }

    private class NewServerWizard extends ProgressIndicatorWizard {
        private ServerDetailsPage serverDetailsPage = new ServerDetailsPage(
                "Add a new Timeseries Server",
                ""
        );

        private NewServerWizard(Frame frame) throws HeadlessException {
            super(frame, "Select a Config Directory");
            setSize(350, 250);
            setLocationRelativeTo(frame);
            setStepsPaneNavigable(false);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            PageList p = new PageList();
            p.append(serverDetailsPage);

            setPageList(p);

            setFinishAction(new FinishAction());
        }

        private String getServerConnectionUrl() {
            return "http://" + serverDetailsPage.getHostName() + ":" + serverDetailsPage.getPort() + "/";
        }

        private class ServerDetailsPage extends DefaultWizardPage {

            private JTextField hostField = new JTextField(20);
            private JTextField portField = new JTextField(20);
            private JTextField descriptionField = new JTextField(20);
            JPanel serverFormPanel = new JPanel();

            public ServerDetailsPage(String title, String description) {
                super(title, description);
                portField.addActionListener(new FinishAction());
            }

            public String getHostName() {
                return hostField.getText();
            }

            public String getServerDescription() {
                return descriptionField.getText();
            }

            public void setServerDescription(String s) {
                descriptionField.setText(s);
            }

            public int getPort() {
                try {
                    return Integer.parseInt(portField.getText());
                } catch ( NumberFormatException nfe) {
                    return 0;
                }
            }

            @Override
            public void initContentPane() {
                FormLayout layout = new FormLayout(
                        "10dlu:grow, pref:none:right, 3dlu:none, pref:none, 10dlu:grow",
                        "2dlu:grow, pref:none, 5dlu:none, pref:none, 5dlu:none, pref:none, 5dlu:none, pref:none, 10dlu:grow"
                );

                //layout.setRowGroups(new int[][]{{2,4,6}});
                serverFormPanel.setLayout(layout);

                CellConstraints cc = new CellConstraints();
                serverFormPanel.add(new JLabel("Host"), cc.xy(2, 4));
                serverFormPanel.add(hostField, cc.xy(4, 4));
                serverFormPanel.add(new JLabel("Port"), cc.xy(2, 6));
                serverFormPanel.add(portField, cc.xy(4, 6));
                serverFormPanel.add(new JLabel("Description"), cc.xy(2, 8));
                serverFormPanel.add(descriptionField, cc.xy(4, 8));

                addComponent(serverFormPanel);
            }

            @Override
            public void setupWizardButtons() {
                fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.BACK);
                fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.NEXT);
            }

        }


        private class FinishAction extends AbstractAction {

            public FinishAction() {
                super("Add Server", ImageUtils.ADD_SERVER_ICON_16x16);
            }

            public void actionPerformed(ActionEvent e) {
                addProgressPane(0.8f, 24, 15);

                //create a default description if not set
                if ( serverDetailsPage.getServerDescription().trim().length() == 0) {
                    serverDetailsPage.setServerDescription(serverDetailsPage.getHostName() + ":" + serverDetailsPage.getPort());
                }

                final CheckAndCreateServerCommand checkServerCommand = new CheckAndCreateServerCommand();
                checkServerCommand.execute(
                    new ProgressIndicatorTaskListener("Checking Server", serverDetailsPage.serverFormPanel) {
                        public void error(Task task, Throwable error) {
                            JOptionPane.showMessageDialog(
                                NewServerWizard.this,
                                "Cannot connect to server at " + getServerConnectionUrl(),
                                "Cannot connect to server",
                                JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                );
            }

            private class CheckAndCreateServerCommand extends SwingCommand {

                @Override
                protected Task createTask() {
                    return new BackgroundTask() {

                        @Override
                        protected void doInBackground() throws Exception {

                            class CheckServerQuery extends AbstractRemoteQuery {

                                private boolean success;

                                public CheckServerQuery() throws MalformedURLException {
                                    super(new URL(getServerConnectionUrl()));
                                }

                                public ContentHandler getContentHandler() {
                                    return new DefaultHandler() {
                                        public void startElement(String uri, String localName, String qName, Attributes attributes) {
                                            if (localName.equals(ElementName.context.name())) {
                                                success = true;
                                            }
                                        }
                                    };
                                }

                                @Override
                                public String getQueryDescription() {
                                    return "Check Timeseries Server";
                                }
                            }

                            CheckServerQuery q = new CheckServerQuery();
                            q.runQuery();

                            if (!q.success) {
                                throw new Exception("Failed to connect to timeseries server at " + q.getQueryUrl());
                            }

                            if ( serverDictionary.serverExists(serverDetailsPage.getHostName(), serverDetailsPage.getPort())) {
                                JOptionPane.showMessageDialog(NewServerWizard.this,
                                    "A server with this address already exists",
                                    "Duplicate Server",
                                    JOptionPane.INFORMATION_MESSAGE
                                );
                            } else {
                                serverDictionary.getOrCreateServer(
                                    serverDetailsPage.getHostName(),
                                    serverDetailsPage.getPort(),
                                    serverDetailsPage.getServerDescription()
                                );
                            }
                        }

                        @Override
                        protected void doInEventThread() throws Exception {
                            closeCurrentPage();
                            setVisible(false);
                        }
                    };
                }
            }
        }
    }

}
