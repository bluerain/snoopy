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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import org.apache.log4j.Logger;

import Ice.Identity;

import com.googlecode.snoopyd.Defaults;
import com.googlecode.snoopyd.adapter.Adapter;
import com.googlecode.snoopyd.adapter.DiscovererAdapter;
import com.googlecode.snoopyd.adapter.SessionierAdapter;
import com.googlecode.snoopyd.core.event.KernelEvent;
import com.googlecode.snoopyd.core.filter.KernelFilter;
import com.googlecode.snoopyd.core.filter.KernelFilter.FilterAction;
import com.googlecode.snoopyd.core.filter.ToogleFilter;
import com.googlecode.snoopyd.core.state.KernelListener;
import com.googlecode.snoopyd.core.state.KernelState;
import com.googlecode.snoopyd.core.state.SuspenseState;
import com.googlecode.snoopyd.driver.Activable;
import com.googlecode.snoopyd.driver.Aliver;
import com.googlecode.snoopyd.driver.Controller;
import com.googlecode.snoopyd.driver.Discoverer;
import com.googlecode.snoopyd.driver.Driver;
import com.googlecode.snoopyd.driver.Hoster;
import com.googlecode.snoopyd.driver.Invoker;
import com.googlecode.snoopyd.driver.Moduler;
import com.googlecode.snoopyd.driver.Networker;
import com.googlecode.snoopyd.driver.Resulter;
import com.googlecode.snoopyd.driver.Scheduler;
import com.googlecode.snoopyd.driver.Sessionier;
import com.googlecode.snoopyd.driver.Startable;
import com.googlecode.snoopyd.module.Module;
import com.googlecode.snoopyd.session.ISessionPrx;
import com.googlecode.snoopyd.util.Identities;
import com.googlecode.snoopymm.IModuleManagerPrx;
import com.googlecode.snoopymm.IModuleManagerPrxHelper;
import com.googlecode.snoopymm.ModuleNotFoundException;

public class Kernel implements Runnable {
	
	public static class KernelException extends RuntimeException {

		public KernelException() {
			super();
		}

		public KernelException(String msg) {
			super(msg);
		}
	}
	
	public static enum KernelStatus {
		NETWORKABLE, MODULABLE		
	}

	public static Logger logger = Logger.getLogger(Kernel.class);
	
	private Identity identity;

	private Ice.Communicator communicator;
	private Ice.Properties properties;

	private Ice.ObjectAdapter primary;
	private Ice.ObjectAdapter secondary;

	private Thread self;

	private KernelState state;
	
	private EnumSet<KernelStatus> statuses;
	
	private int rate;
	
	private Map<String, String> context;

	private Queue<KernelEvent> pool;

	private HashMap<Class<?>, Driver> drivers;
	private HashMap<Class<?>, Adapter> adapters;

	private Map<Ice.Identity, ISessionPrx> parents;
	private Map<Ice.Identity, ISessionPrx> childs;

	private Map<Ice.Identity, Map<String, String>> cache;

	private List<KernelListener> kernelListeners;
	private List<KernelFilter> kernelFilters;
	
	private HashMap<UUID, Module> modules; 
	
	private IModuleManagerPrx moduleManager;
	
	public Kernel(Ice.Communicator communicator) throws KernelException {

		this.statuses = EnumSet.noneOf(KernelStatus.class);
		
		this.rate = Integer.MIN_VALUE;
		
		this.context = new HashMap<String, String>();
		
		this.pool = new LinkedList<KernelEvent>();

		this.cache = new HashMap<Identity, Map<String, String>>();
		
		this.parents = new HashMap<Identity, ISessionPrx>();
		this.childs = new HashMap<Identity, ISessionPrx>();

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
		
		logger.debug("init kernel filters");
		initKernelFilters();

		logger.debug("init kernel rate");
		initKernelRate();
		
		logger.debug("starting kernel thread");
		self = new Thread(this, Defaults.KERNEL_THREAD_NAME);
		self.start();
	}
	
