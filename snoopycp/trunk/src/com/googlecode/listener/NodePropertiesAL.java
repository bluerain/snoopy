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

package com.googlecode.listener;

import com.googlecode.snoopycp.core.Domain;
import com.googlecode.snoopycp.model.Node;
import com.googlecode.snoopycp.ui.MainFrame;
import com.googlecode.snoopycp.ui.NodePropertiesInternalFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.tree.DefaultMutableTreeNode;


 public class NodePropertiesAL implements ActionListener {

        private MainFrame view;
        private Domain domain;

        public NodePropertiesAL(MainFrame view, Domain _domain) {
            this.view = view;
            this.domain = _domain;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // get selected node
            DefaultMutableTreeNode lastSelectNode = (DefaultMutableTreeNode) view.getTree().getLastSelectedPathComponent();
            // get object with information about node
            Node node = (Node) lastSelectNode.getUserObject();
            // list of node properties
            HashMap<String, String> map = (HashMap<String, String>) domain.hoster(node.identity).context();
            // put in list ip-address
            map.put("IP", domain.cache(node.identity).get("primary").split(" ")[2]);
            // display properties in new window
            view.addInternalFrame(new NodePropertiesInternalFrame(map, node.name, this.domain.configurer(node.identity)));
        }
    }
