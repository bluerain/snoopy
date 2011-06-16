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
import com.googlecode.snoopycp.util.Identities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.tree.DefaultMutableTreeNode;


public class NodeShutdownAL implements ActionListener {

        private MainFrame view;
        private Domain domain;

        public NodeShutdownAL(MainFrame view, Domain _domain) {
            this.view = view;
            this.domain = _domain;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // get selected node
            DefaultMutableTreeNode lastSelectNode = (DefaultMutableTreeNode) view.getTree().getLastSelectedPathComponent();
            // get object with information about node
            Node node = (Node) lastSelectNode.getUserObject();
            // command "shutdown" to selected node
            domain.controller(node.identity).shutdown();
            for (String host : domain.hosts()) { // search in all hosts
                if (Identities.equals(domain.enviroment().get(host), node.identity)) {
                    domain.removeHost(host); // remove host from domain after shutdown
                }
            }
            // Domain is changed, notify to somebody
            domain.notifyObserver();
        }
    }
