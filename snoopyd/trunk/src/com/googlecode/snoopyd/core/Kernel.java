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

package com.googlecode.snoopyd.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import Ice.Identity;

import com.googlecode.snoopyd.Defaults;
import com.googlecode.snoopyd.adapter.Adapter;
import com.googlecode.snoopyd.adapter.DiscovererAdapter;
import com.googlecode.snoopyd.adapter.SessionierAdapter;
import com.googlecode.snoopyd.core.event.KernelEvent;
import com.googlecode.snoopyd.core.state.KernelListener;
import com.googlecode.snoopyd.core.state.KernelState;
import com.googlecode.snoopyd.core.state.SuspenseState;
import com.googlecode.snoopyd.driver.Activable;
import com.googlecode.snoopyd.driver.Aliver;
import com.googlecode.snoopyd.driver.Discoverer;
import com.googlecode.snoopyd.driver.Driver;
import com.googlecode.snoopyd.driver.Loadable;
import com.googlecode.snoopyd.driver.Networker;
import com.googlecode.snoopyd.driver.Restartable;
import com.googlecode.snoopyd.driver.Sessionier;
import com.googlecode.snoopyd.session.ISessionPrx;
import com.googlecode.snoopyd.util.Identities;

public class Kernel implements Loadable, Activable, Restartable, Runnable {

	public static class KernelInfo {

		public final Identity identity;
		public final int rate;
		public final String primary;
		public final String secondary;
		public final String state;
		public final String mode;

		public KernelInfo(Identity identity, int rate, String primary,
				String secondary, String state, String mode) {
			this.identity = identity;
			this.rate = rate;
			this.primary = primary;
			this.secondary = secondary;
			this.state = state;
			this.mode = mode;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append("identity=" + Identities.toString(identity) + ", ");
			sb.append("rate=" + rate + ", ");
			sb.append("primary=" + primary + ", ");
			sb.append("secondary=" + secondary + ", ");
			sb.append("state=" + state + ", ");
			sb.append("mode=" + mode);
			sb.append("]");

			return sb.toString();
		}
	}

	public static class KernelConfiguration {

	}

	private static Logger logger = Logger.getLogger(Kernel.class);

	private KernelInfo info;

	private Identity identity;

	private Ice.Communicator communicator;
	private Ice.Properties properties;

	private Ice.ObjectAdapter primary;
	private Ice.ObjectAdapter secondary;

	// private Configuration configuration;

	private Thread self;

	private KernelState state;

	private ConcurrentLinkedQueue<KernelEvent> pool;

	private HashMap<Class<?>, Driver> drivers;
	private HashMap<Class<?>, Adapter> adapters;

	private Map<Ice.Identity, ISessionPrx> parents;
	private Map<Ice.Identity, ISessionPrx> childs;

	private Map<Ice.Identity, Map<String, String>> cache;

	private List<KernelListener> kernelListeners;

	public Kernel(Ice.Communicator communicator) {

		// ConfigurationBuilder builder = new ConfigurationBuilder();
		// Configuration configuration = builder.rate(10).build();

		this.pool = new ConcurrentLinkedQueue<KernelEvent>();
		this.cache = new HashMap<Identity, Map<String,String>>();
		

		this.state = new SuspenseState(this);

		this.communicator = communicator;
		this.properties = communicator.getProperties();

		this.identity = Identities.randomIdentity(properties
				.getProperty("Snoopy.Domain"));

		logger.debug("init kernel drivers");
		initDrivers();

		logger.debug("init kernel adapters");
		initAdapters();

		logger.debug("init primary ice adapter");
		initPrimaryAdapter();
		logger.info("primary adapter endpoints is a \""
				+ primaryPublishedEndpoints() + "\"");

		logger.debug("init secondary ice adapter");
		initSecondaryAdapter();

		logger.debug("primary secondary endpoints is a \""
				+ secondaryPublishedEndpoints() + "\"");

		logger.debug("init kernel listeners");
		initKernelListeners();

		this.info = new KernelInfo(identity(), rate(),
				primaryPublishedEndpoints(), secondaryPublishedEndpoints(),
				state().getClass().getSimpleName(), "none");
	}

	public Identity identity() {
		return identity;
	}

	public int rate() {
		return properties.getPropertyAsInt("Snoopy.Rate");
	}

	public String primaryPublishedEndpoints() {
		return primary.getPublishedEndpoints()[primary.getPublishedEndpoints().length - 1]
				._toString();
	}

	public String secondaryPublishedEndpoints() {
		return secondary.getPublishedEndpoints()[secondary
				.getPublishedEndpoints().length - 1]._toString();
	}

	public String primaryEndpoints() {
		return primary.getEndpoints()[primary.getEndpoints().length - 1]
				._toString();
	}

	public String secondaryEndpoints() {
		return secondary.getEndpoints()[secondary.getEndpoints().length - 1]
				._toString();
	}

	public KernelInfo kernelInfo() {
		return info;
	}

