package com.od.jtimeseries.ui.timeserious.action;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.dialog.ButtonEvent;
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
                    serverDetailsPage.getHostName(),
                    0
                );

                TimeSeriesServerContext context = new TimeSeriesServerContext(
                    s, s.getDescription(), s.getDescription()
                );
                rootContext.addChild(context);

                closeCurrentPage();
            }
        }
    }

    private class ServerDetailsPage extends DefaultWizardPage {

        JTextField hostField  = new JTextField(20);
        JTextField portField = new JTextField(20);

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
            JPanel p = new JPanel();

            //layout.setRowGroups(new int[][]{{2,4,6}});
            p.setLayout(layout);

            CellConstraints cc = new CellConstraints();
            p.add(new JLabel("Host"), cc.xy(2, 4));
            p.add(hostField, cc.xy(4, 4));
            p.add(new JLabel("Port"), cc.xy(2, 6));
            p.add(portField, cc.xy(4, 6));

            addComponent(p);
        }

        @Override
        public void setupWizardButtons() {
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.NEXT);
        }

    }
}
