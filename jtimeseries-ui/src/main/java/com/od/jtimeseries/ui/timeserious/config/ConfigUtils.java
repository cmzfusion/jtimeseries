package com.od.jtimeseries.ui.timeserious.config;

import com.jidesoft.dialog.StandardDialog;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 29-Apr-2010
 * Time: 07:56:08
 */
public class ConfigUtils {

    private final String rootNodePath = "jtimeseries/timeserious/ui/configs";
    private final String configMapNodeName = "configMap";

    private List<SavedConfig> configs = new LinkedList<SavedConfig>();

    public ConfigUtils() {
        loadConfigsFromPreferences();
    }

    public void setMostRecent(SavedConfig config) {
        configs.remove(config);
        configs.add(0, config);
        storeConfigsToPreferences();
    }

    /**
     * @return SelectedConfig, or null if user did not select an existing config
     */
    public SavedConfig getSelectedConfig() {
        SavedConfig result = null;
        if ( configs.size() == 1) {
            return configs.get(0);
        } else if ( configs.size() > 1) {
            ConfigSelectionDialog d = new ConfigSelectionDialog(configs);
            d.pack();
            d.setVisible(true);
            result = d.getSelectedConfig();
        }
        return result;
    }

    public void loadConfigsFromPreferences()  {
        LinkedList<SavedConfig> result = new LinkedList<SavedConfig>();
        try {
            Preferences prefs = getRootNode();
            byte[] configMap = prefs.getByteArray(configMapNodeName, null);
            if ( configMap != null ) {
                ByteArrayInputStream bis = new ByteArrayInputStream(configMap);
                XMLDecoder d = new XMLDecoder(bis);
                result = (LinkedList<SavedConfig>)d.readObject();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        configs = result;
    }

    public void storeConfigsToPreferences() {
        Preferences prefs = getRootNode();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLEncoder encoder = null;
        try {
            encoder = new XMLEncoder(bos);
            encoder.writeObject(configs);
            encoder.flush();
            if ( bos.size() > (Preferences.MAX_VALUE_LENGTH * 0.75) ) {
                throw new IOException("Saved config preferences too large to save, please remove some config files");
            }
            prefs.putByteArray(configMapNodeName, bos.toByteArray());
            prefs.flush();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if ( encoder != null ) {
                encoder.close();
            }
        }
    }

    private Preferences getRootNode() {
        return Preferences.userRoot().node(rootNodePath);
    }


}
