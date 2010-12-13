package com.od.jtimeseries.ui.timeserious.action;

import com.jidesoft.dialog.ButtonEvent;
import com.jidesoft.dialog.ButtonListener;
import com.jidesoft.dialog.ButtonNames;
import com.jidesoft.dialog.PageList;
import com.jidesoft.wizard.DefaultWizardPage;
import com.jidesoft.wizard.WizardDialog;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.ui.download.panel.TimeSeriesServerContext;
import com.od.jtimeseries.ui.net.udp.UiTimeSeriesServerDictionary;
import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 12-Dec-2010
 * Time: 19:14:32
 *
 */
public class NewServerAction extends AbstractAction {

    private JFrame frame;
    private UiTimeSeriesServerDictionary serverDictionary;

    public NewServerAction(JFrame frame, UiTimeSeriesServerDictionary serverDictionary, TimeSeriesContext rootContext) {
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

            setFinishAction(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {

                    //TODO
                    InetAddress i = null;
                    try {
                        i = InetAddress.getByName(serverDetailsPage.getHostName());
                    } catch (UnknownHostException e1) {
                        e1.printStackTrace();
                    }

                    TimeSeriesServer s = new TimeSeriesServer(
                        i,
                        serverDetailsPage.getPort(),
                        serverDetailsPage.getServerDescription(),
                        0
                    );

                    boolean added = serverDictionary.addServer(s);
//                    if (added) {
//                        TimeSeriesServerContext context = new TimeSeriesServerContext(
//                            s, rootContext, s.getDescription(), s.getDescription()
//                        );
//                    }

                    closeCurrentPage();
                }
            });
        }


    }

    private class ServerDetailsPage extends DefaultWizardPage {

        JTextField hostField  = new JTextField();
        JTextField portField = new JTextField();
        JTextField serverDescriptionField = new JTextField();

        public ServerDetailsPage(String title, String description) {
            super(title, description);
        }

        public String getHostName() {
            return hostField.getText();
        }

        public String getServerDescription() {
            return serverDescriptionField.getText();
        }

        public int getPort() {
            //TODO
            return Integer.parseInt(portField.getText());
        }

        @Override
        public void initContentPane() {
            addComponent(hostField);
            addComponent(portField);
        }

        @Override
        public void setupWizardButtons() {
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.NEXT);
        }

    }
}
