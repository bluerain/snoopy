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

import org.apache.log4j.Logger;

import Ice.Identity;

import com.googlecode.snoopyd.Defaults;
import com.googlecode.snoopyd.adapter.AdapterManager;
import com.googlecode.snoopyd.adapter.DiscovererAdapter;
import com.googlecode.snoopyd.config.Configuration;
import com.googlecode.snoopyd.driver.Activable;
import com.googlecode.snoopyd.driver.Discoverer;
import com.googlecode.snoopyd.driver.Driver;
import com.googlecode.snoopyd.driver.DriverManager;
import com.googlecode.snoopyd.driver.Loadable;
import com.googlecode.snoopyd.util.Identities;

public class Kernel implements Loadable, Activable {

	private static Logger logger = Logger.getLogger(Kernel.class);

	private Identity identity;

	private Ice.Communicator communicator;
	private Ice.Properties properties;

	private Ice.ObjectAdapter primary;
	private Ice.ObjectAdapter secondary;

	private Configuration configuration;

	private DriverManager driverManager;
	private AdapterManager adapterManager;

	public Kernel(Ice.Communicator communicator) {

		// ConfigurationBuilder builder = new ConfigurationBuilder();
		// Configuration configuration = builder.rate(10).build();

		this.communicator = communicator;
		this.properties = communicator.getProperties();

		this.identity = Identities.randomIdentity(properties
				.getProperty("Snoopy.Domain"));

		logger.info("init driver manager");
		initDriverManager();
		
		logger.info("init adapter manager");
		initAdapterManager();

		logger.info("init primary adapter");
		initPrimaryAdapter();
		logger.info("primary adapter endpoints is a \"" + primaryEndpoins() + "\"");
		
		logger.info("init secondary adapter");
		initSecondaryAdapter();
		logger.info("primary secondary endpoints is a \"" + secondaryEndpoints() + "\"");
	}

	public Identity identity() {
		return identity;
	}

	public int rate() {
		return properties.getPropertyAsInt("Snoopy.Rate");
	}
	
	public String primaryEndpoins() {
		return primary.getPublishedEndpoints()[primary.getPublishedEndpoints().length - 1]._toString();
	}
	
	public String secondaryEndpoints() {
		return secondary.getPublishedEndpoints()[secondary.getPublishedEndpoints().length - 1]._toString();
	}

	public Ice.Communicator communicator() {
		return communicator;
	}

	public Ice.Properties properties() {
		return properties;
	}

	public void load() {
		driverManager.loadAll();
	}

	public void unload() {
		driverManager.unloadAll();
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
		communicator.waitForShutdown();
	}

	public void stop() {
		communicator.destroy();
	}

	public Collection<Driver> drivers() {
		return driverManager.getAll();
	}

	private void initDriverManager() {
		driverManager = new DriverManager();

		driverManager.add(Discoverer.class, new Discoverer(Discoverer.NAME,
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

	private void initPrimaryAdapter() {
		primary = communicator
				.createObjectAdapter(Defaults.DEFAULT_PRIMARY_ADAPTER_NAME);

	}

	private void initSecondaryAdapter() {
		secondary = communicator
				.createObjectAdapter(Defaults.DEFAULT_SECONDARY_ADAPTER_NAME);

		secondary.add((Ice.Object) adapterManager.get(DiscovererAdapter.class),
				adapterManager.get(DiscovererAdapter.class).identity());
	}

}
