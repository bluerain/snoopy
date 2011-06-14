/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

    private MainFrame view;
    private Coordinator coordinator;
    private Domain domain;
    private Connection conn = null;
    private Statement stmt;
    private ResultSet rs;
    private Logger logger;

    public HostResultsAL(MainFrame view, Coordinator _coordinator, Logger _logger) {
        this.view = view;
        this.coordinator = _coordinator;
        this.domain = coordinator.domain();
        logger = _logger;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DefaultMutableTreeNode lastSelectNode = (DefaultMutableTreeNode) view.getTree().getLastSelectedPathComponent();
        Node node = (Node) lastSelectNode.getUserObject();
        String[][] results = this.getHostResults(node.name);
        if (results != null) {
            view.addInternalFrame(new Results(new DataBaseHostTableModel(results), node.name));
        } else {
            JOptionPane.showMessageDialog(view, "Cannot connect to database");
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
}
