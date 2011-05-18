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

import Ice.Identity;
import com.googlecode.snoopycp.core.Domain;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Constants;
import java.util.Set;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class TreeModel extends DefaultTreeModel implements javax.swing.tree.TreeModel {

    private Domain domain;

    public TreeModel(Domain domain) {
        super(new DefaultMutableTreeNode(domain.name()));

        this.domain = domain;
    }

    public void update() {
        DefaultMutableTreeNode domainRoot = (DefaultMutableTreeNode) root;
        domainRoot.removeAllChildren();

        Set<String> hosts = domain.hosts();
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
            domainRoot.add(node);

            for (String moduleID : domain.moduler(identity).values()) {
                node.add(new DefaultMutableTreeNode(
                        new Node(null, moduleID, Node.Type.MODULE, null), false));
            }
        }
    }
}