	public Ice.Communicator communicator() {
		return communicator;
	}

	public Ice.Properties properties() {
		return properties;
	}

	public Ice.ObjectAdapter primary() {
		return primary;
	}

	public Ice.ObjectAdapter secondary() {
		return secondary;
	}

	public KernelState state() {
		return state;
	}

	public Object mode() {
		return null;
	}

	public void recofigure(KernelConfiguration configuration) {

	}

	@Override
	public void load() {
		for (Driver driver: drivers.values()) {
			if (driver instanceof Loadable) {
				((Loadable) driver).load();
			}
		}
	}

	@Override
	public void unload() {
		for (Driver driver: drivers.values()) {
			if (driver instanceof Loadable) {
				((Loadable) driver).unload();
			}
		}
	}

	public synchronized void toogle(KernelState kernelState) {

		if (this.state.getClass() != kernelState.getClass()) {

			logger.info("changing kernel state on "
					+ kernelState.getClass().getSimpleName());

			this.state = kernelState;

			for (KernelListener listener : kernelListeners) {
				listener.stateChanged(kernelState);
			}

		} else {

		}
	}

	public synchronized void restart() {

		logger.debug("reseting kernel");

		reset();
	}

	public synchronized void reset() {
		try {
			notify();
		} catch (IllegalMonitorStateException e) {
			logger.warn("can not reset kernel: " + e.getMessage());
		}
	}

	public synchronized void await() {
		try {

			wait();

		} catch (InterruptedException e) {
			logger.error("wait interrupted");
		}
	}

	public void activate() {
		
		for (Driver driver: drivers.values()) {
			if (driver instanceof Loadable) {
				((Activable) driver).activate();
			}
		}
		
		primary.activate();
		secondary.activate();
	}

	public void deactivate() {
		
		for (Driver driver: drivers.values()) {
			if (driver instanceof Loadable) {
				((Activable) driver).deactivate();
			}
		}

		primary.deactivate();
		primary.deactivate();
	}

	public void start() {

		try {

			self = new Thread(this);
			self.start();
			self.join();

		} catch (InterruptedException ex) {
			logger.error(ex.getMessage());
		}
	}

	public void stop() {

		self.interrupt();
		communicator.destroy();
	}

	@Override
	public void run() {

		for (;;) {

			for (; !pool.isEmpty();) {
				KernelEvent event = pool.poll();

				logger.debug("handle " + event.getClass().getSimpleName());

				state.handler().handle(event);

			}

			await();
		}
	}

	public synchronized void handle(KernelEvent event) {

		pool.offer(event);
		notify();
	}

	public Driver driver(Class<?> clazz) {
		return drivers.get(clazz);
	}

	public Collection<Driver> drivers() {
		return Collections.unmodifiableCollection(drivers.values());
	}

	public Adapter adapter(Class<?> clazz) {
		return adapters.get(clazz);
	}

	public Collection<Adapter> adapters() {
		return Collections.unmodifiableCollection(adapters.values());
	}
	
	public Map<Ice.Identity, Map<String, String>> cache() {
		return Collections.unmodifiableMap(cache);
	}
	
	public Map<Ice.Identity, ISessionPrx> parents() {
		return parents;
	}

	public Map<Ice.Identity, ISessionPrx> childs() {
		return childs;
	}

	private void initDrivers() {

		drivers = new HashMap<Class<?>, Driver>();

		drivers.put(Sessionier.class, new Sessionier(this));
		drivers.put(Discoverer.class, new Discoverer(this));
		drivers.put(Aliver.class, new Aliver(this));
		drivers.put(Networker.class, new Networker(this));
	}

	private void initAdapters() {

		adapters = new HashMap<Class<?>, Adapter>();

		adapters.put(
				DiscovererAdapter.class,
				new DiscovererAdapter(Identities
						.stringToIdentity(DiscovererAdapter.class.getSimpleName()),
						(Discoverer) drivers.get(Discoverer.class)));

		adapters.put(Sessionier.class, new SessionierAdapter(identity, new Sessionier(this)));
	}

	private void initPrimaryAdapter() {
		primary = communicator
				.createObjectAdapter(Defaults.DEFAULT_PRIMARY_ADAPTER_NAME);

		primary.add((Ice.Object) adapters.get(Sessionier.class),
				((Adapter) adapters.get(Sessionier.class)).identity());
	}

	private void initSecondaryAdapter() {
		secondary = communicator
				.createObjectAdapter(Defaults.DEFAULT_SECONDARY_ADAPTER_NAME);

		secondary.add((Ice.Object) adapters.get(DiscovererAdapter.class),
				((Adapter) adapters.get(DiscovererAdapter.class)).identity());
	}

	public void initKernelListeners() {
		kernelListeners = new LinkedList<KernelListener>();

		for (Driver driver : drivers.values()) {
			if (driver instanceof KernelListener) {
				kernelListeners.add((KernelListener) driver);
			}
		}
	}
}
