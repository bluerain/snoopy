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
 * TestInternalFrame.java
 *
 * Created on 18.05.2011, 15:49:54
 */
package com.googlecode.snoopycp.ui;

/**
 *
 * @author spiff
 */
import com.googlecode.snoopycp.core.Domain;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import org.apache.log4j.Logger;

public class NotepadInternalFrame extends javax.swing.JInternalFrame {

    private Logger logger = Logger.getLogger(NotepadInternalFrame.class);
    private JTextArea editArea;
    private JFileChooser fileChooser = new JFileChooser();
    //... Create actions for menu items, buttons, ...
    private Action openAction = new OpenAction();
    private Action saveAction = new SaveAction();
    private Action exitAction = new ExitAction(this);
    private Domain domain;

    //============================================================== constructor
    public NotepadInternalFrame(Domain _domain) {
        initComponents();
        domain = _domain;
        //... Create scrollable text area.
        editArea = new JTextArea(15, 80);
        editArea.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        editArea.setFont(new Font("monospaced", Font.PLAIN, 14));
        //System.out.println("editArea are configured");
        JScrollPane scrollingText = new JScrollPane(editArea);
        //System.out.println("editArea added to scrollpane");
        //this.jScrollPane1.setViewportView(editArea);

        //-- Create a content pane, set layout, add component.
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.add(scrollingText, BorderLayout.CENTER);

        //... Create menubar
        this.menuItemOpen.setAction(openAction);
        this.menuItemSave.setAction(saveAction);
        this.menuItemClose.setAction(exitAction);

        //... Set window content and menu.
        setContentPane(content);

        //... Set other window characteristics.
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Notepad");
        pack();
        //setLocationRelativeTo(null);
        setClosable(true);
        setResizable(true);
        setMaximizable(true);
    }

    public void deploy(Object[] hosts, boolean _activate, String _schedule) {
        String hos = null;
        try {
            for (Object host : hosts) {
                hos = (String) host;
                Ice.Identity ident = domain.enviroment().get(hos);
                String muid = java.util.UUID.randomUUID().toString();
                domain.moduler(ident).ice_ping();
                domain.moduler(ident).deploy(muid, editArea.getText());
                String[] scheduleArray = _schedule.split(";");
                int[] delay = new int[scheduleArray.length];
                long[] delays = new long[scheduleArray.length];
                for (int i = 0; i < scheduleArray.length; i++) {
                    delay[i] = Integer.parseInt(scheduleArray[i]);
                    delays[i] = (long) delay[i] * 1000;
                }
                domain.scheduler(ident).ice_ping();
                domain.scheduler(ident).schedule(muid, delays, null);
                if (!_activate) {
                    domain.scheduler(ident).toogle(muid);
                }
                domain.updateModules(ident);
            }
        } catch (Ice.ConnectionRefusedException e) {
            logger.warn("Problem with deploy on remote node " + hos + e.getMessage());
        }
    }

    ////////////////////////////////////////////////// inner class OpenAction
    class OpenAction extends AbstractAction {
        //============================================= constructor

        public OpenAction() {
            super("Open...");
            putValue(MNEMONIC_KEY, new Integer('O'));
        }

        //========================================= actionPerformed
        @Override
        public void actionPerformed(ActionEvent e) {
            int retval = fileChooser.showOpenDialog(NotepadInternalFrame.this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File f = fileChooser.getSelectedFile();
                try {
                    FileReader reader = new FileReader(f);
                    editArea.read(reader, "");  // Use TextComponent read
                } catch (IOException ioex) {
                    System.out.println(e);
                    System.exit(1);
                }
            }
        }
    }

    //////////////////////////////////////////////////// inner class SaveAction
    class SaveAction extends AbstractAction {
        //============================================= constructor

        SaveAction() {
            super("Save...");
            putValue(MNEMONIC_KEY, new Integer('S'));
        }

        //========================================= actionPerformed
        @Override
        public void actionPerformed(ActionEvent e) {
            int retval = fileChooser.showSaveDialog(NotepadInternalFrame.this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File f = fileChooser.getSelectedFile();
                try {
                    FileWriter writer = new FileWriter(f);
                    editArea.write(writer);  // Use TextComponent write
                } catch (IOException ioex) {
                    JOptionPane.showMessageDialog(NotepadInternalFrame.this, ioex);
                    //System.exit(1);
                }
            }
        }
    }

    ///////////////////////////////////////////////////// inner class ExitAction
    class ExitAction extends AbstractAction {

        JInternalFrame frame;
        //============================================= constructor

        private ExitAction(JInternalFrame aThis) {
            super("Exit");
            frame = aThis;
            putValue(MNEMONIC_KEY, new Integer('X'));
        }

        //========================================= actionPerformed
        @Override
        public void actionPerformed(ActionEvent e) {
            frame.dispose();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuItemOpen = new javax.swing.JMenuItem();
        menuItemSave = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        menuItemDeploy = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuItemClose = new javax.swing.JMenuItem();

        menuFile.setText("File");

        menuItemOpen.setText("Open");
        menuFile.add(menuItemOpen);

        menuItemSave.setText("Save");
        menuFile.add(menuItemSave);
        menuFile.add(jSeparator2);

        menuItemDeploy.setText("Deploy");
        menuItemDeploy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemDeployActionPerformed(evt);
            }
        });
        menuFile.add(menuItemDeploy);
        menuFile.add(jSeparator1);

        menuItemClose.setText("Exit");
        menuFile.add(menuItemClose);

        menuBar.add(menuFile);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuItemDeployActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemDeployActionPerformed
        ChooseModuleInternalFrame cmif = new ChooseModuleInternalFrame(domain, this);
        this.getParent().add(cmif);
        cmif.show();
    }//GEN-LAST:event_menuItemDeployActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenuItem menuItemClose;
    private javax.swing.JMenuItem menuItemDeploy;
    private javax.swing.JMenuItem menuItemOpen;
    private javax.swing.JMenuItem menuItemSave;
    // End of variables declaration//GEN-END:variables
}