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
package com.googlecode.snoopycp.ui;

import com.googlecode.snoopycp.Defaults;
import com.googlecode.snoopycp.model.Node;
import java.awt.Component;
import java.util.HashMap;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import sun.util.logging.resources.logging;

public class IconTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        String stringValue = tree.convertValueToText(value, sel, expanded, leaf, row, hasFocus);

        this.hasFocus = hasFocus;
        setText(" " + stringValue + " ");
        if (sel) {
            setForeground(getTextSelectionColor());
        } else {
            setForeground(getTextNonSelectionColor());
        }

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (node.getUserObject() instanceof String) {
            setIcon(getImageIcon("globe-network.png"));
        } else {
            Node userObj = (Node) node.getUserObject();
            switch (userObj.nodeType) {
                case DOMEN:
                    setIcon(getImageIcon("network.png"));
                    break;
                case NODE:
                    if (userObj.os == Node.OsType.WIN) {
                        setIcon(getImageIcon("logo_win.jpg"));
                    } else if (userObj.os == Node.OsType.LIN) {
                        setIcon(getImageIcon("logo_lin.jpg"));
                    } else {
                        setIcon(getImageIcon("logo_qst.png"));
                    }
                    break;
                case MODULE:
                    try {
                        HashMap<String, String> map = (HashMap) userObj.moduleStatuses;
                        if (map.get(userObj.muid).equalsIgnoreCase("on")) {
                            setIcon(getImageIcon("status-online.png"));
                        } else {
                            setIcon(getImageIcon("status-offline.png"));
                        }
                    } catch (NullPointerException e) {
                        //JOptionPane.showMessageDialog(null, "No module status for " + userObj.name);
                        setIcon(getImageIcon("status-offline.png"));
                    }
                    break;
                default:
                    setIcon(getImageIcon("logo_qst.jpg"));
            }
        }

        setComponentOrientation(tree.getComponentOrientation());
        selected = sel;
        return this;
    }

    private ImageIcon getImageIcon(String _iconName) {
        return new ImageIcon(getClass().getResource(Defaults.PATH_TO_SHARE + _iconName));
    }
}
