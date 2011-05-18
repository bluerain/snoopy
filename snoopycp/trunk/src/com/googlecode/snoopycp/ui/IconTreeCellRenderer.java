package com.googlecode.snoopycp.ui;

import com.googlecode.snoopycp.Defaults;
import com.googlecode.snoopycp.model.Node;
import com.googlecode.snoopycp.model.Node;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

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
            setIcon(getImageIcon("point.jpg"));
        } else {
            Node userObj = (Node) node.getUserObject();
            switch (userObj.nodeType) {
                case DOMEN:
                    setIcon(getImageIcon("point.jpg"));
                    break;
                case NODE:
                    if (userObj.os == Node.OsType.WIN) {
                        setIcon(getImageIcon("logo_win.jpg"));
                        //setIcon(new ImageIcon(getClass().getResource("point.jpg")));
                    } else if (userObj.os == Node.OsType.LIN) {
                        setIcon(getImageIcon("logo_lin.jpg"));
                    } else {
                        setIcon(getImageIcon("logo_qst.jpg"));
                    }
                    break;
                case MODULE:
                    // TODO status of module
                    setIcon(getImageIcon("status-offline.png"));
                    break;
                default:
                    setIcon(getImageIcon("jpg"));
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
