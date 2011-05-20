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
