package com.od.jtimeseries.util.identifiable;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 13-Dec-2010
 * Time: 20:38:44
 */
public interface IdentifiableTreeListener {

    /**
     * nodes changed in a way which did not affect tree structure (e.g. node description changed)
     *
     * @param contextTreeEvent
     */
    public void nodesChanged(IdentifiableTreeEvent contextTreeEvent);

    public void nodesAdded(IdentifiableTreeEvent contextTreeEvent);

    public void nodesRemoved(IdentifiableTreeEvent contextTreeEvent);

}
