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

import java.util.Timer;

import org.apache.log4j.Logger;

import com.googlecode.snoopyd.Defaults;
import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.state.ActiveState;
import com.googlecode.snoopyd.core.state.KernelListener;
import com.googlecode.snoopyd.core.state.KernelState;
import com.googlecode.snoopyd.core.state.PassiveState;

public class Scheduler extends AbstractDriver implements Driver, Startable,
		KernelListener {

	private static Logger logger = Logger.getLogger(Scheduler.class);

	private boolean started;

	private Timer self;

	public Scheduler(Kernel kernel) {
		super(Scheduler.class.getSimpleName(), kernel);
	}

	@Override
	public synchronized void start() {
		logger.debug("starting " + name);
		
		started = true;
		
		self = new Timer();
		
		// TODO:
	}

	@Override
	public synchronized void stop() {
		logger.debug("stoping " + name);
		
		self.cancel();
	}

	@Override
	public synchronized boolean started() {
		return started;
	}

	@Override
	public synchronized void restart() {

		logger.debug("restarting " + name);
		
		stop();
		start();
	}

	@Override
	public void stateChanged(KernelState currentState) {
		if (currentState instanceof ActiveState) {
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
