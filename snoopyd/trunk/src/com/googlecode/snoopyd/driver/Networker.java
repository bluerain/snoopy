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

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.log4j.Logger;

import com.googlecode.snoopyd.Defaults;
import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.event.NetworkDisabledEvent;
import com.googlecode.snoopyd.core.event.NetworkEnabledEvent;

public class Networker extends AbstractDriver implements Driver, Activable,
		Runnable {

	private static Logger logger = Logger.getLogger(Networker.class);

	public static final int NETWORK_UNDEFINED = -1;
	public static final int NETWORK_ENABLED = 0;
	public static final int NETWORK_DISABLED = 1;

	private int networkState;

	private boolean started;

	public Networker(Kernel kernel) {
		super(Networker.class.getSimpleName(), kernel);

		this.networkState = NETWORK_UNDEFINED;
		this.started = false;
	}

	@Override
	public void run() {

		for (; started;) {

			try {

				boolean checker = false;

				Enumeration<NetworkInterface> interfaces = NetworkInterface
						.getNetworkInterfaces();

				while (interfaces.hasMoreElements()) {
					NetworkInterface nic = interfaces.nextElement();
					try {

						logger.debug("checking " + nic.getDisplayName()
								+ " interface :: " + nic.isUp());

						checker = checker || (nic.isUp() && !nic.isLoopback());

					} catch (SocketException ignored) {
					}
				}

				if (networkState == NETWORK_UNDEFINED) {
					if (checker) {

						logger.debug("network is enabled");

						networkState = NETWORK_ENABLED;

						kernel.handle(new NetworkEnabledEvent());

					} else {

						logger.debug("network is disabled");

						networkState = NETWORK_DISABLED;

						kernel.handle(new NetworkDisabledEvent());

					}

				} else if (networkState == NETWORK_DISABLED) {

					if (checker) {

						logger.debug("network is enabled");

						networkState = NETWORK_ENABLED;

						kernel.handle(new NetworkEnabledEvent());

					} else {

						logger.debug("network still disabled");
					}

				} else if (networkState == NETWORK_ENABLED) {

					if (!checker) {

						logger.debug("network is disabled");

						networkState = NETWORK_DISABLED;

						kernel.handle(new NetworkDisabledEvent());

					} else {

						logger.debug("network still enabled");
					}
				}

			} catch (SocketException ex) {

				if (networkState == NETWORK_UNDEFINED
						|| networkState == NETWORK_ENABLED) {

					logger.debug("network is disabled");

					networkState = NETWORK_DISABLED;

					kernel.handle(new NetworkDisabledEvent());
				}
			}

			try {
				Thread.sleep(Defaults.NETWORKER_INTERVAL);
			} catch (InterruptedException ignored) {
			} 
		}
		
		synchronized (this) {
			notify();
		}
	}

	@Override
	public synchronized void  activate() {

		started = true;

		Thread self = new Thread(this);
		self.start();
	}

	@Override
	public synchronized void deactivate() {

		started = false;

		try {
			wait();
		} catch (InterruptedException e) {
			logger.warn(e.getMessage());
		}
	}
}