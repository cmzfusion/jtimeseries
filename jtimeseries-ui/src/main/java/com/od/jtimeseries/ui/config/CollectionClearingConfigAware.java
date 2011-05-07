package com.od.jtimeseries.ui.config;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 05/01/11
 * Time: 18:32
 * To change this template use File | Settings | File Templates.
 */
public interface CollectionClearingConfigAware {

    void prepareConfigForSave(TimeSeriousConfig config);

    void restoreConfig(TimeSeriousConfig config);

    List<CollectionClearingConfigAware> getConfigAwareChildren();

    void clearConfig();

}
