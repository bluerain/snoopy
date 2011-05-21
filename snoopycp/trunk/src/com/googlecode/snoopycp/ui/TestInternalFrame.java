/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

    /** Creates new form TestInternalFrame */
//    public TestInternalFrame() {
//        initComponents();
//    }
private JTextArea    _editArea;
    private JFileChooser _fileChooser = new JFileChooser();
    
    //... Create actions for menu items, buttons, ...
    private Action _openAction = new OpenAction();
    private Action _saveAction = new SaveAction();
    private Action _exitAction = new ExitAction(this); 
    
    //===================================================================== main
//    public static void main(String[] args) {
//        //new NutPad();
//    }
    
    //============================================================== constructor
    public TestInternalFrame() {
        //... Create scrollable text area.
        _editArea = new JTextArea(15, 80);
        _editArea.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        _editArea.setFont(new Font("monospaced", Font.PLAIN, 14));
        JScrollPane scrollingText = new JScrollPane(_editArea);
        
        //-- Create a content pane, set layout, add component.
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.add(scrollingText, BorderLayout.CENTER);
        
        //... Create menubar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = menuBar.add(new JMenu("File"));
        fileMenu.setMnemonic('F');
        fileMenu.add(_openAction);       // Note use of actions, not text.
        fileMenu.add(_saveAction);
        fileMenu.addSeparator(); 
        fileMenu.add(_exitAction);
        
        //... Set window content and menu.
        setContentPane(content);
        setJMenuBar(menuBar);
        
        //... Set other window characteristics.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
            int retval = _fileChooser.showOpenDialog(TestInternalFrame.this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File f = _fileChooser.getSelectedFile();
                try {
                    FileReader reader = new FileReader(f);
                    _editArea.read(reader, "");  // Use TextComponent read
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
            int retval = _fileChooser.showSaveDialog(TestInternalFrame.this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File f = _fileChooser.getSelectedFile();
                try {
                    FileWriter writer = new FileWriter(f);
                    _editArea.write(writer);  // Use TextComponent write
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 394, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 274, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
// File   : editor/NutPad.java -- A very simple text editor

// Purpose: Illustrates use of AbstractActions for menus.
//          It only uses a few Action features.  Many more are available.
//          This program uses the obscure "read" and "write"
//               text component methods.
// Author : Fred Swartz - 2006-12-14 - Placed in public domain.


///////////////////////////////////////////////////////////////////////// NutPad
//public class NutPad extends JFrame {
//    //... Components 
//    
//}