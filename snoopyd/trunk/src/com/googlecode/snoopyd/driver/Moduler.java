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

import com.googlecode.snoopyd.Defaults;
import com.googlecode.snoopyd.core.Kernel;

public class Moduler extends AbstractDriver implements Driver, Activable, Runnable {

	private static Logger logger = Logger.getLogger(Moduler.class);

	private boolean started;
	
	public static final int MODULE_MANAGER_UNDEFINED = -1;
	public static final int MODULE_MANAGER_CONECTED = 0;
	public static final int MODULE_MANAGER_DISCONECTED = 1;

	private int mmState;
	
	public Moduler(Kernel kernel) {
		super(Moduler.class.getSimpleName(), kernel);
		
		this.mmState = MODULE_MANAGER_UNDEFINED;
		this.started = false;
	}
	

	@Override
	public void activate() {
		started = true;
		
		initModuleManager();

		Thread self = new Thread(this);
		self.start();
	}



	@Override
	public void deactivate() {
		started = false;
		
		disposeModuleManager();

		try {
			wait();
		} catch (InterruptedException e) {
			logger.warn(e.getMessage());
		}		
	}

	@Override
	public void run() {

		for (;started ;) {

			
			
			
			try {
				Thread.currentThread().sleep(Defaults.MODULER_INTERVAL);
			} catch (InterruptedException ignored) {
			}
		}
		
		synchronized (this) {
			notify();
		}
	}
	
	private void initModuleManager() {
		
	}
	
	private void disposeModuleManager() {
		
	}
}
