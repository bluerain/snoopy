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

package com.googlecode.snoopycp.core;

import Ice.Communicator;
import Ice.Identity;
import com.googlecode.snoopycp.Defaults;
import com.googlecode.snoopycp.util.Identities;
import com.googlecode.snoopyd.driver.IConfigurerPrx;
import com.googlecode.snoopyd.driver.IControllerPrx;
import com.googlecode.snoopyd.driver.IHosterPrx;
import com.googlecode.snoopyd.driver.IModulerPrx;
import com.googlecode.snoopyd.driver.ISchedulerPrx;
import com.googlecode.snoopyd.driver.ISessionierPrx;
import com.googlecode.snoopyd.driver.ISessionierPrxHelper;
import com.googlecode.snoopyd.session.IUserSessionPrx;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;

public class Domain extends Observable implements Runnable {

    public static Logger logger = Logger.getLogger(Domain.class);
    private Ice.Identity identity;
    private Ice.Communicator communicator;
    private String name;
    private Ice.ObjectAdapter adapter;
    private Set<String> hosts;
    private Map<String, Ice.Identity> enviroment;
    private Map<Ice.Identity, Long> life;
    private Map<Ice.Identity, Map<String, String>> cache;
    private Map<Ice.Identity, IHosterPrx> hosters;
    private Map<Ice.Identity, IUserSessionPrx> sessions;
    private Map<Ice.Identity, IControllerPrx> controllers;
    private Map<Ice.Identity, Integer> hashes;
    private Map<Ice.Identity, String> osPull;
    private Map<Ice.Identity, IModulerPrx> modulers;
    private Map<Ice.Identity, ISchedulerPrx> schedulers;
    private Map<Ice.Identity, Map<String, String>> modulesStatus;
    private Map<Ice.Identity, Map<String, String>> modulesName;
    private Map<Ice.Identity, IConfigurerPrx> configurers;

    public Domain(Communicator communicator, String name) {

        this.identity = Identities.randomIdentity(name);

        this.communicator = communicator;
        this.name = name;

        this.hosts = new HashSet<String>();
        this.enviroment = new HashMap<String, Ice.Identity>();

        this.life = new HashMap<Identity, Long>();

        this.cache = new ConcurrentHashMap<Ice.Identity, Map<String, String>>();

        this.sessions = new ConcurrentHashMap<Ice.Identity, IUserSessionPrx>();
        this.controllers = new ConcurrentHashMap<Ice.Identity, IControllerPrx>();
        this.hosters = new ConcurrentHashMap<Ice.Identity, IHosterPrx>();
        this.osPull = new ConcurrentHashMap<Ice.Identity, String>();
        this.modulers = new ConcurrentHashMap<Ice.Identity, IModulerPrx>();
        this.schedulers = new ConcurrentHashMap<Ice.Identity, ISchedulerPrx>();
        this.configurers = new ConcurrentHashMap<Ice.Identity, IConfigurerPrx>();
        this.modulesStatus = new ConcurrentHashMap<Ice.Identity, Map<String, String>>();
        this.modulesName = new ConcurrentHashMap<Ice.Identity, Map<String, String>>();

        this.hashes = new HashMap<Identity, Integer>();

        this.adapter = communicator.createObjectAdapter(Defaults.DEFAULT_ADAPTER_NAME);

        this.adapter.add(new DiscovererAdapter(new Discoverer(this)), communicator.stringToIdentity(Discoverer.class.getSimpleName()));
        this.adapter.activate();

        Thread self = new Thread(this);
        // FIXME too early start
        self.start();
    }

    public String name() {
        return name;
    }

    public Ice.Identity identity() {
        return identity;
    }

    public Ice.Communicator communicator() {
        return communicator;
    }

