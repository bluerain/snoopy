/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.listener;

import com.googlecode.snoopycp.core.Domain;
import com.googlecode.snoopycp.model.Node;
import com.googlecode.snoopycp.ui.MainFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Leo
 */
public class NodeForceStartAL implements ActionListener {

        private MainFrame view;
        private Domain domain;

        public NodeForceStartAL(MainFrame view, Domain _domain) {
            this.view = view;
            this.domain = _domain;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DefaultMutableTreeNode lastSelectNode = (DefaultMutableTreeNode) view.getTree().getLastSelectedPathComponent();
            Node node = (Node) lastSelectNode.getUserObject();
            this.domain.moduler(node.identity).force(node.muid, this.view.showInputDialog().split(";"));

        }
    }
