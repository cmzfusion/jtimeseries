package com.od.jtimeseries.ui.util;

import junit.framework.TestCase;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


/**
* keep a firm ref to JButtons we create, so that we can test that weak reference listeners added to the
* buttons can be removed from the buttons by the cleanup thread and gc'd, once their delegateListeners have been gc'd
*
* When each MyCollectable is created, it wraps it's actionListener with a weak reference listener
* and registers the weak reference listner with the button to listen for button action events
*
* Since a weak reference listener was added to the button, the MyCollectables are are available for gc.
* Once a MyCollectable is gc'd, the weak ref listener cleanup thread should then remove
* weak reference proxy listeners from these buttons, and the weak ref listener should
* then itself be freed up for gc
*
* We can check this works by keeping a local collection of weak references to the weak reference listeners
* created by MyCollectable. At the end of the test, some of these weak references should have had their referant
* weak reference listener garbage collected - proving that weak reference listeners are successfully being removed
* from the JButtons by the cleanup thread and are freed for gc as expected
*/
 public class TestWeakReferenceListener extends TestCase {

    private Set<WeakReference<WeakReferenceListener>> listeners = Collections.synchronizedSet(
            new HashSet<WeakReference<WeakReferenceListener>>());

    private List<JButton> buttons = new ArrayList<JButton>();

    public void testListener() throws InvocationTargetException, InterruptedException {

        WeakReferenceListener.setCleanupPeriod(1);

        //usually two batches are enough for some WeakReferenceListener to be freed up and collected
        for ( int loop=0; loop < 2; loop ++) {
            SwingUtilities.invokeAndWait(
                new Runnable() {
                    public void run() {
                        createCollectables();
                    }
                }
            );
            Thread.sleep(1000);
        }

        int collectCount = 0;
        for ( WeakReference<WeakReferenceListener> l : listeners ) {
            if ( l.get() == null ) {
                collectCount++;
            }
        }

        System.out.println("Collected: " + collectCount);
        assertTrue("No WeakReferenceListener collected", collectCount > 0);
    }

    private void createCollectables() {
        for ( int loop=0; loop < 10000; loop ++ ) {
            JButton b = new JButton();
            buttons.add(b);
            new MyCollectable(listeners, b);
        }
    }

    public static class MyCollectable {

        private ActionListener a = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        };

        public MyCollectable(Set<WeakReference<WeakReferenceListener>> refs, JButton b) {
            WeakReferenceListener l = new WeakReferenceListener(a);
            l.addListenerTo(b);
            refs.add(new WeakReference<WeakReferenceListener>(l));
        }

    }

}