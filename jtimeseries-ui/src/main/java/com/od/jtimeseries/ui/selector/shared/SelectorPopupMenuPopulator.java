package com.od.jtimeseries.ui.selector.shared;

import com.od.jtimeseries.util.identifiable.Identifiable;

import javax.swing.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 04/01/11
 * Time: 19:24
 * To change this template use File | Settings | File Templates.
 */
public interface SelectorPopupMenuPopulator {

    void addMenuItems(JPopupMenu menu, SelectorComponent s, List<Identifiable> selectedIdentifiable);
}
