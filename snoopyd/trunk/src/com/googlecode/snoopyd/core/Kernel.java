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

import org.apache.log4j.Logger;

import Ice.Communicator;

import com.googlecode.snoopyd.driver.Activable;
import com.googlecode.snoopyd.driver.Discoverer;
import com.googlecode.snoopyd.driver.DiscovererAdapter;
import com.googlecode.snoopyd.driver.Loadable;

public class Kernel implements Loadable, Activable {

	private static Logger logger = Logger.getLogger(Kernel.class);

	private Communicator communicator;

	private Identity uuid;

	private Ice.ObjectAdapter udpAdapter;
	private Ice.ObjectAdapter tcpAdapter;

	private DriverManager driverManager;
	private AdapterManager adapterManager;

	public Kernel(Ice.Communicator communicator) {

		this.communicator = communicator;

		this.uuid = new Identity(communicator.getProperties().getProperty(
				"Snoopy.Domain"));

		this.driverManager = new DriverManager();
		this.driverManager.add(Discoverer.class, new Discoverer(this));

		this.adapterManager = new AdapterManager(this);

		this.udpAdapter = communicator.createObjectAdapter("UDPAdapter");
		//this.tcpAdapter = communicator.createObjectAdapter("TCPAdapter");

		this.udpAdapter.add(
				new DiscovererAdapter(new Discoverer(this)), communicator
						.stringToIdentity("discoverer"));
	}

	public Identity UUID() {
		return uuid;
	}
	
	public Communicator communicator() {
		return communicator;
	}
	
	public void load() {
		driverManager.load();
	}
	
	public void unload() {
		
	}

	public void activate() {

		driverManager.activate();
		
		udpAdapter.activate();
		// tcpAdapter.activate();

		communicator.waitForShutdown();
	}

	public void deactivate() {
		udpAdapter.deactivate();
		// tcpAdapter.deactivate();

		communicator.destroy();
	}

}
