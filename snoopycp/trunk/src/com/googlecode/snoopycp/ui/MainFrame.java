/**
 * Copyright 2011 Snoopy Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * View2.java
 *
 * Created on 15.05.2011, 19:21:33
 */
package com.googlecode.snoopycp.ui;

import com.googlecode.snoopycp.controller.Coordinator;
import com.googlecode.snoopycp.controller.DomainController;
import com.googlecode.snoopycp.model.GraphModel;
import com.googlecode.snoopycp.model.TreeModel;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;
import org.apache.log4j.Logger;

public class MainFrame extends javax.swing.JFrame implements Observer {

    public static Logger logger = Logger.getLogger(MainFrame.class);
    private DomainController controller;
    private TreeModel treeModel;
    private GraphModel graphModel;
    private VisualizationViewer<String, String> visualizationViewer;
    private Layout<String, String> layout;
    private netMapIFrame nmif = new netMapIFrame();

    
    private Coordinator coordinator;
    //private JPopupMenu popup;
    //private JMenuItem mi;

    /** Creates new form MainFrame */
    public MainFrame() {
        initComponents();
        this.setLocationRelativeTo(null);
        //this.goodLook();
    }

    public MainFrame(DomainController _controller) {
        this();

        this.controller = _controller;

        this.treeModel = controller.createTreeModel();
        this.graphModel = controller.createGraphModel();

        this.tree.setModel(treeModel);
        this.tree.setCellRenderer(new IconTreeCellRenderer());

        this.layout = new FRLayout<String, String>(graphModel.graph(), new Dimension(200, 200));
        this.visualizationViewer = new VisualizationViewer<String, String>(layout);
        this.visualizationViewer.getRenderContext().setVertexLabelTransformer(controller.createLabelTransformer());
        this.visualizationViewer.getRenderContext().setVertexFillPaintTransformer(controller.createFillTransformer());

        visualizationViewer.setPreferredSize(new Dimension(50, 100));
        visualizationViewer.setBackground(Color.WHITE);
        nmif.add(visualizationViewer);

        update(null, null);

        pack();
    }

    /**
     * Set icons for menu, set nice names
     */
    private void goodLook() {
        //this.menuItemExit.setIcon(getImageIcon("door-open.png"));
        //this.btnNotepad.setIcon(getImageIcon("document.png"));
    }

    public void addInternalFrame(JInternalFrame _frame) {
        this.jdp.add(_frame);
        _frame.setLocation(centralPosition(_frame));
        _frame.setVisible(true);
    }

    public void setCoordinator(Coordinator _coordinator) {
        coordinator = _coordinator;
    }

    public void setActionsOnPopup(Map<String, ActionListener> _actions) {
        treeModel.setPopupMenu(this.tree, _actions);
    }

    public javax.swing.JTree getTree() {
        return this.tree;
    }

