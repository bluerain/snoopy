/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.listener;

import com.googlecode.snoopycp.core.Domain;
import com.googlecode.snoopycp.model.Node;
import com.googlecode.snoopycp.ui.MainFrame;
import com.googlecode.snoopycp.ui.NodePropertiesInternalFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Leo
 */
 public class NodePropertiesAL implements ActionListener {

        private MainFrame view;
        private Domain domain;

        public NodePropertiesAL(MainFrame view, Domain _domain) {
            this.view = view;
            this.domain = _domain;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DefaultMutableTreeNode lastSelectNode = (DefaultMutableTreeNode) view.getTree().getLastSelectedPathComponent();
            Node node = (Node) lastSelectNode.getUserObject();
            //System.out.println();
            HashMap<String, String> map = (HashMap<String, String>) domain.hoster(node.identity).context();
            map.put("IP", domain.cache(node.identity).get("primary").split(" ")[2]);
            view.addInternalFrame(new NodePropertiesInternalFrame(map, node.name, this.domain.configurer(node.identity)));
            //System.out.println(domain.configurer(node.identity).configuration().get("connectionstring"));
        }
    }
