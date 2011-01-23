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
import com.googlecode.snoopycp.util.Identities;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import java.util.Map;

public class GraphModel {

    private Domain domain;
    private Graph<String, String> graph;

    public GraphModel(Domain domain) {
        this.domain = domain;

        graph = new DirectedSparseGraph<String, String>();
    }

    public void update() {

        for (String host : domain.hosts()) {

            graph.addVertex(host);

            Ice.Identity identity = domain.enviroment().get(host);

            if (identity != null) {

                Map<String, String> ctx = domain.cache(identity);

                for (String child : ctx.get("childs").split(";")) {

                    if (!child.equals("")) {

                        Ice.Identity childIdentity = Identities.stringToIdentity(child);

                        for (String childhost : domain.hosts()) {
                            if (childIdentity.equals(domain.enviroment().get(childhost))) {

                                String edgeString = Identities.toString(Identities.xor(identity, childIdentity));
                                graph.addEdge(edgeString, host, childhost);

                                break;
                            }
                        }
                    }

                    
                }
            }
        }
    }

    public Graph<String, String> graph() {
        return graph;
    }
}
