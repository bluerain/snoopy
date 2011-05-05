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

import Ice.Identity;

import com.googlecode.snoopyd.Defaults;
import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.event.ChildSessionRecivedEvent;
import com.googlecode.snoopyd.core.event.ChildSessionSendedEvent;
import com.googlecode.snoopyd.core.event.DiscoverRecivedEvent;
import com.googlecode.snoopyd.core.event.KernelStateChangedEvent;
import com.googlecode.snoopyd.core.event.NetworkDisabledEvent;
import com.googlecode.snoopyd.core.event.NetworkEnabledEvent;
import com.googlecode.snoopyd.core.event.ParentNodeDeadedEvent;
import com.googlecode.snoopyd.core.event.SnoopydStartedEvent;
import com.googlecode.snoopyd.core.event.SnoopydTerminatedEvent;
import com.googlecode.snoopyd.core.state.ActiveState;
import com.googlecode.snoopyd.core.state.OfflineState;
import com.googlecode.snoopyd.core.state.OnlineState;
import com.googlecode.snoopyd.core.state.PassiveState;
import com.googlecode.snoopyd.driver.ISessionierPrx;
import com.googlecode.snoopyd.driver.ISessionierPrxHelper;
import com.googlecode.snoopyd.session.IKernelSessionPrx;
import com.googlecode.snoopyd.session.IKernelSessionPrxHelper;
import com.googlecode.snoopyd.session.KernelSession;
import com.googlecode.snoopyd.session.KernelSessionAdapter;
import com.googlecode.snoopyd.util.Identities;

public class OnlineHandler extends AbstractHandler implements KernelHandler {

	private Ice.Identity leaderIdentity;
	private int leaderRate;
	
	private long startTimeStamp;

	public OnlineHandler(Kernel kernel) {
		super(kernel);
		
		this.leaderIdentity = kernel.identity();
		this.leaderRate = kernel.rate();
		
		this.startTimeStamp = System.currentTimeMillis();
	}

	@Override
	public void handle(NetworkEnabledEvent event) {

	}

	@Override
	public void handle(NetworkDisabledEvent event) {
		
		kernel.childs().clear();
		kernel.parents().clear();
		
		kernel.handle(new KernelStateChangedEvent(new OfflineState(kernel)));
	}

	@Override
	public void handle(ChildSessionSendedEvent event) {

		kernel.handle(new KernelStateChangedEvent(new ActiveState(kernel)));
		
	}

	@Override
	public void handle(ChildSessionRecivedEvent event) {
		
	}

	@Override
	public void handle(DiscoverRecivedEvent event) {

		kernel.cache().put(event.identity(), event.context());

		Ice.Identity eventIdentity = event.identity();
		int eventRate = Integer.parseInt(event.context().get("rate"));
		
		if (eventRate > leaderRate) {
			leaderIdentity = eventIdentity;
			leaderRate = eventRate;
		}
		
		if ((System.currentTimeMillis() - startTimeStamp) > Defaults.DISCOVER_TIMEOUT) {

			Map<String, String> targetContext = kernel.cache().get(leaderIdentity);

			String proxy = Identities.toString(leaderIdentity) + ": "
					+ targetContext.get("primary");

			ISessionierPrx prx = ISessionierPrxHelper.checkedCast(kernel
					.communicator().stringToProxy(proxy));

			IKernelSessionPrx selfSession = IKernelSessionPrxHelper
					.uncheckedCast(kernel.primary()
							.addWithUUID(
									new KernelSessionAdapter(new KernelSession(
											kernel))));

			IKernelSessionPrx remoteSession = prx.createKernelSession(
					kernel.identity(), selfSession);

			kernel.parents().put(leaderIdentity, remoteSession);

			kernel.handle(new KernelStateChangedEvent(new PassiveState(kernel)));
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
    	kernel.dispose();
    	
    	synchronized (event) {
    		event.notify();
		}
	}
}
