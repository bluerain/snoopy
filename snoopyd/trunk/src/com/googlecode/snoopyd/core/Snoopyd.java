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
import com.googlecode.snoopyd.driver.Driver;
import com.googlecode.snoopyd.util.Identities;

public class Snoopyd extends Ice.Application {
	
	private static Logger logger = Logger.getLogger(Snoopyd.class);

	public static final int EXIT_SUCCESS = 0;
	public static final int EXIT_FAILURE = 999;

	public void terminate() {
		logger.info("terminating " + Defaults.APP_NAME + " " + Defaults.APP_VER);
	}
	
	@Override
	public int run(String[] args) {
		logger.info("running " + Defaults.APP_NAME + " " + Defaults.APP_VER);
		
		Kernel kernel = new Kernel(communicator());
		logger.info("loading kernel (identity = " + Identities.toString(kernel.identity()) + ")"); 
		
		kernel.load();
		
		logger.info("loading kernel drivers:");
		for (Driver drv: kernel.drivers()) {
				logger.info("... " + drv.name());
		}


		kernel.activate();

		logger.info("activating kernel drivers: ");
		
		logger.info("statring kernel");
		
		kernel.start();
		
		return EXIT_SUCCESS;
	}
}
