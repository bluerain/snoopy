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

import com.googlecode.snoopycp.Defaults;
import com.googlecode.snoopycp.core.Domain;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
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
    private JMenuItem menuItem;     // template for all popup menu
    private Ice.Identity currentId; // Identity of current selected node
    private TreePath selectedPath;
    private DefaultMutableTreeNode selectedNode;
    private Node userObj;           // User object initilation of node
    private Map<String, ActionListener> actions;

    public TreeModel(Domain domain) {
        super(new DefaultMutableTreeNode(domain.name())); // call constructor of superclass
        this.domain = domain;
        //initPopup(); // build popup menu and bind actions
    }

    public void update() {
        DefaultMutableTreeNode domainRoot = (DefaultMutableTreeNode) root;
        domainRoot.removeAllChildren(); // Clean tree

        Set<String> hosts = domain.hosts(); // get all host from cache
        String osName = null;
        Node.OsType os = Node.OsType.UNKNOWN;


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
            HashMap<String, String> fullKeys = (HashMap) domain.moduleName(identity);
            Set<String> keys = fullKeys.keySet();
            for (String moduleID : keys) {
                node.add(new DefaultMutableTreeNode(
                        new Node(identity, fullKeys.get(moduleID), moduleID, Node.Type.MODULE, domain.moduleStatus(identity)), false));
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

    /**
     * Bind popup menu on _tree on mouse right click
     * @param _tree tree for popup menu binding
     */
    public void setPopupMenu(JTree _tree, Map<String, ActionListener> _actions) {
        final JTree tree = _tree;
        actions = _actions;
        initPopup();
        tree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int selRow = tree.getRowForLocation(e.getX(), e.getY());
                    if (selRow >= 0) {
                        selectedPath = tree.getPathForLocation(e.getX(), e.getY());
                        tree.setSelectionPath(selectedPath);
                        Object obj = tree.getLastSelectedPathComponent();
                        if (obj != null) {
                            selectedNode = (DefaultMutableTreeNode) obj;
                            final Object tmp = selectedNode.getUserObject();
                            if (tmp instanceof String) {
                            } else {
                                userObj = (Node) tmp;
                                final Node.Type popupType = userObj.nodeType;
                                currentId = userObj.identity;
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
        menuItem = new JMenuItem("Force start", getImageIcon("work.png"));
        menuItem.addActionListener(actions.get("ForceStart"));
        popupModule.add(menuItem);
        menuItem = new JMenuItem("Properties", getImageIcon("property.png"));
        menuItem.addActionListener(actions.get("ModuleProperties"));
        popupModule.add(menuItem);
        popupModule.setOpaque(true);
        popupModule.setLightWeightPopupEnabled(true);

        // Init popup menu for Node
        popupNode = new JPopupMenu();
        menuItem = new JMenuItem("Configure", getImageIcon("gear.png"));
        menuItem.addActionListener(actions.get("Configure"));
        popupNode.add(menuItem);
        menuItem = new JMenuItem("Shutdown", getImageIcon("slash.png"));
        menuItem.addActionListener(actions.get("Shutdown"));
        popupNode.add(menuItem);
        menuItem = new JMenuItem("Properties", getImageIcon("property.png"));
        menuItem.addActionListener(actions.get("NodeProperties"));
        popupNode.add(menuItem);

        // Init popup menu for Domen (Not showing)
        popupDomen = new JPopupMenu();
        menuItem = new JMenuItem("Bla-bla");
        menuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        popupDomen.add(menuItem);
        menuItem = new JMenuItem("Force");
        menuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        popupDomen.add(menuItem);
    }

    private ImageIcon getImageIcon(String _iconName) {
        return new ImageIcon(getClass().getResource(Defaults.PATH_TO_SHARE + _iconName));
    }
}
