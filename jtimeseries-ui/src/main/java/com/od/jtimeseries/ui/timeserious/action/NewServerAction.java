package com.od.jtimeseries.ui.timeserious.action;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.dialog.ButtonEvent;
import com.jidesoft.dialog.ButtonNames;
import com.jidesoft.dialog.PageList;
import com.jidesoft.wizard.DefaultWizardPage;
import com.jidesoft.wizard.WizardDialog;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.httpd.AttributeName;
import com.od.jtimeseries.net.httpd.ElementName;
import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.ui.download.panel.TimeSeriesServerContext;
import com.od.jtimeseries.ui.net.AbstractRemoteQuery;
import com.od.jtimeseries.ui.net.udp.UiTimeSeriesServerDictionary;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.swing.progress.ProgressIndicatorTaskListener;
import com.od.swing.progress.ProgressLayeredPane;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;
import swingcommand.BackgroundTask;
import swingcommand.SwingCommand;
import swingcommand.Task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 12-Dec-2010
 * Time: 19:14:32
 */
public class NewServerAction extends AbstractAction {

    private JFrame frame;
    private TimeSeriesContext rootContext;

    public NewServerAction(JFrame frame, TimeSeriesContext rootContext) {
        super("New Server", ImageUtils.ADD_SERVER_ICON_16x16);
        this.frame = frame;
        this.rootContext = rootContext;
        super.putValue(SHORT_DESCRIPTION, "Add a new server to connect and download series data");
    }

    public void actionPerformed(ActionEvent e) {
        NewServerWizard w = new NewServerWizard(frame);
        w.setLocationRelativeTo(frame);
        w.setVisible(true);
    }

    private class NewServerWizard extends WizardDialog {
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


        private class FinishAction extends AbstractAction {

            public FinishAction() {
                super("Add Server", ImageUtils.ADD_SERVER_ICON_16x16);
            }

            public void actionPerformed(ActionEvent e) {
                SwingCommand checkServerCommand = new CheckAndCreateServerCommand();
                checkServerCommand.execute(
                    new ProgressIndicatorTaskListener("Checking Server", serverDetailsPage.serverFormPanel )
                );
            }

            private class CheckAndCreateServerCommand extends SwingCommand {

                TimeSeriesServer server;

                @Override
                protected Task createTask() {
                    return new BackgroundTask() {

                        @Override
                        protected void doInBackground() throws Exception {
                            Thread.sleep(1000);
                            
                            InetAddress i = InetAddress.getByName(serverDetailsPage.getHostName());
                            server = new TimeSeriesServer(
                                    i,
                                    serverDetailsPage.getPort(),
                                    serverDetailsPage.getHostName(),
                                    0
                            );

                            class CheckServerQuery extends AbstractRemoteQuery {

                                private boolean success;

                                public CheckServerQuery(TimeSeriesServer s) throws MalformedURLException {
                                    super(new URL("http://" + serverDetailsPage.getHostName() + ":" + serverDetailsPage.getPort() + "/"));
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

                            CheckServerQuery q = new CheckServerQuery(server);
                            q.runQuery();

                            if ( ! q.success ) {
                                throw new Exception("Failed to connect to timeseries server at " + q.getQueryUrl());
                            }
                        }

                        @Override
                        protected void doInEventThread() throws Exception {
                          TimeSeriesServerContext context = new TimeSeriesServerContext(
                                server,
                                server.getDescription(),
                                server.getDescription()
                          );
                          rootContext.addChild(context);
                          closeCurrentPage();
                          setVisible(false);
                        }
                    };
                }
            }
        }
    }

    private class ServerDetailsPage extends DefaultWizardPage {

        private JTextField hostField = new JTextField(20);
        private JTextField portField = new JTextField(20);
        JPanel serverFormPanel = new JPanel();

        public ServerDetailsPage(String title, String description) {
            super(title, description);
        }

        public String getHostName() {
            return hostField.getText();
        }

        public int getPort() {
            //TODO
            return Integer.parseInt(portField.getText());
        }

        @Override
        public void initContentPane() {
            FormLayout layout = new FormLayout(
                    "10dlu:grow, pref:none:right, 3dlu:none, pref:none, 10dlu:grow",
                    "2dlu:grow, pref:none, 5dlu:none, pref:none, 5dlu:none, pref:none, 10dlu:grow"
            );

            //layout.setRowGroups(new int[][]{{2,4,6}});
            serverFormPanel.setLayout(layout);

            CellConstraints cc = new CellConstraints();
            serverFormPanel.add(new JLabel("Host"), cc.xy(2, 4));
            serverFormPanel.add(hostField, cc.xy(4, 4));
            serverFormPanel.add(new JLabel("Port"), cc.xy(2, 6));
            serverFormPanel.add(portField, cc.xy(4, 6));

            ProgressLayeredPane progressPane = new ProgressLayeredPane(serverFormPanel);
            addComponent(progressPane);
        }

        @Override
        public void setupWizardButtons() {
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.NEXT);
        }

    }
}
