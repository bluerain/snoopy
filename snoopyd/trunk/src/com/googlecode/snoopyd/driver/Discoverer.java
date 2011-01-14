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

import com.googlecode.snoopyd.Defaults;
import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.event.DiscoverRecivedEvent;
import com.googlecode.snoopyd.core.state.KernelListener;
import com.googlecode.snoopyd.core.state.KernelState;
import com.googlecode.snoopyd.core.state.OfflineState;
import com.googlecode.snoopyd.core.state.OnlineState;
import com.googlecode.snoopyd.util.Identities;

public class Discoverer extends AbstractDriver implements Driver, Runnable,
		Startable, KernelListener {

	private static Logger logger = Logger.getLogger(Discoverer.class);

	private Thread self;

	private boolean started;

	public Discoverer(Kernel kernel) {
		super(Discoverer.class.getSimpleName(), kernel);
		this.started = false;
	}

	public void discover(Ice.Identity identity, Map<String, String> context) {
		logger.debug("recive discover from " + Identities.toString(identity));

		kernel.handle(new DiscoverRecivedEvent(identity, context));
	}

	@Override
	public synchronized void start() {

		logger.debug("starting " + name);

		started = true;
		self = new Thread(this);
		self.start();
	}

	@Override
	public synchronized void stop() {

		logger.debug("stoping " + name);

		started = false;

		try {
			wait();
		} catch (InterruptedException e) {
			logger.warn(e.getMessage());
		}
	}

	@Override
	public synchronized void restart() {

		logger.debug("restarting " + name);

		stop();
		start();
	}

	@Override
	public boolean started() {
		return started;
	}

	@Override
	public void run() {

		IDiscovererPrx multicast = IDiscovererPrxHelper.uncheckedCast(kernel
				.communicator().propertyToProxy("Discoverer.Multicast"));
		multicast = IDiscovererPrxHelper.checkedCast(multicast.ice_datagram());

		try {

			for (; started;) {

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

				Thread.sleep(Defaults.DEFAULT_DISCOVER_INTERVAL);
			}

		} catch (InterruptedException ex) {
			logger.warn(ex.getMessage());
		}

		synchronized (this) {
			notify();
		}
	}

	@Override
	public void stateChanged(KernelState currentState) {
		if (currentState instanceof OnlineState) {
			if (!started) {
				start();
			}
		} else if (currentState instanceof OfflineState) {
			if (started) {
				stop();
			}
		}
	}
}
