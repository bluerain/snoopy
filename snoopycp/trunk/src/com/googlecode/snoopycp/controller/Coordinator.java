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
import com.googlecode.snoopycp.ui.View;
import com.googlecode.snoopycp.core.Domain;
import com.googlecode.snoopycp.model.Node;
import com.googlecode.snoopycp.model.TableModel;
import com.googlecode.snoopycp.ui.NodePropertiesInternalFrame;
import com.googlecode.snoopycp.util.Identities;
import com.googlecode.snoopyd.driver.IModulerPrx;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.log4j.Logger;

public class Coordinator {

    public static class NodePropertiesAL implements ActionListener {

        private View view;
        private Domain domain;

        public NodePropertiesAL(View view, Domain _domain) {
            this.view = view;
            this.domain = _domain;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DefaultMutableTreeNode lastSelectNode = (DefaultMutableTreeNode) view.getTree().getLastSelectedPathComponent();
            Node node = (Node) lastSelectNode.getUserObject();
            System.out.println();
            HashMap <String, String> map = (HashMap<String, String>) domain.hoster(node.identity).context();
            map.put("IP", domain.cache(node.identity).get("primary").split(" ")[2]);
            view.addInternalFrame(new NodePropertiesInternalFrame(map));
        }
    }

    public static class NodeForceStartAL implements ActionListener {

        private View view;
        private Domain domain;

        public NodeForceStartAL(View view, Domain _domain) {
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

        private View view;
        private Domain domain;

        public NodeShutdownAL(View view, Domain _domain) {
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
                    domain.hosts().remove(host);
                    logger.debug("Host: " + host + " was removed. " + domain.hosts().size());
                }
            }
            domain.notifyObserver();
        }
    }

    public static Logger logger = Logger.getLogger(Coordinator.class);
    private Domain domain;
    private View view;
    private IModulerPrx moduler;
    //private Map<String, ActionListener> actions;

    public Coordinator(Domain domain, View view) {
        this.domain = domain;
        this.view = view;

        this.view.setActionsOnPopup(this.packActions());
        domain.addObserver(view);
    }

    private Map<String, ActionListener> packActions() {
        Map<String, ActionListener> actions = new HashMap<String, ActionListener>();
        actions.put("Properties", new NodePropertiesAL(view, domain));
        actions.put("ForceStart", new NodeForceStartAL(view, domain));
        actions.put("Shutdown", new NodeShutdownAL(view, domain));
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
    public Ice.Identity currentIdentity() {
        Object obj = view.getTree().getLastSelectedPathComponent();
        DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) obj;
        obj = dmtn.getUserObject();
        Node node = (Node) obj;
        return node.identity;
    }
}
