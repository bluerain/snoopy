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
package com.googlecode.snoopycp.controller;

import com.googlecode.snoopycp.Defaults;
import com.googlecode.snoopycp.ui.MainFrame;
import com.googlecode.snoopycp.core.Domain;
import com.googlecode.snoopycp.model.Node;
import com.googlecode.snoopycp.ui.ModulePropertyInternalFrame;
import com.googlecode.snoopycp.ui.NodePropertiesInternalFrame;
import com.googlecode.snoopycp.util.Identities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.log4j.Logger;

public class Coordinator {

    public static class NodePropertiesAL implements ActionListener {

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
            HashMap <String, String> map = (HashMap<String, String>) domain.hoster(node.identity).context();
            map.put("IP", domain.cache(node.identity).get("primary").split(" ")[2]);
            view.addInternalFrame(new NodePropertiesInternalFrame(map, node.name, this.domain.configurer(node.identity)));
            //System.out.println(domain.configurer(node.identity).configuration().get("connectionstring"));
        }
    }
    
    public static class ModulePropertiesAL implements ActionListener {

        private MainFrame view;
        private Domain domain;

        public ModulePropertiesAL(MainFrame view, Domain _domain) {
            this.view = view;
            this.domain = _domain;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DefaultMutableTreeNode lastSelectNode = (DefaultMutableTreeNode) view.getTree().getLastSelectedPathComponent();
            Node node = (Node) lastSelectNode.getUserObject();
            //System.out.println();
            HashMap <String, String> map = (HashMap<String, String>) domain.hoster(node.identity).context();
            map.put("IP", domain.cache(node.identity).get("primary").split(" ")[2]);
            view.addInternalFrame(new ModulePropertyInternalFrame(domain, node.identity, node.muid));
            //System.out.println(domain.configurer(node.identity).configuration().get("connectionstring"));
        }
    }

    public static class NodeForceStartAL implements ActionListener {

        private MainFrame view;
        private Domain domain;

        public NodeForceStartAL(MainFrame view, Domain _domain) {
            this.view = view;
            this.domain = _domain;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DefaultMutableTreeNode lastSelectNode = (DefaultMutableTreeNode) view.getTree().getLastSelectedPathComponent();
            Node node = (Node) lastSelectNode.getUserObject();
            this.domain.moduler(node.identity).force(node.muid, this.view.showInputDialog().split(";"));
            
        }
    }
    
    public static class NodeShutdownAL implements ActionListener {

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

    public static Logger logger = Logger.getLogger(Coordinator.class);
    private Domain domain;
    private MainFrame view;
    
    public Coordinator(Domain _domain, MainFrame view) {
        this.domain = _domain;
        this.view = view;
        this.view.setActionsOnPopup(this.packActions());
        domain.addObserver(view);
        view.setCoordinator(this);
    }
    
    public Domain domain() {
        return this.domain;
    }

    private Map<String, ActionListener> packActions() {
        Map<String, ActionListener> actions = new HashMap<String, ActionListener>();
        actions.put("NodeProperties", new NodePropertiesAL(view, domain));
        actions.put("ForceStart", new NodeForceStartAL(view, domain));
        actions.put("Shutdown", new NodeShutdownAL(view, domain));
        actions.put("ModuleProperties", new ModulePropertiesAL(view, domain));
        return actions;
    }

    public void launch() {

        view.setTitle("[" + domain.name() + "] " + Defaults.APP_NAME + " " + Defaults.APP_VER);

        view.setLocationRelativeTo(null);
        view.setVisible(true);

        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException ex) {
                logger.error(ex.getMessage());
            }
        }
    }

    public void terminate() {

        synchronized (this) {
            notify();
        }
    }
}
