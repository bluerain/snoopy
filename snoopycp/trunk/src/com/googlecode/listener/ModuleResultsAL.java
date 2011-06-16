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

import com.googlecode.snoopycp.controller.Coordinator;
import com.googlecode.snoopycp.core.Domain;
import com.googlecode.snoopycp.model.DataBaseModuleTableModel;
import com.googlecode.snoopycp.model.Node;
import com.googlecode.snoopycp.ui.MainFrame;
import com.googlecode.snoopycp.ui.Results;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.log4j.Logger;

/**
 *
 * @author Leo
 */
public class ModuleResultsAL implements ActionListener {

    private MainFrame view;             // View
    private Coordinator coordinator;    // Controller
    private Domain domain;              // Model
    private Connection conn = null;     // connection to database
    private Statement stmt;
    private ResultSet rs;               // Results of sql script executing
    private Logger logger;              // Logger which prints to console

    public ModuleResultsAL(MainFrame view, Coordinator _coordinator, Logger _logger) {
        this.view = view;
        this.coordinator = _coordinator;
        this.logger = _logger;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // get selected node
        DefaultMutableTreeNode lastSelectNode = (DefaultMutableTreeNode) view.getTree().getLastSelectedPathComponent();
        // get object with information about node
        Node node = (Node) lastSelectNode.getUserObject();
        logger.debug(node.identity + "  " + node.muid);
        // get list of hosts registred in panel
        Set<String> hosts = coordinator.domain().hosts();
        String hostname = null;
        for (String host : hosts) { // for all hosts
            if (coordinator.domain().enviroment().get(host).equals(node.identity)) {
                hostname = host; // get host name of selected node
                break;
            }
        }
        // get data from database
        String[][] results = this.getModuleResults(hostname, node.name);
        if (results != null) {
            // Display all in window
            view.addInternalFrame(new Results(new DataBaseModuleTableModel(results), node.name));
        } else {
            JOptionPane.showMessageDialog(view, "Cannot connect to database");
        }
    }

    public String[][] getModuleResults(String hostname, String moduleName) {
        // Sql script
        String sql = "select Result.`result`, Result.datestamp from `Result`, `Host`, `Module` where Host.`name`='" + hostname + "' and Host.idHost=Result.idHost and Module.idModule=Result.idModule and Module.`name`='" + moduleName + "'";
        // String to connect
        String url = domain.configurer(domain.enviroment().get(hostname)).configuration().get("connectionstring");
        logger.debug("Connect to datebase: " + url);
        String[][] results = null; // results

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance(); // load driver
            conn = DriverManager.getConnection(url);                // connect
            logger.debug("Database connection established");        // all is good
            stmt = conn.createStatement();
            if (stmt.execute(sql)) {
                rs = stmt.getResultSet();   // get results from base
                int i = 0;
                results = new String[2][];
                while (rs.next()) { // calculate row count
                    i++;
                }
                results[0] = new String[i];
                results[1] = new String[i];
                rs.beforeFirst();           // reinit resultset
                rs = stmt.getResultSet();
                i = 0;
                while (rs.next()) {
                    results[0][i] = rs.getString(1);    // get data
                    results[1][i] = rs.getString(2);    //   in string format
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
