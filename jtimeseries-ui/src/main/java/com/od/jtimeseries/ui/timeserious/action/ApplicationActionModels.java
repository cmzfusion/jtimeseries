package com.od.jtimeseries.ui.timeserious.action;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 08-Dec-2010
 * <p/>
 * To change this template use File | Settings | File Templates.
 */
public class ApplicationActionModels {

    private DesktopSelectionActionModel desktopSelectionActionModel = new DesktopSelectionActionModel();
    private VisualizerSelectionActionModel visualizerSelectionActionModel = new VisualizerSelectionActionModel();

    public DesktopSelectionActionModel getDesktopSelectionActionModel() {
        return desktopSelectionActionModel;
    }

    public void setDesktopSelectionActionModel(DesktopSelectionActionModel desktopSelectionActionModel) {
        this.desktopSelectionActionModel = desktopSelectionActionModel;
    }

    public VisualizerSelectionActionModel getVisualizerSelectionActionModel() {
        return visualizerSelectionActionModel;
    }

    public void setVisualizerSelectionActionModel(VisualizerSelectionActionModel visualizerSelectionActionModel) {
        this.visualizerSelectionActionModel = visualizerSelectionActionModel;
    }
}
