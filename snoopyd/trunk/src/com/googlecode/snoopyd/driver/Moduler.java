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

import java.util.Map;

import org.apache.log4j.Logger;

import com.googlecode.snoopyd.Defaults;
import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.Kernel.KernelException;
import com.googlecode.snoopyd.core.event.InvokationEvent;
import com.googlecode.snoopyd.core.event.ModuleManagerConnectedEvent;
import com.googlecode.snoopyd.core.event.ModuleManagerDisconectedEvent;
import com.googlecode.snoopymm.IModuleManagerPrx;

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
	
	public Map<String, String> fetch() {
		return kernel.moduleManager().fetch();
	}

	@Override
	public synchronized void activate() {
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

	@Override
	public void run() {

		for (;started ;) {
			
			if (mmState == MODULE_MANAGER_UNDEFINED) {
				
				kernel.handle(new InvokationEvent() {
					@Override
					public void run() {
						try {
							kernel.initModuleManager();
							
							logger.debug("module manager now conected");

							mmState = MODULE_MANAGER_CONECTED;
							kernel.handle(new ModuleManagerConnectedEvent());
							
						} catch (KernelException ex) {
							mmState = MODULE_MANAGER_DISCONECTED;
							logger.debug("module manager still disconected");
						}
					}
				});			
			
			} else if (mmState == MODULE_MANAGER_DISCONECTED) {
				
				kernel.handle(new InvokationEvent() {
					@Override
					public void run() {
						try {
							kernel.initModuleManager();
							logger.debug("module manager now conected");

							mmState = MODULE_MANAGER_CONECTED;
							kernel.handle(new ModuleManagerConnectedEvent());
						} catch (KernelException ex) {
							mmState = MODULE_MANAGER_DISCONECTED;
							logger.debug("module manager stil disconected");
						}
					}
				});			
				
			} else if (mmState == MODULE_MANAGER_CONECTED) {

				IModuleManagerPrx moduleManager = kernel.moduleManager();
			
				try {
					moduleManager.ice_ping();
				
					if (mmState == MODULE_MANAGER_CONECTED) {
					
						logger.debug("module manager still conected");

					} else if (mmState == MODULE_MANAGER_DISCONECTED) {
					
						logger.debug("module manager now conected");
					
						mmState = MODULE_MANAGER_CONECTED;
					}
				
				} catch (Exception ex) {
			
					logger.debug("module manager now disconected");
					
					mmState = MODULE_MANAGER_DISCONECTED;
					kernel.handle(new ModuleManagerDisconectedEvent());
				}
			}
			
			try {
				Thread.sleep(Defaults.MODULER_INTERVAL);
			} catch (InterruptedException ignored) {
			}
		}
		
		synchronized (this) {
			notify();
		}
	}
	
	private void disposeModuleManager() {
		
		kernel.handle(new InvokationEvent() {
			@Override
			public void run() {
				kernel.disposeModuleManager();
			}
		});
		
		mmState = MODULE_MANAGER_DISCONECTED;
		
	}
}
