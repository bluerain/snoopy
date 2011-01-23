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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;

public class TableModel extends AbstractTableModel implements javax.swing.table.TableModel {

    public static final String[] COLUMNS = {"Property", "Value"};
    private Domain domain;
    private List<String> keys;
    private List<String> values;
    private int size;

    public TableModel(Domain domain) {
        super();

        this.domain = domain;

        this.keys = new ArrayList<String>();
        this.values = new ArrayList<String>();

        this.size = 0;
    }

    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    public int getRowCount() {
        return size;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return keys.get(rowIndex);
        } else {
            return values.get(rowIndex);
        }
    }

    public void update(Ice.Identity identity) {

        keys.clear();
        values.clear();

        size = 0;

        try {
            if (identity != null) {

                domain.hoster(identity).ice_ping();

                Map<String, String> data = domain.hoster(identity).context();

                size = data.size();

                for (String key : data.keySet()) {
                    keys.add(key);
                    values.add(data.get(key));
                }
            }
        } catch (Exception ex) {
        }
    }
}
