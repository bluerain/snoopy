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
import com.googlecode.snoopycp.model.DataBaseHostTableModel;
import com.googlecode.snoopycp.model.Node;
import com.googlecode.snoopycp.ui.MainFrame;
import com.googlecode.snoopycp.ui.Results;
import java.sql.Connection;
import java.sql.Statement;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.log4j.Logger;

/**
 *
 * @author Leo
 */
public class HostResultsAL implements ActionListener {

    private MainFrame view;             // View
    private Coordinator coordinator;    // Controller
    private Domain domain;              // Model
    private Connection conn = null;     // connection to database
    private Statement stmt;
    private ResultSet rs;               // Results of sql script executing
    private Logger logger;              // Logger which prints to console

    public HostResultsAL(MainFrame view, Coordinator _coordinator, Logger _logger) {
        // Initialisation of data
        this.view = view;
        this.coordinator = _coordinator;
        this.domain = coordinator.domain();
        logger = _logger;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Get last selected node in the Tree
        DefaultMutableTreeNode lastSelectNode = (DefaultMutableTreeNode) view.getTree().getLastSelectedPathComponent();
        Node node = (Node) lastSelectNode.getUserObject();
        String[][] results = this.getHostResults(node.name); // get data from database
        if (results != null) {
            // Display results in new window
            view.addInternalFrame(new Results(new DataBaseHostTableModel(results), node.name));
        } else {
            JOptionPane.showMessageDialog(view, "Cannot connect to database");
        }
    }

    public String[][] getHostResults(String hostname) {
        // SQL script
        String sql = "select Module.name, Result.`result`, Result.datestamp from `Result`, `Host`, `Module` where Host.`name`='" + hostname + "' and Host.idHost=Result.idHost and Module.idModule=Result.idModule";
        // Url to connect
        String url = domain.configurer(domain.enviroment().get(hostname)).configuration().get("connectionstring");
        logger.debug("Connect to datebase: " + url);
        String[][] results = null;

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();   // load driver
            conn = DriverManager.getConnection(url);                // get connection
            logger.debug("Database connection established");
            stmt = conn.createStatement();
            if (stmt.execute(sql)) {    // execute sql script
                rs = stmt.getResultSet();   // get results
                int i = 0;
                results = new String[3][];
                while (rs.next()) {     // calculate row count
                    i++;
                }
                results[0] = new String[i];
                results[1] = new String[i];
                results[2] = new String[i];
                rs.beforeFirst();       // initialisation result set
                rs = stmt.getResultSet();
                i = 0;
                while (rs.next()) {
                    results[0][i] = rs.getString(1);    // Fill
                    results[1][i] = rs.getString(2);    //   the
                    results[2][i] = rs.getString(3);    //     results
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