    public void cacheit(Ice.Identity identity, Map<String, String> context) {

        synchronized (this) {

            boolean changed = false;

            int oldSize = cache.size();
            cache.put(identity, context);
            int newSize = cache.size();

            life.put(identity, System.currentTimeMillis());

            if (oldSize != newSize) {

                hosts.add(context.get("hostname"));
//                Set<String> set = context.keySet();
//                for(String str: set) {
//                    System.out.print("OMG: " + str + " : ");
//                    System.out.println(context.get(str));
//                }

                enviroment.put(context.get("hostname"), identity);

                osPull.put(identity, context.get("os"));

                String proxy = Identities.toString(identity) + ": " + context.get("primary");
                try {
                    ISessionierPrx remoteSessionier = ISessionierPrxHelper.checkedCast(communicator.stringToProxy(proxy));
                    logger.debug("Checked cast to remote session");

                    remoteSessionier.ice_ping();
                    IUserSessionPrx remoteSessionPrx = remoteSessionier.createUserSession(identity(), null);
                    sessions.put(identity, remoteSessionPrx);

                    remoteSessionPrx.ice_ping();
                    IHosterPrx remoteHoster = remoteSessionPrx.hoster();
                    hosters.put(identity, remoteHoster);

                    remoteSessionPrx.ice_ping();
                    IControllerPrx remoteController = remoteSessionPrx.controller();
                    controllers.put(identity, remoteController);

                    remoteSessionPrx.ice_ping();
                    IModulerPrx remoteModuler = remoteSessionPrx.moduler();
                    modulers.put(identity, remoteModuler);
                    remoteModuler.ice_ping();
                    modulesName.put(identity, remoteModuler.fetch());

                    remoteSessionPrx.ice_ping();
                    ISchedulerPrx remoteScheduler = remoteSessionPrx.scheduler();
                    schedulers.put(identity, remoteScheduler);

                    schedulers.get(identity).ice_ping();
                    modulesStatus.put(identity, schedulers.get(identity).statetable());

                    remoteSessionPrx.ice_ping();
                    IConfigurerPrx remoteConfigurer = remoteSessionPrx.configurer();
                    configurers.put(identity, remoteConfigurer);
                } catch (Ice.ConnectionRefusedException ex) {
                    logger.warn("Problem with fetch information about remote node: " + ex.getMessage());
                    // TODO do something with problem: del already added info
                }
                changed = true;
                logger.debug("New host: " + context.get("hostname") + " was added");
                logger.debug(newSize + " hosts in domain");
            } else {
                if (hosts.contains(context.get("hostname")) && enviroment.get(context.get("hostname")) == null) {
                    enviroment.put(context.get("hostname"), identity);
                    changed = true;
                }
            }

            if ((hashes.get(identity) == null ? 0 : hashes.get(identity)) != context.hashCode()) {
                hashes.put(identity, context.hashCode());
                changed = true;
            }

            if (changed) {
                notifyObserver();
            }
        }
    }

    public Set<String> hosts() {
        return hosts;
    }

    public Map<Ice.Identity, String> osPull() {
        return osPull;
    }

    public boolean isDied(Ice.Identity identity) {
        return !enviroment.containsValue(identity);
    }

    public Map<String, Ice.Identity> enviroment() {
        return Collections.unmodifiableMap(enviroment);
    }

    public Map<String, String> cache(Ice.Identity identity) {
        return cache.get(identity);
    }

    public IHosterPrx hoster(Ice.Identity identity) {
        return hosters.get(identity);
    }

    public IModulerPrx moduler(Ice.Identity _identity) {
        return modulers.get(_identity);
    }

    public ISchedulerPrx scheduler(Ice.Identity _identity) {
        return schedulers.get(_identity);
    }

    public IControllerPrx controller(Ice.Identity identity) {
        return controllers.get(identity);
    }

    public IConfigurerPrx configurer(Ice.Identity identity) {
        return configurers.get(identity);
    }

    public Map<String, String> moduleStatus(Ice.Identity _ident) {
        return modulesStatus.get(_ident);
    }

    public Map<String, String> moduleName(Ice.Identity _ident) {
        return modulesName.get(_ident);
    }

    public IUserSessionPrx session(Ice.Identity identity) {
        return sessions.get(identity);
    }

    public void updateModules(Ice.Identity _ident) {
        try {
            this.sessions.get(_ident).ice_ping();
            HashMap<String, String> map = (HashMap<String, String>) this.sessions.get(_ident).moduler().fetch();
            this.modulesName.remove(_ident);
            this.modulesName.put(_ident, map);
            schedulers.get(_ident).ice_ping();
            modulesStatus.remove(_ident);
            modulesStatus.put(_ident, schedulers.get(_ident).statetable());
            notifyObserver();
//            for (String str : map.values()) {
//                System.out.println(str);
//            }
            logger.debug("Modules list updated");
        } catch (Ice.ConnectionRefusedException ex) {
            logger.warn("Updating modules list failed: " + ex.getMessage());
        }
    }

    @Override
    public void run() {

        for (;;) {

            boolean changed = false;
            //modulesStatus.clear();
            synchronized (this) {
                for (String host : hosts) {
                    Ice.Identity id = enviroment.get(host);

                    if (id == null) {
                        continue;
                    }

                    if (System.currentTimeMillis() - life.get(id) > 10000) {
                        // FIXME Then node back to life adding new node
                        enviroment.remove(host);
                        logger.debug("Host " + host + " didn`t response and was removed from domain");
                        changed = true;
                    }
//                    try {
//                        //modulesStatus.put(id, schedulers.get(id).statetable());
//                    } catch (Ice.ConnectionRefusedException ex) {
//                        logger.debug(host + " node didn`t response: " + ex.getMessage());
//                        // TODO don`t remove - make [dead]
//                        removeHost(host);
//                    }

                }
            }

            if (changed) {
                notifyObserver();
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                logger.warn("Thread cann`t fall into dream: " + ex.getMessage());
            }
        }

    }

    public void notifyObserver() {
        setChanged();
        notifyObservers();
        logger.debug("Domain is changed. Observers notified.");
    }

    public void removeHost(String _hostname) {
        this.hosts.remove(_hostname);
        logger.debug("Host: " + _hostname + " was removed. " + hosts.size());
        notifyObserver();
    }
}
