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
import java.util.Queue;

import org.apache.log4j.Logger;

import Ice.Identity;

import com.googlecode.snoopyd.Defaults;
import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.state.ActiveState;
import com.googlecode.snoopyd.core.state.KernelListener;
import com.googlecode.snoopyd.core.state.KernelState;
import com.googlecode.snoopyd.core.state.PassiveState;

public class Resulter extends AbstractDriver implements Driver, Startable,
		Runnable, KernelListener {

	private static Logger logger = Logger.getLogger(Resulter.class);

	public static class Result {

		private Ice.Identity identity;
		private String muid;
		private String[] result;

		public Result(Identity identity, String muid, String[] result) {
			this.identity = identity;
			this.muid = muid;
			this.result = result;
		}

		public Ice.Identity identity() {
			return identity;
		}

		public String muid() {
			return muid;
		}

		public String[] result() {
			return result;
		}
	}

	private Queue<Result> pool;

	private boolean started;

	public Resulter(Kernel kernel) {
		super(Resulter.class.getSimpleName(), kernel);

		this.pool = new LinkedList<Result>();
		this.started = false;
	}

	public synchronized void store(Ice.Identity identity, String muid,
			String[] result) {

		pool.offer(new Result(identity, muid, result));

		if (pool.size() > Defaults.RESULTER_THRESHHOLD) {
			notify();
		}
	}

	@Override
	public synchronized void start() {
		logger.debug("starting " + name);

		Thread self = new Thread(this, Defaults.RESULTER_THREAD_NAME);
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
	public void run() {

		for (; started;) {
			
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException ignored) {
				}
			}

			for (; !pool.isEmpty() && started;) {

				Result result = pool.poll();
				
				// TODO: store in DB
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
