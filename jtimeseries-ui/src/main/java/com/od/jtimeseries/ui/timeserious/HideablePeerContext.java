package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 26/04/11
 * Time: 10:49
 * To change this template use File | Settings | File Templates.
 *
 * A context which relates to a ui element that can be hidden
 * e.g. a visualizer window
 *
 * When the peer is hidden, it's config is stored so that it can be re-shown in the same state
 */
public abstract class HideablePeerContext<E> extends DefaultTimeSeriesContext {

    protected E peerConfig;
    protected boolean shown;

    public HideablePeerContext(String id, String description, E peerConfig, boolean shown) {
        super(id, description);
        this.peerConfig = peerConfig;
        this.shown = shown;
    }

    public E getConfiguration() {
        return isPeerCreatedAndShown() ?
            createVisualizerConfig(true) :
            peerConfig;
    }

    /**
     * On startup, the config may be marked shown on load before the peer has
     * actually been created, so use this method to be sure the peer actually exists
     */
    protected boolean isPeerCreatedAndShown() {
        return isShown() && isPeerCreated();
    }

    protected abstract E createVisualizerConfig(boolean shown);

    public boolean isShown() {
        return shown;
    }

    public boolean isHidden() {
        return ! isShown();
    }

    protected abstract boolean isPeerCreated();

    protected abstract void disposePeerResource();

    public void setShown(boolean shown) {
        if (this.shown != shown) {
            this.shown = shown;
            if (!shown) {
                peerConfig = createVisualizerConfig(false);
                disposePeerResource();
            }
            fireNodeChanged("shown");
        }
    }

    public abstract void setPeerResource(VInternalFrame frame);
}
