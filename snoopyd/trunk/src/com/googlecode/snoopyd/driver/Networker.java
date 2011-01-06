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

import com.googlecode.snoopyd.core.Kernel;

public class Networker extends AbstractDriver implements Driver, Activable,
		Runnable, Restartable {

	private static Logger logger = Logger.getLogger(Networker.class);

	public static final String NAME = "networker";
	
	public static final int NETWORK_UNDEFINED = -1;
	public static final int NETWORK_AVALIBLE = 0;
	public static final int NETWORK_UNAVALIBLE = 1;

	public static final int NETWORKER_INTERVAL = 3000;

	private Thread self;
	
	private int networkState;

	public Networker(String name, Kernel kernel) {
		super(name, kernel);

		this.networkState = NETWORK_UNDEFINED;
	}

	@Override
	public void run() {

		try {
			for (; self.isAlive();) {

				try {

					boolean checker = false;
					
					Enumeration<NetworkInterface> interfaces = NetworkInterface
							.getNetworkInterfaces();

					while (interfaces.hasMoreElements()) {
						NetworkInterface nic = interfaces.nextElement();
						try {
							
							checker = checker || (nic.isUp() && !nic.isLoopback());
							
						} catch (SocketException ignored) { }
					}
					
					if (networkState == NETWORK_UNDEFINED) {
						if (checker) {
							
							logger.debug("network is avalible");
							
							networkState = NETWORK_AVALIBLE;

							kernel.toogle(new Kernel.DiscoverMode(kernel));
							kernel.toogle(new Kernel.StartingState(kernel));
							kernel.reset();
							
						} else {
							
							logger.debug("network is unavalible");
							
							networkState = NETWORK_UNAVALIBLE;

							kernel.toogle(new Kernel.OfflineMode(kernel));
							kernel.toogle(new Kernel.StartingState(kernel));
							kernel.reset();
							
						}
					
					} else if (networkState == NETWORK_UNAVALIBLE) {
						
						if (checker) {
							
							logger.debug("network is avalible");
							
							networkState = NETWORK_AVALIBLE;
							
							kernel.toogle(new Kernel.DiscoverMode(kernel));
							kernel.toogle(new Kernel.StartingState(kernel));
							kernel.reset();
						
						} else {
						
							logger.debug("network still unavalible");
							
						}
						
					} else if (networkState == NETWORK_AVALIBLE) {
						
						if (!checker) {
							
							logger.debug("network is unavalible");
							
							networkState = NETWORK_UNAVALIBLE;

							kernel.toogle(new Kernel.OfflineMode(kernel));
							kernel.toogle(new Kernel.StartingState(kernel));
							kernel.reset();
							
						} else {
							
							logger.debug("network still avalible");
						}
					}
					
				} catch (SocketException ex) {

					if (networkState == NETWORK_UNDEFINED || networkState == NETWORK_AVALIBLE) {
						
						logger.debug("network is unavalible");
						
						networkState = NETWORK_UNAVALIBLE;
						
						kernel.toogle(new Kernel.OfflineMode(kernel));
						kernel.toogle(new Kernel.StartingState(kernel));
						kernel.reset();
						
					}
				}

				Thread.sleep(NETWORKER_INTERVAL);
			}

		} catch (InterruptedException ex) {
			logger.info(ex.getMessage());
		}
	}

	@Override
	public void activate() {
		self = new Thread(this);
		self.start();
	}

	@Override
	public void deactivate() {
		self.interrupt();
	}

	@Override
	public void restart() {
		networkState = NETWORK_UNDEFINED;
		deactivate();
		activate();
	}
}
