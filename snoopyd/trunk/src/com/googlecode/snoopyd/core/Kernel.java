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
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import Ice.Identity;

import com.googlecode.snoopyd.Defaults;
import com.googlecode.snoopyd.adapter.AdapterManager;
import com.googlecode.snoopyd.adapter.DiscovererAdapter;
import com.googlecode.snoopyd.core.event.KernelEvent;
import com.googlecode.snoopyd.core.state.KernelState;
import com.googlecode.snoopyd.core.state.SuspenseState;
import com.googlecode.snoopyd.driver.Activable;
import com.googlecode.snoopyd.driver.Aliver;
import com.googlecode.snoopyd.driver.Discoverer;
import com.googlecode.snoopyd.driver.Driver;
import com.googlecode.snoopyd.driver.DriverManager;
import com.googlecode.snoopyd.driver.Loadable;
import com.googlecode.snoopyd.driver.Networker;
import com.googlecode.snoopyd.driver.Restartable;
import com.googlecode.snoopyd.manager.Manager;
import com.googlecode.snoopyd.session.SessionManager;
import com.googlecode.snoopyd.session.SessionManagerAdapter;
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

	private boolean stateChanged;
	private boolean modeChanged;

	private Map<Class<?>, Manager> managers;

	private Thread self;

	private KernelState state;

	private ConcurrentLinkedQueue<KernelEvent> pool;

	public Kernel(Ice.Communicator communicator) {

		// ConfigurationBuilder builder = new ConfigurationBuilder();
		// Configuration configuration = builder.rate(10).build();

		this.pool = new ConcurrentLinkedQueue<KernelEvent>();

		this.state = new SuspenseState(this);

		this.communicator = communicator;
		this.properties = communicator.getProperties();

		this.identity = Identities.randomIdentity(properties
				.getProperty("Snoopy.Domain"));

		this.managers = new HashMap<Class<?>, Manager>();

		logger.debug("init driver manager");
		initDriverManager();

		logger.debug("init adapter manager");
		initAdapterManager();

		logger.debug("init session manager");
		initSessionManager();

		logger.debug("init primary adapter");
		initPrimaryAdapter();
		logger.info("primary adapter endpoints is a \""
				+ primaryPublishedEndpoints() + "\"");

		logger.debug("init secondary adapter");
		initSecondaryAdapter();

		logger.debug("primary secondary endpoints is a \""
				+ secondaryPublishedEndpoints() + "\"");

		this.info = new KernelInfo(identity(), rate(),
				primaryPublishedEndpoints(), secondaryPublishedEndpoints(),
				handler().getClass().getSimpleName(), "none");
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

	public KernelState handler() {
		return state;
	}

	public Object mode() {
		return null;
	}

	public void recofigure(KernelConfiguration configuration) {

	}

	@Override
	public void load() {
		((DriverManager) managers.get(DriverManager.class)).loadAll();
	}

	@Override
	public void unload() {
		((DriverManager) managers.get(DriverManager.class)).unloadAll();
	}

	public synchronized void toogle(KernelState kernelState) {

		stateChanged = false;

		if (!stateChanged && this.state.getClass() != kernelState.getClass()) {
			stateChanged = true;

			logger.info("changing kernel state on "
					+ kernelState.getClass().getSimpleName());
			this.state = kernelState;
		} else {
			logger.debug("can not change state on "
					+ kernelState.getClass().getSimpleName()
					+ ", because state already changed in this loop");
		}

	}

	// public synchronized void toogle(KernelMode kernelMode) {
	//
	// if (!modeChanged && this.kernelMode.getClass() != kernelMode.getClass())
	// {
	// modeChanged = true;
	//
	// logger.info("changing kernel mode on "
	// + kernelMode.getClass().getSimpleName());
	//
	// this.kernelMode = kernelMode;
	// } else {
	// logger.debug("can not change mode on "
	// + state.getClass().getSimpleName()
	// + ", because model already changed in this loop");
	// }
	//
	// }

	public synchronized void restart() {

		logger.debug("reseting kernel");

		((Restartable) managers.get(SessionManager.class)).restart();
		((Restartable) managers.get(DriverManager.class)).restart();
		((Restartable) managers.get(AdapterManager.class)).restart();

		stateChanged = false;
		modeChanged = false;

		// kernelMode = new SuspenseMode(this);
		// state = new StartingState(this);

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
		((DriverManager) managers.get(DriverManager.class)).activateAll();
		((AdapterManager) managers.get(AdapterManager.class)).activateAll();

		primary.activate();
		secondary.activate();
	}

	public void deactivate() {
		((DriverManager) managers.get(DriverManager.class)).deactivateAll();
		((AdapterManager) managers.get(AdapterManager.class)).deactivateAll();

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
		return ((DriverManager) managers.get(DriverManager.class)).get(clazz);
	}

	public Collection<Driver> drivers() {
		return ((DriverManager) managers.get(DriverManager.class)).getAll();
	}

	public Manager manager(Class<?> clazz) {
		return managers.get(clazz);
	}

	public Collection<Manager> managers() {
		return Collections.unmodifiableCollection(managers.values());
	}

	private void initDriverManager() {
		DriverManager driverManager = new DriverManager(DriverManager.NAME,
				this);

		driverManager.add(Discoverer.class, new Discoverer(Discoverer.NAME,
				this));

		driverManager.add(Aliver.class, new Aliver(Aliver.NAME, this));

		driverManager.add(Networker.class, new Networker(Networker.NAME, this));

		managers.put(DriverManager.class, driverManager);
	}

	private void initAdapterManager() {
		AdapterManager adapterManager = new AdapterManager(AdapterManager.NAME,
				this);

		adapterManager.add(
				DiscovererAdapter.class,
				new DiscovererAdapter(DiscovererAdapter.NAME, Identities
						.stringToIdentity(DiscovererAdapter.NAME),
						(Discoverer) ((DriverManager) managers
								.get(DriverManager.class))
								.get(Discoverer.class)));

		managers.put(AdapterManager.class, adapterManager);
	}

	private void initSessionManager() {
		SessionManager sessionManager = new SessionManager(SessionManager.NAME,
				this);

		managers.put(SessionManager.class, sessionManager);
	}

	private void initPrimaryAdapter() {
		primary = communicator
				.createObjectAdapter(Defaults.DEFAULT_PRIMARY_ADAPTER_NAME);

		primary.add(
				new SessionManagerAdapter((SessionManager) managers
						.get(SessionManager.class)), identity());
	}

	private void initSecondaryAdapter() {
		secondary = communicator
				.createObjectAdapter(Defaults.DEFAULT_SECONDARY_ADAPTER_NAME);

		secondary.add(
				(Ice.Object) ((AdapterManager) managers
						.get(AdapterManager.class))
						.get(DiscovererAdapter.class),
				((AdapterManager) managers.get(AdapterManager.class)).get(
						DiscovererAdapter.class).identity());
	}
}
