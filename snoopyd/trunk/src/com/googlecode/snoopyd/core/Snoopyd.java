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

public class Snoopyd extends Ice.Application {
	
	private static Logger logger = Logger.getLogger(Snoopyd.class);

	public static final int EXIT_SUCCESS = 0;
	public static final int EXIT_FAILURE = 999;
	
    public static class ShutdownHook extends Thread  {
    	
    	private Kernel kernel;
    	
        public ShutdownHook(Kernel kernel) {
        	this.kernel = kernel;
        }

		public void run() {
            try {
            	
            	kernel.unload();
            	kernel.deactivate();
            	kernel.stop();
            	
            } catch(Ice.LocalException ex) {
                ex.printStackTrace();
            }
        }
    }

	public void terminate() {
		logger.info("terminating " + Defaults.APP_NAME + " " + Defaults.APP_VER);
	}
	
	@Override
	public int run(String[] args) {
		logger.info("running " + Defaults.APP_NAME + " " + Defaults.APP_VER);
		
		logger.info("creating kernel"); 
		Kernel kernel = new Kernel(communicator());
		
		logger.info("setting shutdown hook for snoopyd");
		setInterruptHook(new ShutdownHook(kernel));
		
		logger.info("loading kernel drivers:");
		kernel.load();
		for (Driver drv: kernel.drivers()) {
				logger.info("... " + drv.name());
		}

		logger.info("activating kernel drivers: ");
		kernel.activate();

		logger.info("statring kernel " + kernel.kernelInfo());
		kernel.start();
		
		return EXIT_SUCCESS;
	}
}
