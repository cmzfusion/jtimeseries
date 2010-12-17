package com.od.jtimeseries.util.identifiable;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 13-Dec-2010
 * Time: 20:38:44
 */
public interface IdentifiableTreeListener {

    /**
     * called when the node to which the listener was added changes
     */
    public void nodeChanged(Identifiable node, Object changeDescription);
    
    /**
     * descendant nodes changed in a way which did not affect tree structure (e.g. node description changed)
     */
    public void descendantChanged(IdentifiableTreeEvent contextTreeEvent);

    /**
     * descendant nodes were added
     */
    public void descendantAdded(IdentifiableTreeEvent contextTreeEvent);

    /**
     * descendant nodes were removed
     */
    public void descendantRemoved(IdentifiableTreeEvent contextTreeEvent);

}
