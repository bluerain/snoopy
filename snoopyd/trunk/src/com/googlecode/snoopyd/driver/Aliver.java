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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.session.IKernelSessionPrx;
import com.googlecode.snoopyd.session.ISessionPrx;
import com.googlecode.snoopyd.util.Identities;

public class Aliver extends AbstractDriver implements Driver, Activable,
		Runnable, Restartable {

	private static Logger logger = Logger.getLogger(Aliver.class);

	public static final int ALIVE_INTERVAL = 15000;

	private Thread self;

	public Aliver(Kernel kernel) {
		super(Aliver.class.getSimpleName(), kernel);
	}

	@Override
	public void run() {

		for (;self.isAlive();) {
			
			List<Ice.Identity> tobeRemoved = new ArrayList<Ice.Identity>();
			
			Map<Ice.Identity, ISessionPrx> parents = kernel.parents();
			for (Ice.Identity identity : parents.keySet()) {
				IKernelSessionPrx parent = (IKernelSessionPrx) parents
						.get(identity);

				try {
					parent.ice_ping();
					logger.debug("parent node is alive: "
							+ Identities.toString(identity));
				} catch (Exception ex) {
					logger.debug("parent node is dead: " + Identities.toString(identity));
					
					tobeRemoved.add(identity);
					
					if (kernel.parents().size() - tobeRemoved.size() == 0) {

						//kernel.toogle(new Kernel.PassiveMode(kernel));
						//kernel.toogle(new Kernel.WaitingState(kernel));
						
						//kernel.reset();
					}
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
					logger.debug("child node is dead: " + Identities.toString(identity));
					
					tobeRemoved.add(identity);
				}
			}
			
			// TODO: think, maybe it is not needed
			
//			for (Ice.Identity identity: tobeRemoved) {
//				//manager.removeChild(identity);
//				//manager.removeParent(identity);
//			}

			try {
				Thread.sleep(Aliver.ALIVE_INTERVAL);
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
			}
		}
	}

	@Override
	public void activate() {
		self = new Thread(this);
		self.start();
	}

	@Override
	public void deactivate() {
		self.interrupt();
	}

	@Override
	public void restart() {
		deactivate();
		activate();
	}
}
