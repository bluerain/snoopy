package com.googlecode.snoopycp.model;

import Ice.Identity;
import com.googlecode.snoopyd.driver.ISchedulerPrx;

public class Node {

    public final Ice.Identity identity;
    public final String name;
    public String muid;
    public ISchedulerPrx scheduler;

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
    
    public Node(Identity _identity, String _name, String _muid, Type _type, ISchedulerPrx _scheduler) {
        this(_identity, _name, _type, null);
        muid = _muid;
        scheduler = _scheduler;
    }

    @Override
    public String toString() {
        return name;
    }
}
