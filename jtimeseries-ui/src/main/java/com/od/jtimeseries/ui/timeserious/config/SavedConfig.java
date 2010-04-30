package com.od.jtimeseries.ui.timeserious.config;

import java.net.URI;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 29-Apr-2010
 * Time: 08:13:22
 */
public class SavedConfig {

    private String name;
    private String uri;
    private long lastLoaded;

    public SavedConfig(String name, String uri, long lastLoaded) {
        this.name = name;
        this.uri = uri;
        this.lastLoaded = lastLoaded;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getLastLoaded() {
        return lastLoaded;
    }

    public void setLastLoaded(long lastLoaded) {
        this.lastLoaded = lastLoaded;
    }

    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SavedConfig that = (SavedConfig) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        result = 31 * result + (int) (lastLoaded ^ (lastLoaded >>> 32));
        return result;
    }
}
