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

import com.googlecode.snoopyd.Defaults;
import com.googlecode.snoopyd.adapter.Adapter;
import com.googlecode.snoopyd.core.event.KernelEvent;
import com.googlecode.snoopyd.core.event.SnoopydStartedEvent;
import com.googlecode.snoopyd.core.event.SnoopydTerminatedEvent;
import com.googlecode.snoopyd.driver.Driver;
import com.googlecode.snoopyd.util.Identities;

public class Snoopyd extends Ice.Application {

	private static Logger logger = Logger.getLogger(Snoopyd.class);

	public static final int EXIT_SUCCESS = 0;
	public static final int EXIT_FAILURE = 999;

	public static class ShutdownHook extends Thread {

		private Kernel kernel;

		public ShutdownHook(Kernel kernel) {
			this.kernel = kernel;
		}

		@Override
		public void run() {

			KernelEvent event = new SnoopydTerminatedEvent();

			kernel.handle(event);

			synchronized (event) {
				try {
					event.wait();
				} catch (InterruptedException e) {
					logger.debug(e.getMessage());
				}
			}
		}
	}

	@Override
	public int run(String[] args) {
		logger.info("running " + Defaults.APP_NAME + " " + Defaults.APP_VER);

		logger.debug("creating kernel: ");
		Kernel kernel = new Kernel(communicator());
		
		logger.debug("... identity: " + Identities.toString(kernel.identity()));
		logger.debug("... hostname: " + kernel.hostname());
		logger.debug("... rate: " + kernel.rate());

		logger.debug("setting shutdown hook for snoopyd");
		setInterruptHook(new ShutdownHook(kernel));

		logger.info("loading kernel drivers:");
		kernel.load();
		for (Driver drv : kernel.drivers()) {
			logger.debug("... " + drv.name());
		}

		logger.info("activating drivers adapters: ");
		kernel.activate();
		for (Adapter adp : kernel.adapters()) {
			logger.debug("... " + adp.name());
		}

		logger.info("statring kernel ("
				+ Identities.toString(kernel.identity()) + ")");

		kernel.handle(new SnoopydStartedEvent());

		kernel.startAndWait();
		
		return EXIT_SUCCESS;
	}
}
