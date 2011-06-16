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

import javax.swing.table.AbstractTableModel;


public class DataBaseModuleTableModel extends AbstractTableModel {

    String[] names = {"Result", "Date"};
    String[][] results;

    public DataBaseModuleTableModel(String[][] _results) {
        this.results = _results;
    }

    @Override
    public String getColumnName(int column) {
        return names[column];
    }

    @Override
    public int getRowCount() {
        return results[0].length;
    }

    @Override
    public int getColumnCount() {
        return names.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return results[columnIndex][rowIndex];
    }
}
