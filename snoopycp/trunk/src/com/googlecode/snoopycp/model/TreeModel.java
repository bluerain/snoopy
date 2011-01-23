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
import java.util.Set;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class TreeModel extends DefaultTreeModel implements javax.swing.tree.TreeModel {

    public static class Node {

        public final Ice.Identity identity;
        public final String name;

        public Node(Identity identity, String name) {
            this.identity = identity;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    private Domain domain;

    public TreeModel(Domain domain) {
        super(new DefaultMutableTreeNode(domain.name()));

        this.domain = domain;
    }

    public void update() {

        DefaultMutableTreeNode domainRoot = (DefaultMutableTreeNode) root;
        domainRoot.removeAllChildren();

        Set<String> hosts = domain.hosts();

        for (String host : hosts) {
            Ice.Identity identity = domain.enviroment().get(host);
            if (identity == null) {
                domainRoot.add(new DefaultMutableTreeNode(new Node(identity, host + " [died]")));
            } else {
                domainRoot.add(new DefaultMutableTreeNode(new Node(identity, host)));
            }
        }
    }
}
