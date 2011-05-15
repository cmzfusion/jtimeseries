package com.od.jtimeseries.ui.uicontext;

import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.swing.action.ListSelectionActionModel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 07/01/11
 * Time: 06:58
 * To change this template use File | Settings | File Templates.
 */
public class IdentifiableListActionModel extends ListSelectionActionModel<Identifiable> {

    private Map<Class, List<? extends Identifiable>> nodesByClass = new HashMap<Class, List<? extends Identifiable>>();

    public IdentifiableListActionModel() {
    }

    public IdentifiableListActionModel(List<? extends Identifiable> l) {
        setSelected(l);
    }

    public void setSelected(List<? extends Identifiable> series) {
        nodesByClass.clear();
        super.setSelected(series);
    }

    public void addSelected(Identifiable series) {
        nodesByClass.clear();
        super.addSelected(series);
    }

    public void removeSelected(Identifiable series) {
        nodesByClass.clear();
        super.removeSelected(series);
    }

    public void setSelected(Identifiable series){
        nodesByClass.clear();
        super.setSelected(series);
    }

    public void doClearActionModelState() {
        nodesByClass.clear();
        super.doClearActionModelState();
    }

    public boolean isSelectionLimitedToType(Class... type) {
        int total = 0;
        for ( Class c : type ) {
            total += getSelected(c).size();
        }
        return total == getSelected().size() && total > 0;
    }

    public <C extends Identifiable> java.util.List<C> getSelected(Class<C> clazz) {
        List<C> identifiables = (List<C>)nodesByClass.get(clazz);
        if ( identifiables == null ) {
            identifiables = new LinkedList<C>();
            List<Identifiable> l = getSelected();
            for ( Identifiable i : l) {
                if ( clazz.isAssignableFrom(i.getClass())) {
                    identifiables.add((C)i);
                }
            }
            nodesByClass.put(clazz, identifiables);
        }
        return identifiables;
    }

}