/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ModulePropertyInternalFrame.java
 *
 * Created on 18.05.2011, 17:19:45
 */
package com.googlecode.snoopycp.ui;

/**
 *
 * @author spiff
 */
public class ModulePropertyInternalFrame extends javax.swing.JInternalFrame {

    /** Creates new form ModulePropertyInternalFrame */
    public ModulePropertyInternalFrame() {
        initComponents();
        this.btnON.setSelected(true);
        this.btnOFF.setSelected(false);
        this.setClosable(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnON = new javax.swing.JToggleButton();
        btnOFF = new javax.swing.JToggleButton();
        jLabel1 = new javax.swing.JLabel();

        btnON.setText("ON");
        btnON.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnONActionPerformed(evt);
            }
        });

        btnOFF.setText("OFF");
        btnOFF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOFFActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Status");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(97, 97, 97)
                .addComponent(btnON, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOFF)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(btnOFF)
                    .addComponent(btnON))
                .addContainerGap(232, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOFFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOFFActionPerformed
        if(this.btnON.isSelected()) {
            this.btnOFF.setSelected(true);
            this.btnON.setSelected(false);
        }
    }//GEN-LAST:event_btnOFFActionPerformed

    private void btnONActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnONActionPerformed
        if(this.btnOFF.isSelected()) {
            this.btnON.setSelected(true);
            this.btnOFF.setSelected(false);
        }
    }//GEN-LAST:event_btnONActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnOFF;
    private javax.swing.JToggleButton btnON;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
