package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.config.ConfigAware;
import com.od.jtimeseries.ui.config.TimeSeriousConfig;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.net.udp.UiTimeSeriesServerDictionary;
import com.od.jtimeseries.ui.timeserious.action.ApplicationActionModels;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 08/04/11
 * Time: 06:43
 */
public class FrameManager implements ConfigAware {

    private TimeSeriousMainFrame mainFrame;

    public FrameManager(UiTimeSeriesServerDictionary udpPingHttpServerDictionary, ApplicationActionModels applicationActionModels, DisplayNameCalculator displayNameCalculator, TimeSeriousRootContext rootContext, final ExitAction exitAction) {
        this.mainFrame = new TimeSeriousMainFrame(udpPingHttpServerDictionary,applicationActionModels, exitAction, displayNameCalculator, rootContext);


        mainFrame.setVisible(true);

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if ( ! exitAction.confirmAndSaveConfig(e.getWindow()) ) {
                    //there's no mechanism to cancel the close which I can find, barring throwing an exception
                    //which is then handled by some dedicated logic in the Component class
                    throw new RuntimeException("User cancelled exit");
                }
            }
        });
    }

    public void prepareConfigForSave(TimeSeriousConfig config) {
    }

    public void restoreConfig(TimeSeriousConfig config) {
    }

    public List<ConfigAware> getConfigAwareChildren() {
        return Collections.singletonList((ConfigAware)mainFrame);
    }

    public TimeSeriousMainFrame getMainFrame() {
        return mainFrame;
    }
}
