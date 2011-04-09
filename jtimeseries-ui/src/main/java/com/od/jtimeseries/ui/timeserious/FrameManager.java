package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.config.ConfigAware;
import com.od.jtimeseries.ui.config.TimeSeriousConfig;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.net.udp.UiTimeSeriesServerDictionary;
import com.od.jtimeseries.ui.timeserious.action.ApplicationActionModels;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeEvent;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeListener;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeListenerAdapter;
import com.od.swing.util.AwtSafeListener;

import javax.swing.*;
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
        rootContext.addTreeListener(AwtSafeListener.getAwtSafeListener(new ShowFrameTreeListener(), IdentifiableTreeListener.class));
        MainSeriesSelector mainSeriesSelector = new MainSeriesSelector(
            rootContext,
            applicationActionModels,
            udpPingHttpServerDictionary
        );
        this.mainFrame = new TimeSeriousMainFrame(udpPingHttpServerDictionary,applicationActionModels, exitAction, displayNameCalculator, rootContext, mainSeriesSelector);
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

    private class ShowFrameTreeListener extends IdentifiableTreeListenerAdapter {

        public void descendantAdded(IdentifiableTreeEvent contextTreeEvent) {
            for ( Identifiable c : contextTreeEvent.getNodes()) {
                if ( c instanceof DesktopContext) {
                    DesktopContext n = (DesktopContext)c;
                    if ( n.isShown()) {
                        showDesktop(n);
                    }
                }
            }
        }

        public void descendantChanged(IdentifiableTreeEvent contextTreeEvent) {
            for ( Identifiable c : contextTreeEvent.getNodes()) {
                if ( c instanceof DesktopContext) {
                    DesktopContext n = (DesktopContext)c;
                    if ( n.isShown()) {
                        showDesktop(n);
                    }
                }
            }
        }
    }

    private void showDesktop(DesktopContext desktopContext) {
    }
}
