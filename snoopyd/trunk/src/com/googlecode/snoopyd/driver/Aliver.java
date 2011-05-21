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
import com.googlecode.snoopyd.core.event.ChildNodeDeadedEvent;
import com.googlecode.snoopyd.core.event.ParentNodeDeadedEvent;
import com.googlecode.snoopyd.core.state.ActiveState;
import com.googlecode.snoopyd.core.state.KernelListener;
import com.googlecode.snoopyd.core.state.KernelState;
import com.googlecode.snoopyd.core.state.PassiveState;
import com.googlecode.snoopyd.session.IKernelSessionPrx;
import com.googlecode.snoopyd.session.ISessionPrx;
import com.googlecode.snoopyd.util.Identities;

public class Aliver extends AbstractDriver implements Driver, Runnable,
		Startable, KernelListener {

	private static Logger logger = Logger.getLogger(Aliver.class);

	private boolean started;

	public Aliver(Kernel kernel) {
		super(Aliver.class.getSimpleName(), kernel);
		this.started = false;
	}

	@Override
	public void run() {

		try {

			for (; started;) {

				Map<Ice.Identity, ISessionPrx> parents = kernel.parents();
				for (Ice.Identity identity : parents.keySet()) {
					IKernelSessionPrx parent = (IKernelSessionPrx) parents
							.get(identity);

					try {
						parent.ice_ping();
						logger.debug("parent node is alive: "
								+ Identities.toString(identity));
					} catch (Exception ex) {
						logger.debug("parent node is dead: "
								+ Identities.toString(identity));

						kernel.handle(new ParentNodeDeadedEvent(identity));
					}
				}

				Map<Ice.Identity, ISessionPrx> childs = kernel.childs();
				for (Ice.Identity identity : childs.keySet()) {
					IKernelSessionPrx child = (IKernelSessionPrx) childs
							.get(identity);

					try {
						child.ice_ping();
						logger.debug("child node is alive: "
								+ Identities.toString(identity));
					} catch (Exception ex) {
						logger.debug("child node is dead: "
								+ Identities.toString(identity));
						
						kernel.handle(new ChildNodeDeadedEvent(identity));
					}
				}

				Thread.sleep(Defaults.ALIVE_INTERVAL);
			}

		} catch (InterruptedException ex) {
			logger.warn(ex.getMessage());
		} 
		
		synchronized (this) {
			notify();
		}
	}
	
	@Override
	public synchronized void start() {

		logger.debug("starting " + name);

		Thread self = new Thread(this, Defaults.ALIVER_THREAD_NAME);
		self.start();

		started = true;
	}

	@Override
	public synchronized void stop() {

		logger.debug("stoping " + name);

		started = false;

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
