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

import com.googlecode.snoopycp.core.Domain;
import com.googlecode.snoopycp.model.GraphModel;
import com.googlecode.snoopycp.model.TableModel;
import com.googlecode.snoopycp.model.TreeModel;
import java.awt.Color;
import java.awt.Paint;
import org.apache.commons.collections15.Transformer;

public class DomainController {

    private Domain domain;

    public DomainController(Domain domain) {
        this.domain = domain;
    }

    public TableModel createTableModel() {
        return new TableModel(domain);
    }

    public TreeModel createTreeModel() {
        return new TreeModel(domain);
    }

    public GraphModel createGraphModel() {
        return new GraphModel(domain);
    }

    public Transformer<String, String> createLabelTransformer() {
        return new Transformer<String, String>() {

            public String transform(String vertex) {
                return vertex;
            }
        };
    }

    public Transformer<String, Paint> createFillTransformer() {
        return new Transformer<String, Paint>() {

            public Paint transform(String vertex) {

                if (domain.enviroment().get(vertex) == null) {
                    return Color.GRAY;
                } else  {
                    Ice.Identity identity = domain.enviroment().get(vertex);
                    String state = domain.cache(identity).get("state");
                    
                    if (state.equals("OnlineState")) {
                        return Color.YELLOW;
                    } else if (state.equals("ActiveState")) {
                        return Color.RED;
                    } else {
                        return Color.BLUE;
                    }
                }

            }
        };
    }
}
