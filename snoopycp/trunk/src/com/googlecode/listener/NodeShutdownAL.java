/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.listener;

import com.googlecode.snoopycp.core.Domain;
import com.googlecode.snoopycp.model.Node;
import com.googlecode.snoopycp.ui.MainFrame;
import com.googlecode.snoopycp.util.Identities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Leo
 */
public class NodeShutdownAL implements ActionListener {

        private MainFrame view;
        private Domain domain;

        public NodeShutdownAL(MainFrame view, Domain _domain) {
            this.view = view;
            this.domain = _domain;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DefaultMutableTreeNode lastSelectNode = (DefaultMutableTreeNode) view.getTree().getLastSelectedPathComponent();
            Node node = (Node) lastSelectNode.getUserObject();
            domain.controller(node.identity).shutdown();
            for (String host : domain.hosts()) {
                if (Identities.equals(domain.enviroment().get(host), node.identity)) {
                    domain.removeHost(host);
                }
            }
            domain.notifyObserver();
        }
    }
