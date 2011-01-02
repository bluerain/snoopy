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

import org.apache.log4j.Logger;

import com.googlecode.snoopyd.core.Identity;
import com.googlecode.snoopyd.core.Kernel;

public class Discoverer extends AbstractDriver implements Driver, Activable, Runnable {

	private static Logger logger = Logger.getLogger(Discoverer.class);

	public static final int DISCOVER_INTERVAL = 5000;

	private Thread self;

	public Discoverer(Kernel kernel) {
		super(kernel);
		this.self = new Thread(this);
	}

	public void discover(Identity uuid) {
		logger.info("discover recived by " + uuid);
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

				logger.info("sending discover");
				multicast.discover();
				self.sleep(DISCOVER_INTERVAL);
			}

		} catch (Exception ex) {

		}

	}

}
