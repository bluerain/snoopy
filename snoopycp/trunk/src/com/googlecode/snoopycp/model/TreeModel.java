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
package com.googlecode.snoopycp.model;

import com.googlecode.snoopycp.core.Domain;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class TreeModel extends DefaultTreeModel implements javax.swing.tree.TreeModel {

    private Domain domain;  // container of all
    private JPopupMenu popupNode;
    private JPopupMenu popupModule;
    private JPopupMenu popupDomen;
    private JMenuItem mi;

    public TreeModel(Domain domain) {
        super(new DefaultMutableTreeNode(domain.name())); // call constructor of superclass
        this.domain = domain;
        initPopup(); // build popup menu and bind actions
    }

    public void update() {
        DefaultMutableTreeNode domainRoot = (DefaultMutableTreeNode) root;
        domainRoot.removeAllChildren(); // Clean tree

        Set<String> hosts = domain.hosts(); // get all host from cache
        String osName = null;
        Node.OsType os;


        for (String host : hosts) {
            Ice.Identity identity = domain.enviroment().get(host);
            osName = domain.osPull().get(identity);
            if (osName.indexOf("Win") != -1) {
                os = Node.OsType.WIN;
            } else if (osName.indexOf("lin") != -1 || osName.indexOf("Lin") != -1) {
                os = Node.OsType.LIN;
            } else {
                os = Node.OsType.UNKNOWN;
            }

            DefaultMutableTreeNode node; 
            if (identity == null) {
                node = new DefaultMutableTreeNode(new Node(identity, host + " [died]", Node.Type.NODE, os), true);
            } else {
                node = new DefaultMutableTreeNode(new Node(identity, host, Node.Type.NODE, os), true);
            }
            domainRoot.add(node); // add node in tree

            // Adding all modules of Node in the tree
            for (String moduleID : domain.moduler(identity).values()) {
                node.add(new DefaultMutableTreeNode(
                        new Node(null, moduleID, Node.Type.MODULE, null), false));
            }
        }
    }

    private JPopupMenu getPopupMenu(Node.Type _type) {
        JPopupMenu popup = popupDomen;
        switch (_type) {
            case MODULE:
                popup = popupModule;
                break;
            case NODE:
                popup = popupNode;
                break;
            case DOMEN:
                popup = popupDomen;
                break;
        }

        return popup;
    }

    public void setPopupMenu(JTree _tree) {
        final JTree tree = _tree;
        tree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int selRow = tree.getRowForLocation(e.getX(), e.getY());
                    if (selRow >= 0) {
                        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                        tree.setSelectionPath(selPath);
                        Object obj = tree.getLastSelectedPathComponent();
                        if (obj != null) {
                            DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj;
                            final Object tmp = node.getUserObject();
                            if (tmp instanceof String) {
                            } else {
                                com.googlecode.snoopycp.model.Node userObj = (Node) tmp;
                                final Node.Type popupType = userObj.nodeType;
                                getPopupMenu(popupType).show(e.getComponent(), e.getX(), e.getY());
                            }
                        }
                    }
                }
            }
        });
    }

    private void initPopup() {
        // Init popup menu for Module
        popupModule = new JPopupMenu();
        mi = new JMenuItem("Force start");
        popupModule.add(mi);
        mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Hello");
            }
        });
        mi = new JMenuItem("Properties");
        popupModule.add(mi);
        popupModule.setOpaque(true);
        popupModule.setLightWeightPopupEnabled(true);
        
        // Init popup menu for Node
        popupNode = new JPopupMenu();
        mi = new JMenuItem("Configure");
        popupNode.add(mi);
        mi = new JMenuItem("Stop");
        popupNode.add(new JSeparator(SwingConstants.HORIZONTAL));
        popupNode.add(mi);
        mi = new JMenuItem("Info");
        popupNode.add(mi);
        
        // Init popup menu for Domen (Not showing)
        popupDomen = new JPopupMenu();
        mi = new JMenuItem("Bla-bla");
        popupDomen.add(mi);
        mi = new JMenuItem("Force");
        popupDomen.add(mi);
    }
}
