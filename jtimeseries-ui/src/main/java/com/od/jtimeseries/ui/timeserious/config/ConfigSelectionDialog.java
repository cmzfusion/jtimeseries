package com.od.jtimeseries.ui.timeserious.config;

import com.jidesoft.dialog.StandardDialog;
import com.jidesoft.dialog.BannerPanel;
import com.jidesoft.dialog.ButtonPanel;

import javax.swing.*;
import java.util.List;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 29-Apr-2010
* Time: 09:14:26
*/
class ConfigSelectionDialog extends StandardDialog {

    private final List<SavedConfig> configs;
    private final JList configList = new JList();

    public ConfigSelectionDialog(List<SavedConfig> configs) throws HeadlessException {
        this.configs = configs;
    }

    public JComponent createBannerPanel() {
        return new BannerPanel("Choose a Saved Config");
    }

    public JComponent createContentPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        configList.setListData(configs.toArray());
        configList.setSelectedIndex(0);
        p.add(new JScrollPane(configList));
        return p;
    }

    public ButtonPanel createButtonPanel() {
        return createOKCancelButtonPanel();
    }

    public SavedConfig getSelectedConfig() {
        SavedConfig result = null;
        if ( ! configList.isSelectionEmpty() && getDialogResult() == StandardDialog.RESULT_AFFIRMED ) {
            result = (SavedConfig)configList.getSelectedValue();
        }
        return result;
    }
}
