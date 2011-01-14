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

package com.googlecode.snoopyd.core.handler;

import java.util.Map;

import com.googlecode.snoopyd.Defaults;
import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.event.ChildSessionRecivedEvent;
import com.googlecode.snoopyd.core.event.ChildSessionSendedEvent;
import com.googlecode.snoopyd.core.event.DiscoverRecivedEvent;
import com.googlecode.snoopyd.core.event.NetworkDisabledEvent;
import com.googlecode.snoopyd.core.event.NetworkEnabledEvent;
import com.googlecode.snoopyd.core.event.ParentNodeDeadedEvent;
import com.googlecode.snoopyd.core.event.SnoopydStartedEvent;
import com.googlecode.snoopyd.core.event.SnoopydTerminatedEvent;
import com.googlecode.snoopyd.core.state.ActiveState;
import com.googlecode.snoopyd.core.state.OfflineState;
import com.googlecode.snoopyd.core.state.PassiveState;
import com.googlecode.snoopyd.driver.ISessionierPrx;
import com.googlecode.snoopyd.driver.ISessionierPrxHelper;
import com.googlecode.snoopyd.session.IKernelSessionPrx;
import com.googlecode.snoopyd.session.IKernelSessionPrxHelper;
import com.googlecode.snoopyd.session.KernelSession;
import com.googlecode.snoopyd.session.KernelSessionAdapter;
import com.googlecode.snoopyd.util.Identities;

public class OnlineHandler extends AbstractHandler implements KernelHandler {

	private Kernel kernel;

	private int discoverRecivedCounter;

	public OnlineHandler(Kernel kernel) {
		this.kernel = kernel;
		this.discoverRecivedCounter = 0;
	}

	@Override
	public void handle(NetworkEnabledEvent event) {

	}

	@Override
	public void handle(NetworkDisabledEvent event) {
		
		kernel.childs().clear();
		kernel.parents().clear();
		
		kernel.toogle(new OfflineState(kernel));
	}

	@Override
	public void handle(ChildSessionSendedEvent event) {

	}

	@Override
	public void handle(ChildSessionRecivedEvent event) {
		
	}

	@Override
	public void handle(DiscoverRecivedEvent event) {

		discoverRecivedCounter++;

		int oldSize = kernel.cache().size();
		kernel.cache().put(event.identity(), event.context());
		int newSize = kernel.cache().size();

		if (oldSize == newSize
				&& discoverRecivedCounter > Defaults.DEFAULT_DISCOVER_RECIVED_COUTER_THRESHOLD) {

			Ice.Identity hostIdentity = kernel.identity();
			
			int targetRate = 0;
			Ice.Identity targetIdentity = null;

			Map<Ice.Identity, Map<String, String>> cache = kernel.cache();

			for (Ice.Identity identity : cache.keySet()) {
				Map<String, String> context = cache.get(identity);

				if (Integer.valueOf(context.get("rate")) > targetRate) {
					targetRate = Integer.valueOf(context.get("rate"));
					targetIdentity = identity;
				}
			}

			Map<String, String> targetContext = cache.get(targetIdentity);

			String proxy = Identities.toString(targetIdentity) + ": "
					+ targetContext.get("primary");

			ISessionierPrx prx = ISessionierPrxHelper.checkedCast(kernel
					.communicator().stringToProxy(proxy));

			IKernelSessionPrx selfSession = IKernelSessionPrxHelper
					.uncheckedCast(kernel.primary()
							.addWithUUID(
									new KernelSessionAdapter(new KernelSession(
											kernel))));

			IKernelSessionPrx remoteSession = prx.createKernelSession(
					hostIdentity, selfSession);

			kernel.parents().put(targetIdentity, remoteSession);

			if (Identities.equals(hostIdentity, targetIdentity)) {
				kernel.toogle(new ActiveState(kernel));
			} else {
				kernel.toogle(new PassiveState(kernel));
			}
		}
	}

	@Override
	public void handle(ParentNodeDeadedEvent event) {
		
	}

	@Override
	public void handle(SnoopydStartedEvent event) {
		
	}

	@Override
	public void handle(SnoopydTerminatedEvent event) {

		kernel.unload();
    	kernel.deactivate();
    	kernel.stop();
    	
    	synchronized (event) {
    		event.notify();
		}
	}
}
