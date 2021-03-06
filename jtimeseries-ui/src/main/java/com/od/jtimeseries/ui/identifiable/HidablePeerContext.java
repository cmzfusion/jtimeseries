/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
 *
 * This file is part of JTimeseries.
 *
 * JTimeseries is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimeseries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JTimeseries.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.od.jtimeseries.ui.identifiable;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.impl.SeriesContext;
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
 * When the peer component is hidden, it's config is stored so that it can be restored and
 * shown again later
 */
public abstract class HidablePeerContext<E extends ExportableConfig, P> extends SeriesContext {

    public static final String SHOWN_PROPERTY = "shown";

    private E peerConfig;
    private P peerResource;
    private boolean shown;

    public HidablePeerContext(String id, String description, E peerConfig, boolean shown) {
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

    /**
     * Subclass may override
     */
    public void bringToFront() {
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
                disposePeer();
            }
            fireNodeChanged(SHOWN_PROPERTY);
        }
    }

    public void disposePeer() {
        peerConfig = createPeerConfig(false);
        disposePeerResource();
    }

    public void setPeerResource(P peerResource) {
        this.peerResource = peerResource;
    }

    public P getPeerResource() {
        return peerResource;
    }

    public abstract HidablePeerContext<E,P> newInstance(TimeSeriesContext parent, E config);
}
