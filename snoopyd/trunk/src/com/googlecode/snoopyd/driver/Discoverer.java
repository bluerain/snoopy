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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.event.DiscoverRecivedEvent;
import com.googlecode.snoopyd.core.state.KernelListener;
import com.googlecode.snoopyd.core.state.KernelState;
import com.googlecode.snoopyd.core.state.OnlineState;
import com.googlecode.snoopyd.core.state.SuspenseState;
import com.googlecode.snoopyd.util.Identities;

public class Discoverer extends AbstractDriver implements Driver, Runnable,
		KernelListener {

	private static Logger logger = Logger.getLogger(Discoverer.class);

	public static final int DISCOVER_INTERVAL = 5000;

	private Thread self;

	public Discoverer(Kernel kernel) {
		super(Discoverer.class.getSimpleName(), kernel);
	}

	public void discover(Ice.Identity identity, Map<String, String> context) {
		logger.debug("recive discover from " + Identities.toString(identity));

		kernel.handle(new DiscoverRecivedEvent(identity, context));
	}

	public void start() {

		logger.debug("starting " + name);
		
		self = new Thread(this);
		self.start();
	}

	public void stop() {
		
		logger.debug("stoping " + name);
		
		self.interrupt();
	}

	@Override
	public void run() {

		IDiscovererPrx multicast = IDiscovererPrxHelper.uncheckedCast(kernel
				.communicator().propertyToProxy("Discoverer.Multicast"));
		multicast = IDiscovererPrxHelper.checkedCast(multicast.ice_datagram());

		for (; self.isAlive();) {

			Map<String, String> context = new HashMap<String, String>();

			context.put("identity", Identities.toString(kernel.identity()));
			context.put("rate", String.valueOf(kernel.rate()));
			context.put("primary", kernel.primaryPublishedEndpoints());
			context.put("secondary", kernel.secondaryPublishedEndpoints());
			context.put("state", kernel.state().getClass().getSimpleName());

			try {
				multicast.discover(kernel.identity(), context);
			} catch (Exception ignored) {
			}

			try {
				Thread.sleep(Discoverer.DISCOVER_INTERVAL);
			} catch (InterruptedException ignored) {
			}
		}
	}

	@Override
	public void stateChanged(KernelState currentState) {
		if (currentState instanceof OnlineState) {
			start();
		} else if (currentState instanceof SuspenseState) {
			stop();
		}
	}
}
