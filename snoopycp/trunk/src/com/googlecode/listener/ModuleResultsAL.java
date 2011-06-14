/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

    private MainFrame view;
    private Coordinator coordinator;
    private Logger logger;
    private Domain domain;
    private Connection conn = null;
    private Statement stmt;
    private ResultSet rs;

    public ModuleResultsAL(MainFrame view, Coordinator _coordinator, Logger _logger) {
        this.view = view;
        this.coordinator = _coordinator;
        this.logger = _logger;
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
        String[][] results = this.getModuleResults(hostname, node.name);
        if (results != null) {
            view.addInternalFrame(new Results(new DataBaseModuleTableModel(results), node.name));
        } else {
            JOptionPane.showMessageDialog(view, "Cannot connect to database");
        }
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
