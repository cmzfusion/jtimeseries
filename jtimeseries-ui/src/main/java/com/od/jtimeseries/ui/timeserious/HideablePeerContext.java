package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;
import com.od.jtimeseries.ui.config.ExportableConfig;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 26/04/11
 * Time: 10:49
 * To change this template use File | Settings | File Templates.
 *
 * A context which relates to a peer ui element that can be hidden
 * e.g. a visualizer window
 *
 * When the peer component is hidden, it's config is stored so that it can be shown again
 */
public abstract class HideablePeerContext<E extends ExportableConfig, P> extends DefaultTimeSeriesContext {

    public static final String SHOWN_PROPERTY = "shown";

    private E peerConfig;
    private P peerResource;
    private boolean shown;

    public HideablePeerContext(String id, String description, E peerConfig, boolean shown) {
        super(id, description, false);
        this.peerConfig = peerConfig;
        this.shown = shown;
    }

    public E getConfiguration() {
        return isPeerCreatedAndShown() ?
            createPeerConfig(true) :
            peerConfig;
    }

    /**
     * On startup, the config may be marked shown on load before the peer has
     * actually been created, so use this method to be sure the peer actually exists
     */
    protected boolean isPeerCreatedAndShown() {
        return isShown() && isPeerCreated();
    }

    protected abstract E createPeerConfig(boolean shown);

    public boolean isShown() {
        return shown;
    }

    public boolean isHidden() {
        return ! isShown();
    }

    protected boolean isPeerCreated() {
        return peerResource != null;
    }

    protected void disposePeerResource() {
        peerResource = null;
    }

    public void setShown(boolean shown) {
        if (this.shown != shown) {
            this.shown = shown;
            if (!shown) {
                peerConfig = createPeerConfig(false);
                disposePeerResource();
            }
            fireNodeChanged(SHOWN_PROPERTY);
        }
    }

    protected void setPeerResource(P peerResource) {
        this.peerResource = peerResource;
    }

    protected P getPeerResource() {
        return peerResource;
    }

    public abstract HideablePeerContext<E,P> newInstance(TimeSeriesContext parent, E config);
}
