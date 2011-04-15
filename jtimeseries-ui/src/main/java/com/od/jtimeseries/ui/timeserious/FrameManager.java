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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 08/04/11
 * Time: 06:43
 */
public class FrameManager implements ConfigAware {

    private TimeSeriousMainFrame mainFrame;
    private UiTimeSeriesServerDictionary udpPingHttpServerDictionary;
    private ApplicationActionModels applicationActionModels;
    private DisplayNameCalculator displayNameCalculator;
    private TimeSeriousRootContext rootContext;
    private ExitAction exitAction;
    private MainSeriesSelector mainSeriesSelector;

    public FrameManager(UiTimeSeriesServerDictionary udpPingHttpServerDictionary,
                        ApplicationActionModels applicationActionModels,
                        DisplayNameCalculator displayNameCalculator,
                        TimeSeriousRootContext rootContext,
                        final ExitAction exitAction) {
        this.udpPingHttpServerDictionary = udpPingHttpServerDictionary;
        this.applicationActionModels = applicationActionModels;
        this.displayNameCalculator = displayNameCalculator;
        this.rootContext = rootContext;
        this.exitAction = exitAction;
        this.mainSeriesSelector = new MainSeriesSelector(
            rootContext,
            applicationActionModels,
            udpPingHttpServerDictionary
        );
    }

    public List<ConfigAware> getConfigAwareChildren() {
        return Arrays.asList((ConfigAware)mainSeriesSelector, mainFrame);
    }

    public void prepareConfigForSave(TimeSeriousConfig config) {
    }

    public void restoreConfig(TimeSeriousConfig config) {
        showFrames();
        addShowFrameTreeListener();
    }

    private void showFrames() {
        List<DesktopContext> contexts = rootContext.findAll(DesktopContext.class).getAllMatches();
        for ( DesktopContext context : contexts) {
            if ( context.isShown()) {
                showFrame(context);
            }
        }
    }

    private void addShowFrameTreeListener() {
        rootContext.addTreeListener(AwtSafeListener.getAwtSafeListener(new ShowFrameTreeListener(), IdentifiableTreeListener.class));
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
                        showFrame(n);
                    }
                }
            }
        }

        public void descendantChanged(IdentifiableTreeEvent contextTreeEvent) {
            for ( Identifiable c : contextTreeEvent.getNodes()) {
                if ( c instanceof DesktopContext) {
                    DesktopContext n = (DesktopContext)c;
                    if ( n.isShown()) {
                        showFrame(n);
                    }
                }
            }
        }
    }

    private void showFrame(DesktopContext desktopContext) {
        AbstractDesktopFrame frame;
        if ( desktopContext.isMainDesktopContext()) {
            mainFrame = createMainFrame(desktopContext);
            frame = mainFrame;
        } else {
            frame = new DefaultDesktopFrame(
                udpPingHttpServerDictionary,
                displayNameCalculator,
                desktopContext,
                mainSeriesSelector.getSelectionPanel()
            );
        }
        frame.setVisible(true);
    }

    private TimeSeriousMainFrame createMainFrame(DesktopContext desktopContext) {
        return new TimeSeriousMainFrame(udpPingHttpServerDictionary,applicationActionModels, exitAction, displayNameCalculator, rootContext, mainSeriesSelector, desktopContext);
    }
}
