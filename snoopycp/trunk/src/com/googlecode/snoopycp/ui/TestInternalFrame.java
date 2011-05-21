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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

public class TestInternalFrame extends javax.swing.JInternalFrame {

    private JTextArea editArea;
    private JFileChooser fileChooser = new JFileChooser();
    //... Create actions for menu items, buttons, ...
    private Action openAction = new OpenAction();
    private Action saveAction = new SaveAction();
    private Action exitAction = new ExitAction(this);

    //============================================================== constructor
    public TestInternalFrame() {
        initComponents();
        //... Create scrollable text area.
        editArea = new JTextArea(15, 80);
        editArea.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        editArea.setFont(new Font("monospaced", Font.PLAIN, 14));
        JScrollPane scrollingText = new JScrollPane(editArea);

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
        setTitle("NutPad");
        pack();
        //setLocationRelativeTo(null);
        setClosable(true);
        setResizable(true);
        setMaximizable(true);
        setVisible(true);
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
            int retval = fileChooser.showOpenDialog(TestInternalFrame.this);
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
        public void actionPerformed(ActionEvent e) {
            int retval = fileChooser.showSaveDialog(TestInternalFrame.this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File f = fileChooser.getSelectedFile();
                try {
                    FileWriter writer = new FileWriter(f);
                    editArea.write(writer);  // Use TextComponent write
                } catch (IOException ioex) {
                    JOptionPane.showMessageDialog(TestInternalFrame.this, ioex);
                    System.exit(1);
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

        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuItemOpen = new javax.swing.JMenuItem();
        menuItemSave = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuItemClose = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();

        menuFile.setText("File");

        menuItemOpen.setText("Open");
        menuFile.add(menuItemOpen);

        menuItemSave.setText("Save");
        menuFile.add(menuItemSave);
        menuFile.add(jSeparator1);

        menuItemClose.setText("Exit");
        menuFile.add(menuItemClose);

        menuBar.add(menuFile);

        jMenu1.setText("Deploy");
        menuBar.add(jMenu1);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 394, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 253, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenuItem menuItemClose;
    private javax.swing.JMenuItem menuItemOpen;
    private javax.swing.JMenuItem menuItemSave;
    // End of variables declaration//GEN-END:variables
}