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
import java.util.Map;

import org.apache.log4j.Logger;

import Ice.Identity;

import com.googlecode.snoopyd.Defaults;
import com.googlecode.snoopyd.adapter.AdapterManager;
import com.googlecode.snoopyd.adapter.DiscovererAdapter;
import com.googlecode.snoopyd.driver.Activable;
import com.googlecode.snoopyd.driver.Discoverer;
import com.googlecode.snoopyd.driver.Driver;
import com.googlecode.snoopyd.driver.DriverManager;
import com.googlecode.snoopyd.driver.Loadable;
import com.googlecode.snoopyd.driver.Sessionier;
import com.googlecode.snoopyd.session.IKernelSessionPrx;
import com.googlecode.snoopyd.session.IKernelSessionPrxHelper;
import com.googlecode.snoopyd.session.ISessionManagerPrx;
import com.googlecode.snoopyd.session.ISessionManagerPrxHelper;
import com.googlecode.snoopyd.session.KernelSession;
import com.googlecode.snoopyd.session.KernelSessionAdapter;
import com.googlecode.snoopyd.session.SessionManager;
import com.googlecode.snoopyd.session.SessionManagerAdapter;
import com.googlecode.snoopyd.util.Identities;

public class Kernel implements Loadable, Activable {

	public static final int ACTIVE_MODE = 0;
	public static final int PASSIVE_MODE = 1;

	public static final int WAITING_STATE = 0;
	public static final int SEVERING_STATE = 1;

	public static interface KernelMode {

		public void waiting();

		public void severing();
		
		public void terminating();

	}

	public static class ActiveMode implements KernelMode {

		private Kernel kernel;

		public ActiveMode(Kernel kernel) {
			this.kernel = kernel;
		}

		@Override
		public void waiting() {

			kernel.await();
		}

		@Override
		public void severing() {

			kernel.await();

		}

		@Override
		public void terminating() {
			
		}
	}

	public static class PassiveMode implements KernelMode {

		private Kernel kernel;

		public PassiveMode(Kernel kernel) {
			this.kernel = kernel;
		}

		@Override
		public void waiting() {
			/**
			 * try to connect to node with max rate
			 */
			boolean stateChanged = false;

			while (!stateChanged) {

				int targetRate = 0;
				Ice.Identity targetId = null;

				Discoverer discoverer = (Discoverer) kernel
						.driver(Discoverer.class);
				Map<Ice.Identity, KernelInfo> cache = discoverer.cache();

				if (cache.size() == 0) {

					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						logger.info(e.getMessage());
					}

					continue;
				}

				for (Ice.Identity identity : cache.keySet()) {
					KernelInfo info = cache.get(identity);

					if (info.rate > targetRate) {
						targetRate = info.rate;
						targetId = info.identity;
					}
				}

				KernelInfo targetInfo = cache.get(targetId);

				String proxy = Identities.toString(targetInfo.identity) + ": "
						+ targetInfo.primary;

				ISessionManagerPrx prx = ISessionManagerPrxHelper
						.checkedCast(kernel.communicator().stringToProxy(proxy));

				IKernelSessionPrx selfSession = IKernelSessionPrxHelper
						.uncheckedCast(kernel.primary().addWithUUID(
								new KernelSessionAdapter(new KernelSession(
										kernel))));

				IKernelSessionPrx remoteSession = prx.createKernelSession(
						kernel.identity(), selfSession);

				// TODO: add remote to session manager as parent

				kernel.toogle(new SeveringState(kernel));

				stateChanged = true;
			}
		}

		@Override
		public void severing() {

			kernel.await();

		}

