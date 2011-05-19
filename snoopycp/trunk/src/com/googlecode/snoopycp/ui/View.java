/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * View2.java
 *
 * Created on 15.05.2011, 19:21:33
 */
package com.googlecode.snoopycp.ui;

import com.googlecode.snoopycp.controller.DomainController;
import com.googlecode.snoopycp.model.GraphModel;
import com.googlecode.snoopycp.model.TableModel;
import com.googlecode.snoopycp.model.TreeModel;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;
import org.apache.log4j.Logger;

public class View extends javax.swing.JFrame implements Observer {

    public static Logger logger = Logger.getLogger(View.class);
    private DomainController controller;
    private TableModel tableModel;
    private TreeModel treeModel;
    private GraphModel graphModel;
    private VisualizationViewer<String, String> visualizationViewer;
    private Layout<String, String> layout;
    private netMapIFrame nmif = new netMapIFrame();
    JPopupMenu popup;
    JMenuItem mi;

    /** Creates new form View2 */
    public View() {
        initComponents();
        this.setLocationRelativeTo(null);
        
        
    }

    public View(DomainController controller) {
        this();

        this.controller = controller;

        this.tableModel = controller.createTableModel();
        this.treeModel = controller.createTreeModel();
        this.graphModel = controller.createGraphModel();

        this.tree.setModel(treeModel);
        this.tree.setCellRenderer(new IconTreeCellRenderer());
        treeModel.setPopupMenu(this.tree);
        //this.table.setModel(tableModel);

        this.layout = new FRLayout<String, String>(graphModel.graph(), new Dimension(300, 300));
        this.visualizationViewer = new VisualizationViewer<String, String>(layout);
        this.visualizationViewer.getRenderContext().setVertexLabelTransformer(controller.createLabelTransformer());
        this.visualizationViewer.getRenderContext().setVertexFillPaintTransformer(controller.createFillTransformer());

        //nmif.graphPanel
        nmif.add(visualizationViewer);
        //this.graphPanel.updateUI();

        update(null, null);

        pack();
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
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jsp = new javax.swing.JSplitPane();
        jdp = new javax.swing.JDesktopPane();
        jscp = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        jMenuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuItemExit = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        menuNetwork = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jToolBar.setRollover(true);

        jButton1.setText("jButton1");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar.add(jButton1);

        jButton2.setText("jButton2");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar.add(jButton2);

        jButton3.setText("jButton3");
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar.add(jButton3);

        jsp.setDividerLocation(130);
        jsp.setRightComponent(jdp);

        tree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                treeMouseReleased(evt);
            }
        });
        jscp.setViewportView(tree);

        jsp.setLeftComponent(jscp);

        menuFile.setText("File");

        menuItemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        menuItemExit.setText("Exit");
        menuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemExitActionPerformed(evt);
            }
        });
        menuFile.add(menuItemExit);

        jMenuBar.add(menuFile);

        menuEdit.setText("Edit");

        jMenuItem2.setText("jMenuItem2");
        menuEdit.add(jMenuItem2);

        jMenuBar.add(menuEdit);

        menuNetwork.setText("Network");
        menuNetwork.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuNetworkActionPerformed(evt);
            }
        });

        jMenuItem1.setText("view full map");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        menuNetwork.add(jMenuItem1);

        jMenuBar.add(menuNetwork);

        menuHelp.setText("Help");

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
            .addComponent(jToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE)
            .addComponent(jsp, javax.swing.GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jsp, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemAboutActionPerformed
        AboutDialog ad = new AboutDialog(this, true);
        ad.setVisible(true);
    }//GEN-LAST:event_menuItemAboutActionPerformed

    private void menuNetworkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuNetworkActionPerformed
    }//GEN-LAST:event_menuNetworkActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed

        nmif.setClosable(true);
        nmif.setResizable(true);
        nmif.setSize(400, 150);
        int x = (this.jdp.getSize().width / 2) - (nmif.getSize().width / 2);
        int y = (this.jdp.getSize().height / 2) - (nmif.getSize().height / 2);
        nmif.setLocation(x, y);
        this.jdp.add(nmif);
        nmif.show();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void treeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeMouseReleased
//        JPopupMenu popup;
//        JMenuItem mi;
//        popup = new JPopupMenu();
//        mi = new JMenuItem("Insert a children");
//        //mi.addActionListener(this);
//        mi.setActionCommand("insert");
//        popup.add(mi);
//        mi = new JMenuItem("Remove this node");
//        //mi.addActionListener(this);
//        mi.setActionCommand("remove");
//        popup.add(mi);
//        popup.setOpaque(true);
//        popup.setLightWeightPopupEnabled(true);
//        tree.setComponentPopupMenu(popup);
    }//GEN-LAST:event_treeMouseReleased

    private void menuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_menuItemExitActionPerformed
    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JToolBar jToolBar;
    private javax.swing.JDesktopPane jdp;
    private javax.swing.JScrollPane jscp;
    private javax.swing.JSplitPane jsp;
    private javax.swing.JMenu menuEdit;
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

        treeModel.update();
        tree.updateUI();

        graphModel.update();
        visualizationViewer.updateUI();
        nmif.updatePanel();

        pack();
    }
}
