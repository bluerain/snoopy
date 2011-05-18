
package com.googlecode.snoopycp.model;

import Ice.Identity;

public class Node {
    public final Ice.Identity identity;
        public final String name;
        enum Type {DOMEN, NODE, MODULE};
        enum OsType {WIN, LIN, MAC, UNIX, UNKNOWN};
        public Type nodeType;
        public OsType os;

        public Node(Identity identity, String name, Type _type, OsType _os) {
            this.identity = identity;
            this.name = name;
            this.nodeType = _type;
            this.os = _os;
        }

        @Override
        public String toString() {
            return name;
        }
}
