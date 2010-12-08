package com.od.jtimeseries.ui.util;

import javax.swing.*;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Nick Ebbutt (Object Definitions Ltd.)
 * <p/>
 * Create a dynamic proxy for a listener class, which delegates to a listener instance
 * wrapped in a weak reference
 */
public class WeakReferenceListener {

    private static final Object cleanupLock = new Object();
    private static final ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor();
    private static boolean cleanupRunning;

    //really we need a WeakHashSet
    private static final WeakHashMap<WeakReferenceListener, WeakReferenceListener> listeners = new WeakHashMap<WeakReferenceListener, WeakReferenceListener>();

    private volatile WeakReference listenerDelegate;
    private List<Class> listenerClassInterfaces;
    private Class listenerClass;
    private Class[] addAndRemoveArguments;

    private Object proxyListener;

    private List<WeakReference<Object>> targetObservables = new LinkedList<WeakReference<Object>>();
    private Object firstAddAndRemoveArgument;

    public WeakReferenceListener(Object firstAddAndRemoveArgument, Object delegateListener) {
        this(delegateListener);
        this.firstAddAndRemoveArgument = firstAddAndRemoveArgument;
        addAndRemoveArguments = new Class[] { firstAddAndRemoveArgument.getClass(), listenerClass};
    }

    public WeakReferenceListener(Object delegateListener) {
        listenerClass = delegateListener.getClass();
        listenerDelegate = new WeakReference(delegateListener);
        addAndRemoveArguments = new Class[] { listenerClass };
        listenerClassInterfaces = getAllInterfaces(delegateListener.getClass());
    }

    private LinkedList<Class> getAllInterfaces(Class c) {
        HashSet<Class> interfaces = new HashSet<Class>();
        addAllInterfaces(c, interfaces);
        return new LinkedList<Class>(interfaces);
    }

    private int getListenerMethodArgumentCount() {
        return addAndRemoveArguments.length;
    }

    private void addAllInterfaces(Class startClass, HashSet<Class> interfaces) {
        for (Class c : startClass.getInterfaces()) {
            interfaces.add(c);
        }

        //recursively add up the class heirarchy
        Class superClass = startClass.getSuperclass();
        if (superClass != null) {
            addAllInterfaces(superClass, interfaces);
        }
    }

    private boolean isDelegateCollected() {
        return listenerDelegate.get() == null;
    }

    public boolean addListenerTo(Object targetObservable) {

        Object proxyListener = getOrCreateProxyListener();

        //now find a method to add proxyListener to the observable
        boolean success = false;
        Method[] methods = targetObservable.getClass().getMethods();
        for ( Method m : methods ) {
            if ( m.getName().startsWith("add") && m.getParameterTypes().length == getListenerMethodArgumentCount() && listenerClassInterfaces.contains(m.getParameterTypes()[getListenerMethodArgumentCount() -1])) {
                try {
                    if ( firstAddAndRemoveArgument == null) {
                        m.invoke(targetObservable, proxyListener);
                    } else {
                        m.invoke(targetObservable, firstAddAndRemoveArgument, proxyListener);
                    }
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }

        if ( success ) {
            addForCleanup(targetObservable);
        }

        return success;
    }

    private void addForCleanup(Object targetObservable) {
        synchronized(cleanupLock) {
            //keep a reference to the observable, so that we can remove the proxy listener from it
            targetObservables.add(new WeakReference<Object>(targetObservable));

             //we are going to add the proxy listener to target observables,
            //so add this weak reference listener to the queue so that we can remove
            //proxyListener from target observables if the delegateListener is collected
            listeners.put(this, this);
        }

        checkCleanupThreadStarted();
    }

    public boolean removeListenerFrom(Object targetObservable) {

        boolean success = false;

        //now find a method to remove proxyListener from the observable
        if ( proxyListener != null ) {
            Method[] methods = targetObservable.getClass().getMethods();
            for ( Method m : methods ) {
                if ( m.getName().startsWith("remove") && m.getParameterTypes().length == getListenerMethodArgumentCount() &&  listenerClassInterfaces.contains(m.getParameterTypes()[getListenerMethodArgumentCount() -1])) {
                    try {
                        if ( firstAddAndRemoveArgument == null) {
                            m.invoke(targetObservable, proxyListener);
                        } else {
                            m.invoke(targetObservable, firstAddAndRemoveArgument, proxyListener);
                        }
                        success = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }

            if ( success ) {
                removeFromCleanup(targetObservable);
            }
        }

        return success;
    }

    private void removeFromCleanup(Object targetObservable) {
        synchronized (cleanupLock) {
            WeakReference<Object> ref = null;
            for ( WeakReference<Object> o : targetObservables) {
                if (o.get() == targetObservable) {
                    ref = o;
                }
            }

            if ( ref != null) {
                targetObservables.remove(ref);
            }

            if ( targetObservables.size() == 0 ) {
                listeners.remove(this);
            }
        }
    }


    private void checkCleanupThreadStarted() {
        if ( ! cleanupRunning ) {
            s.scheduleWithFixedDelay(new Runnable() {
                public void run() {
                    List<WeakReferenceListener> currentListeners;
                    synchronized (cleanupLock) {
                        currentListeners = new LinkedList<WeakReferenceListener>(listeners.keySet());
                    }

                    List<WeakReferenceListener> toDispose = new LinkedList<WeakReferenceListener>();

                    for ( WeakReferenceListener l : currentListeners) {
                        if ( l.isDelegateCollected() ) {
                            toDispose.add(l);
                        }
                    }

                    for ( final WeakReferenceListener l : toDispose) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                l.dispose();
                            }
                        });
                    }
                }
            }, 60, 60, TimeUnit.SECONDS);
            cleanupRunning = true;
        }
    }

    private void dispose() {
        //the delegate listener has been collected, good news! this is why we created a weak reference listener in the first place!
        //now we can remove this WeakReferenceListner from each of the observables it was added to and allow weak ref to be garbage collected
        for ( WeakReference<Object> observable : targetObservables) {
            Object o = observable.get();
            if ( o != null) {
                removeListenerFrom(o);
            }
        }
    }

    private Object getOrCreateProxyListener() {
        if ( proxyListener == null ) {
            InvocationHandler handler = new InvocationHandler() {
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (listenerDelegate.get() == null) {
                        return null;
                    } else {
                        Object delegate = listenerDelegate.get();
                        return method.invoke(delegate, args);
                    }
                }
            };

            proxyListener = Proxy.newProxyInstance(listenerClass.getClassLoader(),
                    listenerClassInterfaces.toArray(new Class[listenerClassInterfaces.size()]),
                    handler);
            }
        return proxyListener;
    }

}
