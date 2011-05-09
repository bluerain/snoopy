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
import com.googlecode.snoopyd.core.state.ActiveState;
import com.googlecode.snoopyd.core.state.KernelListener;
import com.googlecode.snoopyd.core.state.KernelState;
import com.googlecode.snoopyd.core.state.PassiveState;

/**
 * 
 * TODO: This should be activable. I Need to review system disign
 * 
 * @author vkostyuk
 *
 */

public class Moduler extends AbstractDriver implements Driver, Runnable, Startable,
		KernelListener {

	private static Logger logger = Logger.getLogger(Moduler.class);

	private boolean started;
	
	public Moduler(Kernel kernel) {
		super(Moduler.class.getSimpleName(), kernel);
		this.started = false;
	}

	@Override
	public void start() {
		logger.debug("starting " + name);

		Thread self = new Thread(this, Defaults.MODULER_THREAD_NAME);
		self.start();

		started = true;
	}

	@Override
	public void stop() {
		logger.debug("stoping " + name);

		started = false;

		try {
			wait();
		} catch (InterruptedException e) {
			logger.warn(e.getMessage());
		}
	}

	@Override
	public boolean started() {
		return false;
	}

	@Override
	public void restart() {
		logger.debug("restarting " + name);

		stop();
		start();
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
	
	/**
	 * TODO: add dispose ModuleManager
	 */
	
	@Override
	public void stateChanged(KernelState currentState) {
		if (currentState instanceof ActiveState
				|| currentState instanceof PassiveState) {
			if (!started) {
				start();
			}
		} else {
			if (started) {
				stop();
			}
		}
	}
}
