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

import java.util.HashMap;
import java.util.Map;

import com.googlecode.snoopyd.Defaults;
import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.event.ChildNodeDeadedEvent;
import com.googlecode.snoopyd.core.event.ChildSessionSendedEvent;
import com.googlecode.snoopyd.core.event.DiscoverRecivedEvent;
import com.googlecode.snoopyd.core.event.KernelStateChangedEvent;
import com.googlecode.snoopyd.core.event.NetworkDisabledEvent;
import com.googlecode.snoopyd.core.event.NetworkEnabledEvent;
import com.googlecode.snoopyd.core.event.ParentNodeDeadedEvent;
import com.googlecode.snoopyd.core.event.ParentSessionRecivedEvent;
import com.googlecode.snoopyd.core.event.ParentSessionSendedEvent;
import com.googlecode.snoopyd.core.event.ScheduleTimeComeEvent;
import com.googlecode.snoopyd.core.event.ScheduleUpdatedEvent;
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

	private Map<Ice.Identity, Integer> leaders;

	private long startTimeStamp;

	public OnlineHandler(Kernel kernel) {
		super(kernel);

		this.leaders = new HashMap<Ice.Identity, Integer>();
		this.leaders.put(kernel.identity(), kernel.rate());

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
	public void handle(DiscoverRecivedEvent event) {

		kernel.cache().put(event.identity(), event.context());

		Ice.Identity eventIdentity = event.identity();
		int eventRate = Integer.parseInt(event.context().get("rate"));

		leaders.put(eventIdentity, eventRate);

		if ((System.currentTimeMillis() - startTimeStamp) > Defaults.DISCOVER_TIMEOUT) {

			boolean connected = false;
			while (!connected) {

				Ice.Identity leaderIdentity = kernel.identity();
				int leaderRate = kernel.rate();
				for (Ice.Identity identity : leaders.keySet()) {
					if (leaders.get(identity) > leaderRate) {
						leaderIdentity = identity;
						leaderRate = leaders.get(identity);
					}
				}

				leaders.remove(leaderIdentity);

				Map<String, String> targetContext = kernel.cache().get(
						leaderIdentity);
				String proxy = Identities.toString(leaderIdentity) + ": "
						+ targetContext.get("primary");

				try {

					ISessionierPrx prx = ISessionierPrxHelper
							.checkedCast(kernel.communicator().stringToProxy(
									proxy));

					IKernelSessionPrx selfSession = IKernelSessionPrxHelper
							.uncheckedCast(kernel.primary().addWithUUID(
									new KernelSessionAdapter(new KernelSession(
											kernel))));

					IKernelSessionPrx leaderSession = prx.createKernelSession(
							kernel.identity(), selfSession);

					kernel.handle(new ChildSessionSendedEvent(
							kernel.identity(), selfSession));
					kernel.handle(new ParentSessionRecivedEvent(leaderIdentity,
							leaderSession));
					
					connected = true;

				} catch (Exception ex) {
					
					connected = false;
				
				}
			}
		}
	}

	@Override
	public void handle(ParentNodeDeadedEvent event) {

	}

	@Override
	public void handle(ChildNodeDeadedEvent event) {

	}

	@Override
	public void handle(ScheduleTimeComeEvent event) {

	}

	@Override
	public void handle(ParentSessionRecivedEvent event) {
		super.handle(event);

		if (event.identity().equals(kernel.identity())) {
			kernel.handle(new KernelStateChangedEvent(new ActiveState(kernel)));
		} else {
			kernel.handle(new KernelStateChangedEvent(new PassiveState(kernel)));
		}

		kernel.handle(new ScheduleUpdatedEvent());
	}

	@Override
	public void handle(ParentSessionSendedEvent event) {
		super.handle(event);
		kernel.handle(new KernelStateChangedEvent(new ActiveState(kernel)));
	}
}
