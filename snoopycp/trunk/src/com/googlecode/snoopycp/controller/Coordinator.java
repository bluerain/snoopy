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
import com.googlecode.snoopycp.model.DataBaseHostTableModel;
import com.googlecode.snoopycp.model.DataBaseModuleTableModel;
import com.googlecode.snoopycp.model.Node;
import com.googlecode.snoopycp.ui.ModulePropertyInternalFrame;
import com.googlecode.snoopycp.ui.NodePropertiesInternalFrame;
import com.googlecode.snoopycp.ui.Results;
import com.googlecode.snoopycp.util.Identities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
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
            HashMap<String, String> map = (HashMap<String, String>) domain.hoster(node.identity).context();
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
            HashMap<String, String> map = (HashMap<String, String>) domain.hoster(node.identity).context();
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

    public static class HostResultsAL implements ActionListener {

        private MainFrame view;
        private Coordinator coordinator;

        public HostResultsAL(MainFrame view, Coordinator _coordinator) {
            this.view = view;
            this.coordinator = _coordinator;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DefaultMutableTreeNode lastSelectNode = (DefaultMutableTreeNode) view.getTree().getLastSelectedPathComponent();
            Node node = (Node) lastSelectNode.getUserObject();
            String[][] results = coordinator.getHostResults(node.name);
            if (results != null) {
                view.addInternalFrame(new Results(new DataBaseHostTableModel(results), node.name));
            } else {
                JOptionPane.showMessageDialog(view, "Cannot connect to database");
            }
        }
    }

    public static class ModuleResultsAL implements ActionListener {

        private MainFrame view;
        private Coordinator coordinator;

        public ModuleResultsAL(MainFrame view, Coordinator _coordinator) {
            this.view = view;
            this.coordinator = _coordinator;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DefaultMutableTreeNode lastSelectNode = (DefaultMutableTreeNode) view.getTree().getLastSelectedPathComponent();
            Node node = (Node) lastSelectNode.getUserObject();
            logger.debug(node.identity + "  " + node.muid);
            //String moduleName = coordinator.domain().enviroment().get(node.identity);
            Set<String> hosts = coordinator.domain().hosts();
            String hostname = null;
            for (String host : hosts) {
                if (coordinator.domain().enviroment().get(host).equals(node.identity)) {
                    hostname = host;
                    break;
                }
            }
            String[][] results = coordinator.getModuleResults(hostname, node.name);
            if (results != null) {
                view.addInternalFrame(new Results(new DataBaseModuleTableModel(results), node.name));
            } else {
                JOptionPane.showMessageDialog(view, "Cannot connect to database");
            }
        }
    }
    public static Logger logger = Logger.getLogger(Coordinator.class);
    private Domain domain;
    private MainFrame view;
    Connection conn = null;
    Statement stmt;
    ResultSet rs;

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
        actions.put("HostResults", new HostResultsAL(view, this));
        actions.put("ModuleResults", new ModuleResultsAL(view, this));
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

    public String[][] getHostResults(String hostname) {
        String sql = "select Module.name, Result.`result`, Result.datestamp from `Result`, `Host`, `Module` where Host.`name`='" + hostname + "' and Host.idHost=Result.idHost and Module.idModule=Result.idModule";
//        String userName = Defaults.DATABASEUSERNAME;
//        String password = Defaults.DATABASEPASSWORD;
        String url = domain.configurer(domain.enviroment().get(hostname)).configuration().get("connectionstring");//.split("?")[0];
        //String url = "jdbc:mysql://" + Defaults.DATABASEADDRESS + ":" + Defaults.DATABASEPORT + "/" + Defaults.DATABASENAME;
        logger.debug("Connect to datebase: " + url);
        String[][] results = null;

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            //logger.debug("Database");
            conn = DriverManager.getConnection(url);//, userName, password);
            logger.debug("Database connection established");
            stmt = conn.createStatement();
            if (stmt.execute(sql)) {
                rs = stmt.getResultSet();
                int i = 0;
                results = new String[3][];
                while (rs.next()) {
                    i++;
                }
                results[0] = new String[i];
                results[1] = new String[i];
                results[2] = new String[i];
                rs.beforeFirst();
                rs = stmt.getResultSet();
                i = 0;
                while (rs.next()) {
                    results[0][i] = rs.getString(1);
                    results[1][i] = rs.getString(2);
                    results[2][i] = rs.getString(3);
                    i++;
                }
            } else {
                logger.debug("SQL statement wasn`t execute");
            }
        } catch (InstantiationException ex) {
            logger.error("Instantiation");
        } catch (IllegalAccessException ex) {
            logger.error("IllegalAccess");
        } catch (ClassNotFoundException ex) {
            logger.error("ClassNotFound");
        } catch (SQLException e) {
            logger.error("Cannot connect to database server");
        }
        return results;
    }

    public String[][] getModuleResults(String hostname, String moduleName) {
        String sql = "select Result.`result`, Result.datestamp from `Result`, `Host`, `Module` where Host.`name`='" + hostname + "' and Host.idHost=Result.idHost and Module.idModule=Result.idModule and Module.`name`='" + moduleName + "'";
//        String userName = Defaults.DATABASEUSERNAME;
//        String password = Defaults.DATABASEPASSWORD;
        String url = domain.configurer(domain.enviroment().get(hostname)).configuration().get("connectionstring");//.split("?")[0];
        logger.debug("Connect to datebase: " + url);
        String[][] results = null;

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url);//, userName, password);
            logger.debug("Database connection established");
            stmt = conn.createStatement();
            if (stmt.execute(sql)) {
                rs = stmt.getResultSet();
                int i = 0;
                results = new String[2][];
                while (rs.next()) {
                    i++;
                }
                results[0] = new String[i];
                results[1] = new String[i];
                rs.beforeFirst();
                rs = stmt.getResultSet();
                i = 0;
                while (rs.next()) {
                    results[0][i] = rs.getString(1);
                    results[1][i] = rs.getString(2);
                    i++;
                }
            } else {
                logger.debug("SQL statement wasn`t execute");
            }
        } catch (InstantiationException ex) {
            logger.error("Instantiation");
        } catch (IllegalAccessException ex) {
            logger.error("IllegalAccess");
        } catch (ClassNotFoundException ex) {
            logger.error("ClassNotFound");
        } catch (SQLException e) {
            logger.error("Cannot connect to database server");
        }
        return results;
    }
}
