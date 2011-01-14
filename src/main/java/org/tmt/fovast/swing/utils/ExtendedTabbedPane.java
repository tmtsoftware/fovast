/*
 *  Copyright 2011 TMT.
 *
 *  License and source copyright header text to be decided
 *
 */
package org.tmt.fovast.swing.utils;

import java.awt.Component;
import javax.swing.JTabbedPane;

/**
 * Extends JTabbedPane to show a close button on tabs.
 * 
 * @author vivekananda_moosani
 */
public abstract class ExtendedTabbedPane extends JTabbedPane {

    /**
     * Called when close button on tab is pressed
     * 
     * @param index
     */
    void extendedRemoveTabAt(int index) {
        if (removeTabOrNot(index)) {
            removeTabAt(index);
        }
    }

    /**
     * Method to be overridden with logic to show confirmation dialogs
     * when closing the tab.
     * 
     * @param index of the tab
     * @return returning false will not close the tab.
     */
    protected abstract boolean removeTabOrNot(int index);

    /**
     * Overrides addTab method to add TabComponent which shows a close button
     * to the Tab header
     * 
     * @param title
     * @param component
     */
    @Override
    public void addTab(String title, Component component) {
        super.addTab(title, component);
        this.setTabComponentAt(this.getTabCount() - 1,
                new ButtonTabComponent(this));
    }

}
