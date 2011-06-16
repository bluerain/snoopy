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

import com.googlecode.snoopycp.core.Domain;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;


public class ParamTableModel extends AbstractTableModel {

    Domain domain;
    Ice.Identity ident;
    Logger logger;
    String muid;

    public ParamTableModel(Domain _domain, Ice.Identity _ident, Logger _logger, String _muid) {
        domain = _domain;
        ident = _ident;
        logger = _logger;
        muid = _muid;
    }

    @Override
    public String getColumnName(int column) {
        return "Params";
    }

    @Override
    public int getRowCount() {
        try {
            domain.scheduler(ident).ice_ping();
            String[] strs = domain.scheduler(ident).paramtable().get(muid).split(";");
            return strs.length;
        } catch (Ice.ConnectionRefusedException ex) {
            logger.warn("Cann`t fetch timetable from " + domain.enviroment().get(ident));
            return 1;
        }
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            domain.scheduler(ident).ice_ping();
            String[] params = domain.scheduler(ident).paramtable().get(muid).split(";");
            return params[rowIndex];
        } catch (Ice.ConnectionRefusedException ex) {
            logger.warn("Cann`t fetch timetable from " + domain.enviroment().get(ident));
        }
        return "Cann`t fetch timetable from " + domain.enviroment().get(ident);
    }
}