	public String hostname() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException ex) {
			return "localhost";
		}
	}
	
	public String os() {
		return System.getProperty("os.name");
	}
	
	public Map<String, String> context() {
		return context;
	}
	
	public Identity identity() {
		return identity;
	}

	public int rate() {
		return rate;
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
	
	public IModuleManagerPrx moduleManager() {
		return moduleManager;
	}

	public void toogle(KernelState kernelState) {

		checkKernelThread();
		
		if (state.getClass() != kernelState.getClass()) {
			
			logger.info("changing kernel state on "
					+ kernelState.getClass().getSimpleName());

			state = kernelState;

			for (KernelListener listener : kernelListeners) {
				listener.stateChanged(kernelState);
			}
		}
	}
	
	public void enable(KernelStatus status) {

		checkKernelThread();
		
		statuses.add(status);

	}
	
	public void disable(KernelStatus status) {
		
		checkKernelThread();
	
		statuses.remove(status);
	}
	
	public EnumSet<KernelStatus> statuses() {
		return statuses;
	}

	public void init() {

		checkKernelThread();
		
		logger.debug("init kernel");

		for (Driver drv : drivers.values()) {
			if (drv instanceof Activable) {
				logger.debug("... activating " + drv.name());
				((Activable) drv).activate();
			}
		}
		
//		for (Driver drv: drivers.values()) {
//			
//			if (drv instanceof Startable) {
//				logger.debug("... starting " + drv.name());
//				((Startable) drv).start();
//			}
//		}
		
		primary.activate();
		secondary.activate();
	}
	
	
	public void dispose() {
		
		checkKernelThread();
			
		logger.debug("dispose kernel");
		
		for (Driver drv : drivers.values()) {
			if (drv instanceof Activable) {
				logger.debug("... deactivating " + drv.name());
				((Activable) drv).deactivate();
			}
		}
		
		for (Driver drv: drivers.values()) {
			if (drv instanceof Startable) {
				if (((Startable) drv).started()) {
					logger.debug("... stopping " + drv.name());
					((Startable) drv).stop();
				}
			}
		}
		
		primary.deactivate();
		secondary.deactivate();
		
		self.interrupt();
	}
	
	public void waitForTerminated() {
		
		try {
			self.join();		
		} catch (InterruptedException ignored) { 
		}
		
	}
	
	@Override
	public void run() {

		for (;;) {

			for (; !pool.isEmpty();) {
				
				KernelEvent event = pool.poll();
				
				boolean eventFiltered = false;
				KernelFilter usedFilter = null;
				
				for (KernelFilter filter: kernelFilters) {
					if (FilterAction.REJECT == filter.accept(event)) {
						eventFiltered = true;
						usedFilter = filter;
						break;
					}
				}
				
				if (eventFiltered) {
					logger.debug("filter " + event.name() + " with " + usedFilter.getClass().getSimpleName());
				} else {
					logger.debug("handle " + event.name() + " with " + state.handler().getClass().getSimpleName());
					state.handler().handle(event);
				}
			}

			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException ignored) {
				}
			}
			
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
	
	public Collection<KernelListener> listiners() {
		return Collections.unmodifiableCollection(kernelListeners);
	}

	public Map<Ice.Identity, Map<String, String>> cache() {
		return cache;
	}

	public Map<Ice.Identity, ISessionPrx> parents() {
		return parents;
	}

	public Map<Ice.Identity, ISessionPrx> childs() {
		return childs;
	}
	
	private void checkKernelThread() {
		if (Thread.currentThread() != self) {
			throw new KernelException("only kernel thread can change kernel state");
		}
	}
	
	private void initDrivers() {

		drivers = new HashMap<Class<?>, Driver>();

		drivers.put(Sessionier.class, new Sessionier(this));
		drivers.put(Discoverer.class, new Discoverer(this));
		drivers.put(Aliver.class, new Aliver(this));
		drivers.put(Networker.class, new Networker(this));
		drivers.put(Hoster.class, new Hoster(this));
		drivers.put(Controller.class, new Controller(this));
		drivers.put(Scheduler.class, new Scheduler(this));
		drivers.put(Resulter.class, new Resulter(this));
		drivers.put(Moduler.class, new Moduler(this));
		drivers.put(Invoker.class, new Invoker(this));
		
	}

	private void initAdapters() {

		adapters = new HashMap<Class<?>, Adapter>();

		adapters.put(
				DiscovererAdapter.class,
				new DiscovererAdapter(Identities
						.stringToIdentity(Discoverer.class
								.getSimpleName()), (Discoverer) drivers
						.get(Discoverer.class)));

		adapters.put(SessionierAdapter.class, new SessionierAdapter(identity,
				(Sessionier) drivers.get(Sessionier.class)));
	}

	private void initPrimaryAdapter() {
		primary = communicator
				.createObjectAdapter(Defaults.PRIMARY_ADAPTER_NAME);

		primary.add((Ice.Object) adapters.get(SessionierAdapter.class),
				((Adapter) adapters.get(SessionierAdapter.class)).identity());
	}

	private void initSecondaryAdapter() {
		secondary = communicator
				.createObjectAdapter(Defaults.SECONDARY_ADAPTER_NAME);

		secondary.add((Ice.Object) adapters.get(DiscovererAdapter.class),
				((Adapter) adapters.get(DiscovererAdapter.class)).identity());
	}

	private void initKernelListeners() {
		kernelListeners = new LinkedList<KernelListener>();

		for (Driver driver : drivers.values()) {
			if (driver instanceof KernelListener) {
				kernelListeners.add((KernelListener) driver);
			}
		}
	}
	
	private void initKernelFilters() {
		kernelFilters = new LinkedList<KernelFilter>();
		
		kernelFilters.add(new ToogleFilter(this));
	}
	
	private void initKernelRate() {
		Hoster hoster = (Hoster) drivers.get(Hoster.class);
		Map<String, String> context = hoster.context();
		
		int ram = Integer.parseInt(context.get("Ram"));
		int mhz = Integer.parseInt(context.get("Mhz"));
		
		rate = (int) (((ram * 0.5 + mhz * 0.5) / Defaults.BASELINE_RATE) * 10);
	}
	
//	private void initKernelModules() {
//	
//		modules = new HashMap<UUID, Module>();
//	
//		String modulesDir  = properties.getProperty("Snoopy.ModulesDir");
//		// String modulesConfig = kernel.properties().getProperty("Snoopy.ModulesConfig");
//		
//		File dir = new File(modulesDir);  
//		String[] list = dir.list();
//		
//		for (String module: list) {
//			
//			if (module.indexOf(".py") != -1) {
//				logger.debug(module);
//			}
//		}
//	}
	
	public void initModuleManager() {
		
		checkKernelThread();
		
		try {
			
			moduleManager = IModuleManagerPrxHelper.checkedCast(communicator.propertyToProxy("ModuleManager.Proxy"));
			
		} catch (Ice.ConnectionRefusedException ex) {
			throw new KernelException("could not connect to module manager");
		}
	}
	
	public void disposeModuleManager() {
		
		checkKernelThread();

		moduleManager = null;
	}
}