    public String showInputDialog() {
        return JOptionPane.showInputDialog(this, "Enter parameters for module:");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar = new javax.swing.JToolBar();
        btnNotepad = new javax.swing.JButton();
        btnNetMap = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        jsp = new javax.swing.JSplitPane();
        jdp = new javax.swing.JDesktopPane();
        jscp = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        jMenuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        menuItemExit = new javax.swing.JMenuItem();
        menuNetwork = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jToolBar.setRollover(true);

        btnNotepad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/googlecode/snoopycp/share/script--plus.png"))); // NOI18N
        btnNotepad.setFocusable(false);
        btnNotepad.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNotepad.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNotepad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNotepadActionPerformed(evt);
            }
        });
        jToolBar.add(btnNotepad);

        btnNetMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/googlecode/snoopycp/share/node-select-child.png"))); // NOI18N
        btnNetMap.setFocusable(false);
        btnNetMap.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNetMap.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNetMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNetMapActionPerformed(evt);
            }
        });
        jToolBar.add(btnNetMap);

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/googlecode/snoopycp/share/refresh.png"))); // NOI18N
        btnRefresh.setFocusable(false);
        btnRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        jToolBar.add(btnRefresh);

        jsp.setDividerLocation(180);
        jsp.setRightComponent(jdp);

        jscp.setViewportView(tree);

        jsp.setLeftComponent(jscp);

        menuFile.setText("File");

        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/googlecode/snoopycp/share/script--plus.png"))); // NOI18N
        jMenuItem3.setText("Notepad");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        menuFile.add(jMenuItem3);

        menuItemExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/googlecode/snoopycp/share/door-open.png"))); // NOI18N
        menuItemExit.setText("Exit");
        menuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemExitActionPerformed(evt);
            }
        });
        menuFile.add(menuItemExit);

        jMenuBar.add(menuFile);

        menuNetwork.setText("Network");

        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/googlecode/snoopycp/share/node-select-child.png"))); // NOI18N
        jMenuItem1.setText("view full map");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        menuNetwork.add(jMenuItem1);

        jMenuBar.add(menuNetwork);

        menuHelp.setText("Help");

        menuItemAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/googlecode/snoopycp/share/home.png"))); // NOI18N
        menuItemAbout.setText("About");
        menuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAboutActionPerformed(evt);
            }
        });
        menuHelp.add(menuItemAbout);

        jMenuBar.add(menuHelp);

        setJMenuBar(jMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 849, Short.MAX_VALUE)
            .addComponent(jsp, javax.swing.GroupLayout.DEFAULT_SIZE, 849, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jsp, javax.swing.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemAboutActionPerformed
        AboutDialog ad = new AboutDialog(this, true);
        ad.setVisible(true);
    }//GEN-LAST:event_menuItemAboutActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // FIXME need call from Coordinator
        //nmif = new netMapIFrame();
        if (!nmif.isShowing()) {
            nmif.setClosable(true);
            nmif.setResizable(true);
            nmif.setSize(400, 400);
            nmif.setLocation(centralPosition(nmif));
            this.jdp.add(nmif);
            //this.menuNetwork.setEnabled(false);
            nmif.show();
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void menuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_menuItemExitActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        this.addInternalFrame(new NotepadInternalFrame(coordinator.domain()));
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void btnNotepadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNotepadActionPerformed
        this.jMenuItem3ActionPerformed(evt);
    }//GEN-LAST:event_btnNotepadActionPerformed

    private void btnNetMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNetMapActionPerformed
        this.jMenuItem1ActionPerformed(evt);
    }//GEN-LAST:event_btnNetMapActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        this.update(null, null);
    }//GEN-LAST:event_btnRefreshActionPerformed
    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNetMap;
    private javax.swing.JButton btnNotepad;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JToolBar jToolBar;
    private javax.swing.JDesktopPane jdp;
    private javax.swing.JScrollPane jscp;
    private javax.swing.JSplitPane jsp;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuItemAbout;
    private javax.swing.JMenuItem menuItemExit;
    private javax.swing.JMenu menuNetwork;
    private javax.swing.JTree tree;
    // End of variables declaration//GEN-END:variables

    @Override
    public void update(Observable o, Object o1) {
        logger.debug("update ui");

        // TODO Save expand status before update and restore it after
        //Enumeration<TreePath> paths = treeModel.getExpandedNodes(tree);
        Enumeration<TreePath> selPaths = null;
        TreePath path = null;
        if (tree.getSelectionPath() != null) {
            path = tree.getSelectionPath().getParentPath();
//            selPaths = tree.getExpandedDescendants(path);
        }
        treeModel.update(this.tree);
        tree.updateUI();
        //treeModel.setExpandedNodes(tree, paths);
        if (path != null) {
            tree.expandPath(path);
        }

        graphModel.update();
        visualizationViewer.updateUI();
        nmif.updatePanel();

        this.setPreferredSize(this.getSize());
        pack();
    }

//    private ImageIcon getImageIcon(String _iconName) {
//        return new ImageIcon(getClass().getResource(Defaults.PATH_TO_SHARE + _iconName));
//    }
    public Point centralPosition(JInternalFrame _frame) {
        int x = (this.jdp.getSize().width / 2) - (_frame.getSize().width / 2);
        int y = (this.jdp.getSize().height / 2) - (_frame.getSize().height / 2);
        return new Point(x, y);
    }
}
