/* %%
 * 
 * JEvolve
 *
 * Copyright 2006 Jeff Ridder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ridderware.jevolve.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;

/**
 * A MDI UI used to visualize the progress of evolution for the jevolve package
 * <p>
 * TODO:
 * Put this guy behind an interface, allow user to specify colors, graph bounds,
 * axis ranges, axis labels, series labels, etc.
 *
 * @author jhanduber
 */
public class EvolutionUI
    extends javax.swing.JFrame
{
    private final UIFactoryOptions generalOptions;

    private final Map<String, GenerationalVisualization> windowName2Window =
        new HashMap<String, GenerationalVisualization>();

    private final JDesktopPane desktop = new JDesktopPane();

    private enum FrameArrangements
    {
        TILE_HORIZONTAL, TILE_VERTICAL, ARRANGE, CASCADE
    };

    /**
     * Creates the UI in a manner such that it will create a seperate window
     * display fitness progression per fitness term relative to the generation.
     */
    public EvolutionUI(UIFactoryOptions generalOptions)
    {
        this.generalOptions = generalOptions;

        initComponents();
        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
        setContentPane(desktop);
        initMyComponents();
        Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
        s.setSize(s.getWidth() * 0.8, s.getHeight() * 0.8);
        this.setPreferredSize(s);
        pack();
        this.tileFrames(FrameArrangements.ARRANGE);
    }

    public void saveViews()
    {
        for (GenerationalVisualization viz : this.windowName2Window.values())
        {
            viz.handleClose();
        }
    }

    private void initMyComponents()
    {
        int fitnessIndex = 0;
        for (String fitnessDesc : generalOptions.getFitnessDescriptions())
        {
            createGenerationalVizLineWindow(fitnessDesc, fitnessIndex++);
        }

        if (generalOptions.getDisplayConstraintError())
        {
            createGenerationalVizLineWindow("Constraint Error", -1);
        }

        if (generalOptions.getFitnessDescriptions().length > 1)
        {
            createParetoVizWindow("Current Pareto Front",
                Pareto2Visualization.ParetoMemory.DISPLAY_CURRENT_FRONT,
                generalOptions.getFitnessDescriptions());
        }

        Collections.unmodifiableMap(windowName2Window);
    }

    private void createParetoVizWindow(
        String title,
        Pareto2Visualization.ParetoMemory paretoMemory,
        String... fitnessDescriptions)
    {
        JMenuItem menu = new JMenuItem();
        menu.setText("Pareto");
        viewMenu.add(menu);

        final Pareto2Visualization frame = new Pareto2Visualization(
            1,//max num pareto ranks to show
            generalOptions.getParetoDrawingFrequency(),//drawing frequency
            this.generalOptions.getPopulationSources(),
            paretoMemory,
            title,
            generalOptions.getVisualizationConsumers(),
            fitnessDescriptions);

        frame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);

        menu.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                frame.setVisible(true);
            }
        });

        desktop.add(frame);
        frame.setVisible(true);
    }

    /**
     * Creates a window/view with the given name, source, and fitness index.
     */
    private void createGenerationalVizLineWindow(String name, int fitnessIndex)
    {
        JMenuItem menu = new JMenuItem();
        menu.setText(name);
        viewMenu.add(menu);

        final GenerationalVisualization frame = new GenerationalVisualization(
            this.generalOptions.getPopulationSources(),
            name,
            "Generation",
            name,
            fitnessIndex,
            generalOptions.getGenerationalDrawingFrequency(),
            generalOptions.getMinGraphBounds(),
            generalOptions.getMaxGraphBounds());

        frame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);

        windowName2Window.put(name, frame);

        menu.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                frame.setVisible(true);
            }
        });

        desktop.add(frame);
        frame.setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainMenuBar = new javax.swing.JMenuBar();
        viewMenu = new javax.swing.JMenu();
        windowsMenu = new javax.swing.JMenu();
        arrangeMenuItem = new javax.swing.JMenuItem();
        cascadeMenuItem = new javax.swing.JMenuItem();
        tileHorizonMenuItem = new javax.swing.JMenuItem();
        tileVerticMenuItem = new javax.swing.JMenuItem();
        minimizeAllMenuItem = new javax.swing.JMenuItem();
        restoreAllMenuItem = new javax.swing.JMenuItem();
        closeAllMenuItem = new javax.swing.JMenuItem();

        setTitle("JEvolve");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        viewMenu.setText("View");
        mainMenuBar.add(viewMenu);

        windowsMenu.setText("Windows");

        arrangeMenuItem.setText("Arrange");
        arrangeMenuItem.setToolTipText("Arranges all windows so they fully take up all space");
        arrangeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                arrangeMenuItemActionPerformed(evt);
            }
        });
        windowsMenu.add(arrangeMenuItem);

        cascadeMenuItem.setText("Cascade");
        cascadeMenuItem.setToolTipText("Cascades all windows");
        cascadeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cascadeMenuItemActionPerformed(evt);
            }
        });
        windowsMenu.add(cascadeMenuItem);

        tileHorizonMenuItem.setText("Tile Horizontally");
        tileHorizonMenuItem.setToolTipText("Tiles all windows horizontally");
        tileHorizonMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tileHorizonMenuItemActionPerformed(evt);
            }
        });
        windowsMenu.add(tileHorizonMenuItem);

        tileVerticMenuItem.setText("Tile Vertically");
        tileVerticMenuItem.setToolTipText("Tiles all windows vertically");
        tileVerticMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tileVerticMenuItemActionPerformed(evt);
            }
        });
        windowsMenu.add(tileVerticMenuItem);

        minimizeAllMenuItem.setText("Minimize All Windows");
        minimizeAllMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minimizeAllMenuItemActionPerformed(evt);
            }
        });
        windowsMenu.add(minimizeAllMenuItem);

        restoreAllMenuItem.setText("Restore All Windows");
        restoreAllMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restoreAllMenuItemActionPerformed(evt);
            }
        });
        windowsMenu.add(restoreAllMenuItem);

        closeAllMenuItem.setText("Close All Windows");
        closeAllMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeAllMenuItemActionPerformed(evt);
            }
        });
        windowsMenu.add(closeAllMenuItem);

        mainMenuBar.add(windowsMenu);

        setJMenuBar(mainMenuBar);
    }// </editor-fold>//GEN-END:initComponents

    private void closeAllMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_closeAllMenuItemActionPerformed
    {//GEN-HEADEREND:event_closeAllMenuItemActionPerformed
        this.closeAllWindows();
    }//GEN-LAST:event_closeAllMenuItemActionPerformed

    private void restoreAllMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_restoreAllMenuItemActionPerformed
    {//GEN-HEADEREND:event_restoreAllMenuItemActionPerformed
        this.restoreAll();
    }//GEN-LAST:event_restoreAllMenuItemActionPerformed

    private void minimizeAllMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_minimizeAllMenuItemActionPerformed
    {//GEN-HEADEREND:event_minimizeAllMenuItemActionPerformed
        this.minimizeWindows();
    }//GEN-LAST:event_minimizeAllMenuItemActionPerformed

    private void tileVerticMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tileVerticMenuItemActionPerformed
    {//GEN-HEADEREND:event_tileVerticMenuItemActionPerformed
        tileFrames(FrameArrangements.TILE_VERTICAL);
    }//GEN-LAST:event_tileVerticMenuItemActionPerformed

    private void arrangeMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_arrangeMenuItemActionPerformed
    {//GEN-HEADEREND:event_arrangeMenuItemActionPerformed
        tileFrames(FrameArrangements.ARRANGE);
    }//GEN-LAST:event_arrangeMenuItemActionPerformed

    private void cascadeMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cascadeMenuItemActionPerformed
    {//GEN-HEADEREND:event_cascadeMenuItemActionPerformed
        tileFrames(FrameArrangements.CASCADE);
    }//GEN-LAST:event_cascadeMenuItemActionPerformed

    private void tileHorizonMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tileHorizonMenuItemActionPerformed
    {//GEN-HEADEREND:event_tileHorizonMenuItemActionPerformed
        tileFrames(FrameArrangements.TILE_HORIZONTAL);
    }//GEN-LAST:event_tileHorizonMenuItemActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
        // TODO add your handling code here:
        for (GenerationalVisualization viz : this.windowName2Window.values())
        {
            viz.handleClose();
        }

        if (generalOptions.getExitJVMonClose())
        {
            System.exit(0);
        }

    }//GEN-LAST:event_formWindowClosing

    /**
     * Tiles all the windows in the desktop according to the given style.
     *
     * @param style
     */
    public void tileFrames(FrameArrangements style)
    {
        Dimension deskDim = desktop.getSize();
        int deskWidth = deskDim.width;
        int deskHeight = deskDim.height;
        JInternalFrame[] frames = desktop.getAllFrames();
        int frameCount = frames.length;
        int frameWidth = 0;
        int frameHeight = 0;
        int xpos = 0;
        int ypos = 0;
        double scale = 0.6;
        int spacer = 30;
        int frameCounter = 0;
        Vector frameVec = new Vector(1, 1);
        boolean areIcons = false;
        int tempy = 0, tempx = 0;
        for (int i = 0; i < frameCount; i++)
        {

            // Only layout frames that are visible, arent icons and
            // aren't resizble. Non resizable frames should be left alone as
            // they will always be centred and generally take little screen
            // room and get in the way of the frames the user wants to see
            if (frames[i].isVisible() && !frames[i].isIcon() &&
                frames[i].isResizable())
            {
                frameVec.addElement(frames[i]);
                frameCounter++;
            }
            else if (frames[i].isIcon())
            {
                areIcons = true;
            }
        }
        if (areIcons)
        {
            deskHeight = deskHeight - 50;
        }
        switch (style)
        {
            case TILE_HORIZONTAL:
                for (int i = 0; i < frameCounter; i++)
                {
                    JInternalFrame temp = (JInternalFrame) frameVec.elementAt(i);
                    frameWidth = deskWidth;
                    frameHeight = (int) (deskHeight / frameCounter);
                    temp.reshape(xpos, ypos, frameWidth, frameHeight);
                    ypos = ypos + frameHeight;
                    temp.moveToFront();
                }
                break;

            case TILE_VERTICAL:
                for (int i = 0; i < frameCounter; i++)
                {
                    JInternalFrame temp = (JInternalFrame) frameVec.elementAt(i);
                    frameWidth = (int) (deskWidth / frameCounter);
                    frameHeight = deskHeight;
                    if (temp.isResizable())
                    {
                        temp.reshape(xpos, ypos, frameWidth, frameHeight);
                    }
                    else
                    {
                        temp.setLocation(xpos, ypos);
                    }
                    xpos = xpos + frameWidth;
                    temp.moveToFront();
                }
                break;
            case CASCADE:
                for (int i = 0; i < frameCounter; i++)
                {
                    JInternalFrame temp = (JInternalFrame) frameVec.elementAt(i);
                    frameWidth = (int) (deskWidth * scale);
                    frameHeight = (int) (deskHeight * scale);
                    if (temp.isResizable())
                    {
                        temp.reshape(xpos, ypos, frameWidth, frameHeight);
                    }
                    else
                    {
                        temp.setLocation(xpos, ypos);
                    }
                    temp.moveToFront();
                    xpos = xpos + spacer;
                    ypos = ypos + spacer;
                    if ((xpos + frameWidth > deskWidth) || (ypos + frameHeight >
                        deskHeight - 50))
                    {
                        xpos = 0;
                        ypos = 0;
                    }
                }
                break;
            case ARRANGE:
                int row = new Long(Math.round(Math.sqrt(
                    new Integer(frameCounter).doubleValue()))).intValue();
                if (row == 0)
                {
                    break;
                }
                int col = frameCounter / row;
                if (col == 0)
                {
                    break;
                }
                int rem = frameCounter % row;
                int rowCount = 1;
                frameWidth = (int) deskWidth / col;
                frameHeight = (int) deskHeight / row;
                for (int i = 0; i < frameCounter; i++)
                {
                    JInternalFrame temp = (JInternalFrame) frameVec.elementAt(i);
                    if (rowCount <= row - rem)
                    {
                        if (temp.isResizable())
                        {
                            temp.reshape(xpos, ypos, frameWidth, frameHeight);
                        }
                        else
                        {
                            temp.setLocation(xpos, ypos);
                        }
                        if (xpos + 10 < deskWidth - frameWidth)
                        {
                            xpos = xpos + frameWidth;
                        }
                        else
                        {
                            ypos = ypos + frameHeight;
                            xpos = 0;
                            rowCount++;
                        }
                    }
                    else
                    {
                        frameWidth = (int) deskWidth / (col + 1);
                        if (temp.isResizable())
                        {
                            temp.reshape(xpos, ypos, frameWidth, frameHeight);
                        }
                        else
                        {
                            temp.setLocation(xpos, ypos);
                        }
                        if (xpos + 10 < deskWidth - frameWidth)
                        {
                            xpos = xpos + frameWidth;
                        }
                        else
                        {
                            ypos = ypos + frameHeight;
                            xpos = 0;
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * Minimises all windows that are iconifiable.
     */
    public void minimizeWindows()
    {
        JInternalFrame[] openWindows = desktop.getAllFrames();
        for (int i = 0; i < openWindows.length; i++)
        {
            if (openWindows[i].isIconifiable())
            {
                try
                {
                    openWindows[i].setIcon(true);
                }
                catch (java.beans.PropertyVetoException pve)
                {
                    pve.printStackTrace();
                }
            }
        }
    }

    /**
     * Restores all minimised windows.
     */
    public void restoreAll()
    {
        JInternalFrame[] openWindows = desktop.getAllFrames();
        for (int i = 0; i < openWindows.length; i++)
        {
            if (openWindows[i].isIcon())
            {
                try
                {
                    openWindows[i].setIcon(false);
                }
                catch (java.beans.PropertyVetoException pve)
                {
                    pve.printStackTrace();
                }
            }
        }
    }

    /**
     * Closes all open windows.
     */
    public void closeAllWindows()
    {
        JInternalFrame[] openWindows = desktop.getAllFrames();
        for (int i = 0; i < openWindows.length; i++)
        {
            openWindows[i].hide();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem arrangeMenuItem;
    private javax.swing.JMenuItem cascadeMenuItem;
    private javax.swing.JMenuItem closeAllMenuItem;
    private javax.swing.JMenuBar mainMenuBar;
    private javax.swing.JMenuItem minimizeAllMenuItem;
    private javax.swing.JMenuItem restoreAllMenuItem;
    private javax.swing.JMenuItem tileHorizonMenuItem;
    private javax.swing.JMenuItem tileVerticMenuItem;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JMenu windowsMenu;
    // End of variables declaration//GEN-END:variables
}
