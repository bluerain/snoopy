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

import Ice.Identity;
import java.util.Map;

public class Node {

    public final Ice.Identity identity;
    public final String name;
    public String muid;
    public Map<String, String> moduleStatuses;

    public enum Type {

        DOMEN, NODE, MODULE
    };

    public enum OsType {

        WIN, LIN, MAC, UNIX, UNKNOWN
    };
    public Type nodeType;
    public OsType os;

    public Node(Identity _identity, String _name, Type _type, OsType _os) {
        this.identity = _identity;
        this.name = _name;
        this.nodeType = _type;
        this.os = _os;
    }
    
    public Node(Identity _identity, String _name, String _muid, Type _type, Map<String, String> _moduleStatuses) {
        this(_identity, _name, _type, null);
        muid = _muid;
        moduleStatuses = _moduleStatuses;
    }

    @Override
    public String toString() {
        return name;
    }
}
