package com.od.jtimeseries.ui.config;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 09/05/11
 * Time: 09:34
 *
 */
public class DisplayNamePatternConfig implements ExportableConfig {

    private List<DisplayNamePattern> displayNamePatterns = new LinkedList<DisplayNamePattern>();

    public List<DisplayNamePattern> getDisplayNamePatterns() {
        return displayNamePatterns;
    }

    public DisplayNamePatternConfig(List<DisplayNamePattern> displayNamePatterns) {
        this.displayNamePatterns = displayNamePatterns;
    }

    public DisplayNamePatternConfig() {
    }

    public void setDisplayNamePatterns(List<DisplayNamePattern> displayNamePatterns) {
        this.displayNamePatterns = displayNamePatterns;
    }

    //the readResolve method allows us to handle migrations where we add fields which need to
    //be initialised - xstream sets the fields null even if a default is
    //assigned when the field is defined
    private Object readResolve() {
        if (displayNamePatterns == null) {
            displayNamePatterns = new LinkedList<DisplayNamePattern>();
        }
        return this;
    }

    public String getTitle() {
        return "DisplayNamePatterns";
    }

    public void setTitle(String title) {
        //there's only one instance of this config
        //so we can't change the title
        throw new UnsupportedOperationException("Cannot change title for DisplayNamePatternConfig");
    }
}
