package com.od.jtimeseries.ui.selector.table;

import com.od.jtimeseries.ui.selector.shared.SelectorComponent;
import com.od.jtimeseries.util.NamedExecutors;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeEvent;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeListener;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
* Created by IntelliJ IDEA.
* User: nick
* Date: 20/12/10
*
* Individual inserts into jide table are extremely inefficiently handled when we have a large number of separate inserts
* This class coalesces events from the context tree so that the table is only update once
*
*/
class CoalescingTreeListener<E> implements IdentifiableTreeListener {

    private static ScheduledExecutorService coalescingEventExceutor = NamedExecutors.newSingleThreadScheduledExecutor("Selector-CoalescingEventExecutor");

    private final LinkedList<TableChange> changeList = new LinkedList<TableChange>();
    private ScheduledFuture updateEvent;
    private Class seriesClass;
    private BeanPerRowModel<E> tableModel;
    private Component tableComponent;

    CoalescingTreeListener(Class seriesClass, BeanPerRowModel<E> tableModel, Component tableComponent) {
        this.seriesClass = seriesClass;
        this.tableModel = tableModel;
        this.tableComponent = tableComponent;
    }

    public void nodeChanged(Identifiable node, Object changeDescription) {
    }

    public void descendantChanged(IdentifiableTreeEvent contextTreeEvent) {
        tableComponent.repaint();
    }

    public void descendantAdded(IdentifiableTreeEvent contextTreeEvent) {
        java.util.List<E> timeSeries = SelectorComponent.getAffectedSeries(seriesClass, contextTreeEvent);
        if ( timeSeries.size() > 0 ) {
            synchronized (changeList) {
                if ( changeList.size() > 0 && changeList.getLast().isAdd()) {
                    changeList.getLast().addBeans(timeSeries);
                } else {
                    changeList.add(new TableChange(true, timeSeries));
                }
                scheduleUpdate();
            }
        }
    }

    public void descendantRemoved(IdentifiableTreeEvent contextTreeEvent) {
        java.util.List<E> timeSeries = SelectorComponent.getAffectedSeries(seriesClass, contextTreeEvent);
        if ( timeSeries.size() > 0 ) {
            synchronized (changeList) {
                if ( changeList.size() > 0 && ! changeList.getLast().isAdd()) {
                    changeList.getLast().addBeans(timeSeries);
                } else {
                    changeList.add(new TableChange(false, timeSeries));
                }
                scheduleUpdate();
            }
        }
    }

    private void scheduleUpdate() {
        if ( updateEvent == null || updateEvent.isDone()) {
            updateEvent = coalescingEventExceutor.schedule(new TableUpdateTask<E>(tableModel), 150, TimeUnit.MILLISECONDS);
        }
    }

    private static class TableChange {
        private boolean isAdd;
        private java.util.List beans;

        private TableChange(boolean add, java.util.List beans) {
            isAdd = add;
            this.beans = beans;
        }

        public void addBeans(java.util.List beans) {
            this.beans.addAll(beans);
        }

        public boolean isAdd() {
            return isAdd;
        }

        public java.util.List getBeans() {
            return beans;
        }
    }

    private class TableUpdateTask<E> implements Runnable {

        private BeanPerRowModel<E> tableModel;

        private TableUpdateTask(BeanPerRowModel<E> tableModel) {
            this.tableModel = tableModel;
        }

        public void run() {
            final LinkedList<TableChange> changeListSnapshot;
            synchronized (changeList) {
                changeListSnapshot = new LinkedList<TableChange>(changeList);
                changeList.clear();
            }

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    for ( TableChange c : changeListSnapshot ) {
                        if ( c.isAdd() ) {
                            tableModel.addObjects(c.getBeans());
                        } else {
                            //TODO since there's no method to remove multiple beans the only way would be
                            //to clear and re-add everything, at present we don't coalesce removes
                            for ( Object o : c.getBeans()) {
                                tableModel.removeObject((E)o);
                            }
                        }
                    }
                }
            });


        }
    }
}
