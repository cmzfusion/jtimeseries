package com.od.jtimeseries.ui.timeserious.frame;

import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.identifiable.IdentifiableTreeEvent;
import com.od.jtimeseries.identifiable.IdentifiableTreeListener;
import com.od.jtimeseries.identifiable.IdentifiableTreeListenerAdapter;
import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.identifiable.HidablePeerContext;
import com.od.jtimeseries.ui.timeserious.action.BringDesktopToFrontAction;
import com.od.jtimeseries.ui.timeserious.action.NewDesktopAction;
import com.od.jtimeseries.ui.timeserious.action.ShowHidableDesktopAction;
import com.od.jtimeseries.ui.timeserious.mainselector.MainSelectorTreeComparator;
import com.od.jtimeseries.ui.timeserious.rootcontext.TimeSeriousRootContext;
import com.od.swing.weakreferencelistener.WeakReferenceListener;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 17/02/12
 * Time: 19:47
 */
public class WindowMenu extends BaseMenu implements IdentifiableTreeListener {

    private NewDesktopAction newDesktopAction;
    private AddDesktopProcessor addDesktopProcessor = new AddDesktopProcessor();
    private RemoveDesktopProcessor removeDesktopProcessor = new RemoveDesktopProcessor();

    //show the desktop menu items in the same order as they appear in the main selector tree
    private MainSelectorTreeComparator comparator = new MainSelectorTreeComparator();

    public List<DesktopContext> contexts;

    private int firstDesktopMenuItemIndex = 2;

    public WindowMenu(NewDesktopAction newDesktopAction, TimeSeriousRootContext rootContext) {
        super("Window");
        this.newDesktopAction = newDesktopAction;
        buildMenu(rootContext);
    }

    private void buildMenu(TimeSeriousRootContext rootContext) {
        JMenuItem newVisualizerItem = new JMenuItem(newDesktopAction);
        add(newVisualizerItem);
        add(new JSeparator());

        contexts = rootContext.findAll(DesktopContext.class).getAllMatches();
        Collections.sort(contexts, comparator);

        for (DesktopContext c :  contexts) {
            add(new DesktopMenuItem(c));
        }
    }

    public void nodeChanged(Identifiable node, Object changeDescription) {
    }

    public void descendantChanged(IdentifiableTreeEvent contextTreeEvent) {
    }

    public void descendantAdded(IdentifiableTreeEvent contextTreeEvent) {
        contextTreeEvent.processNodesAndDescendants(addDesktopProcessor, DesktopContext.class);
    }

    public void descendantRemoved(IdentifiableTreeEvent contextTreeEvent) {
        contextTreeEvent.processNodesAndDescendants(removeDesktopProcessor, DesktopContext.class);
    }

    private void removeDesktopMenuItem(DesktopContext context) {
        contexts.remove(context);
        for ( Component c : getMenuComponents()) {
            if ( c instanceof DesktopMenuItem && ((DesktopMenuItem)c).getDesktopContext() == context) {
                remove(c);
                break;
            }
        }
    }

    private void insertNewDesktopMenuItem(DesktopContext context) {
        //add in the correct place, governed by the sort comparator
        int newIndex = Collections.binarySearch(contexts, context, comparator);
        if ( newIndex < 0) {
            int indexToInsert = -newIndex - 1;
            add(new DesktopMenuItem(context), indexToInsert + firstDesktopMenuItemIndex);
            contexts.add(indexToInsert, context);
        }
    }

    private class AddDesktopProcessor implements IdentifiableTreeEvent.IdentifiableProcessor<DesktopContext> {
        public void process(final DesktopContext identifiable) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    insertNewDesktopMenuItem(identifiable);
                }
            });
        }
    }

    private class RemoveDesktopProcessor implements IdentifiableTreeEvent.IdentifiableProcessor<DesktopContext> {
        public void process(final DesktopContext identifiable) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    removeDesktopMenuItem(identifiable);
                }
            });
        }
    }

    private class DesktopMenuItem extends JMenuItem {

        private DesktopContext desktopContext;
        private IdentifiableTreeListenerAdapter actionUpdatingListener;

        public DesktopMenuItem(DesktopContext desktopContext) {
            super(desktopContext.getId());
            this.desktopContext = desktopContext;
            setAction(desktopContext);
            actionUpdatingListener = new IdentifiableTreeListenerAdapter() {
                public void nodeChanged(Identifiable node, Object changeDescription) {
                    if (HidablePeerContext.SHOWN_PROPERTY.equals(changeDescription)) {
                        setAction(DesktopMenuItem.this.desktopContext);
                    }
                }
            };
            WeakReferenceListener w = new WeakReferenceListener(actionUpdatingListener);
            w.addListenerTo(desktopContext);
        }

        private void setAction(DesktopContext desktopContext) {
            Action action;
            if ( desktopContext.isHidden()) {
                action = new ShowHidableDesktopAction(desktopContext);
            } else {
                action = new BringDesktopToFrontAction(desktopContext);
            }
            action.putValue(Action.NAME, desktopContext.getId());
            action.putValue(Action.SMALL_ICON, null);
            setAction(action);
        }

        public DesktopContext getDesktopContext() {
            return desktopContext;
        }
    }
}
