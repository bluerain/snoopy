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

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;

import com.googlecode.snoopyd.Defaults;
import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.state.ActiveState;
import com.googlecode.snoopyd.core.state.KernelListener;
import com.googlecode.snoopyd.core.state.KernelState;
import com.googlecode.snoopyd.core.state.PassiveState;

public class Invoker extends AbstractDriver implements Driver, Runnable,
		Startable, KernelListener {

	private static Logger logger = Logger.getLogger(Invoker.class);
	
	private boolean started;
	
	private Queue<Map<String, String>> pool;
	
	public Invoker(Kernel kernel) {
		super(Invoker.class.getSimpleName(), kernel);
		this.started = false;
		this.pool = new LinkedList<Map<String,String>>();
	}
	
	public synchronized void invoke(Map<String, String> invokation) {
		
		pool.offer(invokation);
		notify();
	}


	@Override
	public synchronized void start() {
		logger.debug("starting " + name);

		Thread self = new Thread(this, Defaults.INVOKER_THREAD_NAME);
		self.start();

		started = true;
	}

	@Override
	public synchronized void stop() {
		logger.debug("stoping " + name);

		started = false;
		notify();

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
		
		for (;started ;) {

			for (; !pool.isEmpty() && started;) {
				
				/*
				 * module = <MODULE_UUID>
				 * params = <PARAM1>, <PARAM2> 
				 */
				Map<String, String> invokation = pool.poll();
				
				// TODO: Hard-Code HERE! Bitch!
			}

			if (started) {
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException ignored) {
					}
				}
			}
			
		}
		
		synchronized (this) {
			notify();
		}
	}
	
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
