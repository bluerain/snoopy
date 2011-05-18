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
import com.googlecode.snoopyd.driver.IControllerPrx;
import com.googlecode.snoopyd.driver.IHosterPrx;
import com.googlecode.snoopyd.driver.IModulerPrx;
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
    private Map<Ice.Identity, IUserSessionPrx> sessions;
    private Map<Ice.Identity, IHosterPrx> hosters;
    private Map<Ice.Identity, IControllerPrx> controllers;
    private Map<Ice.Identity, Integer> hashes;
    private Map<Ice.Identity, String> osPull;
    private Map<Ice.Identity, Map<String, String>> modulers;

    public Domain(Communicator communicator, String name) {

        this.identity = Identities.randomIdentity(name);

        this.communicator = communicator;
        this.name = name;

        this.hosts = new HashSet<String>();
        this.enviroment = new HashMap<String, Ice.Identity>();

        this.life = new HashMap<Identity, Long>();

        this.cache = new ConcurrentHashMap<Ice.Identity, Map<String, String>>();

        this.sessions = new ConcurrentHashMap<Ice.Identity, IUserSessionPrx>();
        this.hosters = new ConcurrentHashMap<Ice.Identity, IHosterPrx>();
        this.controllers = new ConcurrentHashMap<Identity, IControllerPrx>();
        this.osPull = new ConcurrentHashMap<Ice.Identity, String>();
        this.modulers = new ConcurrentHashMap<Ice.Identity, Map<String, String>>();

        this.hashes = new HashMap<Identity, Integer>();

        this.adapter = communicator.createObjectAdapter(Defaults.DEFAULT_ADAPTER_NAME);

        this.adapter.add(new DiscovererAdapter(new Discoverer(this)), communicator.stringToIdentity(Discoverer.class.getSimpleName()));
        this.adapter.activate();

        Thread self = new Thread(this);
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

                enviroment.put(context.get("hostname"), identity);
                
                osPull.put(identity, context.get("os"));

                String proxy = Identities.toString(identity) + ": " + context.get("primary");

                ISessionierPrx remoteSessionier = ISessionierPrxHelper.checkedCast(communicator.stringToProxy(proxy));

                IUserSessionPrx remoteSessionPrx = remoteSessionier.createUserSession(identity(), null);
                sessions.put(identity, remoteSessionPrx);

                IHosterPrx remoteHoster = remoteSessionPrx.hoster();
                hosters.put(identity, remoteHoster);
                
                IModulerPrx remoteModuler = remoteSessionPrx.moduler();
                modulers.put(identity, remoteModuler.fetch());

                changed = true;
            }

            if ((hashes.get(identity) == null ? 0 : hashes.get(identity)) != context.hashCode()) {

                hashes.put(identity, context.hashCode());

                changed = true;
            }

            if (changed) {
                setChanged();
                notifyObservers();
            }
        }
    }

    public Set<String> hosts() {
        return hosts;
    }
    
    public Map<Ice.Identity, String> osPull() {
        return osPull;
    }

    public boolean died(Ice.Identity identity) {
        return !enviroment.containsValue(identity);
    }

    public Map<String, Ice.Identity> enviroment() {
        return Collections.unmodifiableMap(enviroment);
    }

    public Map<String, String> cache(Ice.Identity identity) {
        return cache.get(identity);
    }

    public IUserSessionPrx session(Ice.Identity identity) {
        return sessions.get(identity);
    }

    public IHosterPrx hoster(Ice.Identity identity) {
        return hosters.get(identity);
    }

    public IControllerPrx controller(Ice.Identity identity) {
        return controllers.get(identity);
    }
    
    public Map<String, String> moduler(Ice.Identity _identity) {
        return modulers.get(_identity);
    }

    public void run() {

        for (;;) {

            boolean changed = false;
            for (String host : hosts) {
                Ice.Identity id = enviroment.get(host);

                if (id == null) continue;

                if (System.currentTimeMillis() - life.get(id) > 10000) {
                    enviroment.remove(host);

                    changed = true;
                }
            }

            if (changed) {
                setChanged();
                notifyObservers();
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                
            }
        }

    }
}
