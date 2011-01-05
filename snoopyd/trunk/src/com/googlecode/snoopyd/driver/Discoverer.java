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

package com.googlecode.snoopyd.driver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

import Ice.Identity;

import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.util.Identities;

public class Discoverer extends AbstractDriver implements Driver, Activable, Runnable {

	private static Logger logger = Logger.getLogger(Discoverer.class);

	public static final String NAME = "discoverer";
	public static final int DISCOVER_INTERVAL = 5000;

	private ConcurrentMap<Ice.Identity, Kernel.KernelInfo> cache;
	
	private Thread self;

	public Discoverer(String name, Kernel kernel) {
		super(name, kernel);
		
		this.self = new Thread(this);
		this.cache = new ConcurrentHashMap<Identity, Kernel.KernelInfo>();
	}

	public void discover(Kernel.KernelInfo info) {
		cache.put(info.identity, info);
		
		logger.info("recive discover with info = " +  info); 
	}
	
	public Map<Ice.Identity, Kernel.KernelInfo> cache() {
		return Collections.unmodifiableMap(cache);
	}

	@Override
	public void activate() {
		self.start();
	}

	@Override
	public void deactivate() {
		//self.destroy();
	}

	@Override
	public void run() {

		IDiscovererPrx multicast = IDiscovererPrxHelper.uncheckedCast(kernel
				.communicator().propertyToProxy("Discoverer.Multicast"));
		multicast = IDiscovererPrxHelper.checkedCast(multicast.ice_datagram());
		
		try {
	
			while(true) {

				Map<String, String> context = new HashMap<String, String>();
				
				context.put("identity", Identities.toString(kernel.identity()));
				context.put("rate", String.valueOf(kernel.rate()));
				context.put("primary", kernel.primaryEndpoins());
				context.put("secondary", kernel.secondaryEndpoints());
				context.put("state", kernel.state().getClass().getSimpleName());
				context.put("mode", kernel.mode().getClass().getSimpleName());
				
				multicast.discover(kernel.identity(), context);
				
				self.sleep(DISCOVER_INTERVAL);
			}

		} catch (Exception ex) {
			logger.error("something went wrong with " + Discoverer.class);
			logger.error(ex.getMessage());
		}
	}
}
