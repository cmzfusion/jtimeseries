package com.od.jtimeseries.timeseries.impl;

import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.util.identifiable.Identifiable;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-Nov-2010
 * Time: 17:07:26
 *
 */
public class DelegatingIdentifiableTimeSeries extends AbstractDelegatingTimeSeries implements IdentifiableTimeSeries {

    private IdentifiableTimeSeries wrappedSeries;

    public DelegatingIdentifiableTimeSeries(IdentifiableTimeSeries wrappedSeries) {
        super(wrappedSeries);
        this.wrappedSeries = wrappedSeries;
    }

    public String getId() {
        return wrappedSeries.getId();
    }

    public String getParentPath() {
        return wrappedSeries.getParentPath();
    }

    public String getPath() {
        return wrappedSeries.getPath();
    }

    public void setDescription(String description) {
        wrappedSeries.setDescription(description);
    }

    public String getDescription() {
        return wrappedSeries.getDescription();
    }

    public Identifiable getParent() {
        return wrappedSeries.getParent();
    }

    public Identifiable setParent(Identifiable parent) {
        return wrappedSeries.setParent(parent);
    }

    public List<Identifiable> getChildren() {
        return wrappedSeries.getChildren();
    }

    public <E extends Identifiable> List<E> getChildren(Class<E> classType) {
        return wrappedSeries.getChildren(classType);
    }

    public Identifiable get(String path) {
        return wrappedSeries.get(path);
    }

    public <E extends Identifiable> E get(String path, Class<E> classType) {
        return wrappedSeries.get(path, classType);
    }

    public Identifiable remove(String path) {
        return wrappedSeries.remove(path);
    }

    public <E extends Identifiable> E remove(String path, Class<E> classType) {
        return wrappedSeries.remove(path, classType);
    }

    public boolean removeChild(Identifiable c) {
        return wrappedSeries.removeChild(c);
    }

    public ReentrantReadWriteLock getContextLock() {
        return wrappedSeries.getContextLock();
    }

    public Identifiable getRoot() {
        return wrappedSeries.getRoot();
    }

    public boolean isRoot() {
        return wrappedSeries.isRoot();
    }

    public boolean containsChildWithId(String id) {
        return wrappedSeries.containsChildWithId(id);
    }

    public boolean containsChild(Identifiable i) {
        return wrappedSeries.containsChild(i);
    }

    public String getProperty(String propertyName) {
        return wrappedSeries.getProperty(propertyName);
    }

    public Properties getProperties() {
        return wrappedSeries.getProperties();
    }

    public void putAllProperties(Properties p) {
        wrappedSeries.putAllProperties(p);
    }

    public String findProperty(String propertyName) {
        return wrappedSeries.findProperty(propertyName);
    }

    public String setProperty(String propertyName, String value) {
        return wrappedSeries.setProperty(propertyName, value);
    }
}
