package com.od.jtimeseries.ui.util;

import junit.framework.TestCase;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TestWeakReferenceListener extends TestCase {

    private Set<WeakReference<WeakReferenceListener>> listeners = Collections.synchronizedSet(
            new HashSet<WeakReference<WeakReferenceListener>>());

    private JButton button = new JButton();

    public void testListener() throws InvocationTargetException, InterruptedException {

        for ( int loop=0; loop < 100; loop ++) {
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
        assertTrue(collectCount > 0);
    }

    private void createCollectables() {
        for ( int loop=0; loop < 1000; loop ++ ) {
            new MyCollectable(listeners, button);
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