		@Override
		public void terminating() {
			
		}
	}

	public static interface KernelState extends Runnable {
		public void run();
	}

	public static class WaitingState implements KernelState {

		private Kernel kernel;

		public WaitingState(Kernel kernel) {
			this.kernel = kernel;
		}

		@Override
		public void run() {
			kernel.mode().waiting();
		}
	}

	public static class SeveringState implements KernelState {

		private Kernel kernel;

		public SeveringState(Kernel kernel) {
			this.kernel = kernel;
		}

		@Override
		public void run() {
			kernel.mode().severing();
		}
	}

	public static class TerminatingState implements KernelState {

		private Kernel kernel;

		public TerminatingState(Kernel kernel) {
			this.kernel = kernel;
		}

		@Override
		public void run() {
			
		}
	}

	/**
	 * TODO: impl builder for this class
	 */
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

	private static Logger logger = Logger.getLogger(Kernel.class);

	private KernelInfo info;

	private Identity identity;

	private Ice.Communicator communicator;
	private Ice.Properties properties;

	private Ice.ObjectAdapter primary;
	private Ice.ObjectAdapter secondary;

	// private Configuration configuration;

	private DriverManager driverManager;
	private AdapterManager adapterManager;
	private SessionManager sessionManager;

	private Thread self;

	private KernelState kernelState;
	private KernelMode kernelMode;

	public Kernel(Ice.Communicator communicator) {

		// ConfigurationBuilder builder = new ConfigurationBuilder();
		// Configuration configuration = builder.rate(10).build();

		this.kernelState = new WaitingState(this);
		this.kernelMode = new PassiveMode(this);

		this.communicator = communicator;
		this.properties = communicator.getProperties();

		this.identity = Identities.randomIdentity(properties
				.getProperty("Snoopy.Domain"));

		logger.info("init driver manager");
		initDriverManager();

		logger.info("init adapter manager");
		initAdapterManager();

		logger.info("init session manager");
		initSessionManager();

		logger.info("init primary adapter");
		initPrimaryAdapter();
		logger.info("primary adapter endpoints is a \"" + primaryEndpoins()
				+ "\"");

		logger.info("init secondary adapter");
		initSecondaryAdapter();
		logger.info("primary secondary endpoints is a \""
				+ secondaryEndpoints() + "\"");

		this.info = new KernelInfo(identity(), rate(), primaryEndpoins(),
				secondaryEndpoints(), String.valueOf(state().getClass()
						.getSimpleName()), mode().getClass().getSimpleName());
	}

	public Identity identity() {
		return identity;
	}

	public int rate() {
		return properties.getPropertyAsInt("Snoopy.Rate");
	}

	public String primaryEndpoins() {
		return primary.getPublishedEndpoints()[primary.getPublishedEndpoints().length - 1]
				._toString();
	}

	public String secondaryEndpoints() {
		return secondary.getPublishedEndpoints()[secondary
				.getPublishedEndpoints().length - 1]._toString();
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
		return kernelState;
	}

	public KernelMode mode() {
		return kernelMode;
	}

	public void load() {
		driverManager.loadAll();
	}

	public void unload() {
		driverManager.unloadAll();
	}

	public synchronized void toogle(KernelState kernelState) {
		if (this.kernelState.getClass() != kernelState.getClass()) {
			logger.info("changing kernel state on "
					+ kernelState.getClass().getSimpleName());
			this.kernelState = kernelState;
		}
	}

	public synchronized void toogle(KernelMode kernelMode) {
		if (this.kernelMode.getClass() != kernelMode.getClass()) {
			logger.info("changing kernel mode on "
					+ kernelMode.getClass().getSimpleName());
			this.kernelMode = kernelMode;
		}
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
			logger.error("something went wrong while kernel waits: "
					+ e.getMessage());
		}
	}

	public void activate() {
		driverManager.activateAll();
		adapterManager.activateAll();

		primary.activate();
		secondary.activate();
	}

	public void deactivate() {
		driverManager.deactivateAll();
		adapterManager.deactivateAll();

		primary.deactivate();
		primary.deactivate();
	}

	public void start() {

		do {
			loop();			
		} while (!(kernelState instanceof TerminatingState));

	}

	public void stop() {

		toogle(new TerminatingState(this));
		reset();

		communicator.destroy();
	}

	public void loop() {
		try {

			logger.info("new kernel loop: "
					+ kernelState.getClass().getSimpleName() + ", "
					+ kernelMode.getClass().getSimpleName());

			self = new Thread(kernelState);

			self.start();

			self.join();

		} catch (InterruptedException e) {
			logger.error("something went wrong while kernel loop: "
					+ e.getMessage());
		}
	}

	public Driver driver(Class<?> clazz) {
		return driverManager.get(clazz);
	}

	public Collection<Driver> drivers() {
		return driverManager.getAll();
	}

	private void initDriverManager() {
		driverManager = new DriverManager();

		driverManager.add(Discoverer.class, new Discoverer(Discoverer.NAME,
				this));

		driverManager.add(Sessionier.class, new Sessionier(Sessionier.NAME,
				this));

	}

	private void initAdapterManager() {
		adapterManager = new AdapterManager();

		adapterManager.add(
				DiscovererAdapter.class,
				new DiscovererAdapter(DiscovererAdapter.NAME, Identities
						.stringToIdentity(DiscovererAdapter.NAME),
						(Discoverer) driverManager.get(Discoverer.class)));

	}

	private void initSessionManager() {
		sessionManager = new SessionManager(this);
	}

	private void initPrimaryAdapter() {
		primary = communicator
				.createObjectAdapter(Defaults.DEFAULT_PRIMARY_ADAPTER_NAME);

		primary.add(new SessionManagerAdapter(sessionManager), identity());

	}

	private void initSecondaryAdapter() {
		secondary = communicator
				.createObjectAdapter(Defaults.DEFAULT_SECONDARY_ADAPTER_NAME);

		secondary.add((Ice.Object) adapterManager.get(DiscovererAdapter.class),
				adapterManager.get(DiscovererAdapter.class).identity());
	}
}
