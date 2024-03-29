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

import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.event.ChildNodeDeadedEvent;
import com.googlecode.snoopyd.core.event.DiscoverRecivedEvent;
import com.googlecode.snoopyd.core.event.KernelStateChangedEvent;
import com.googlecode.snoopyd.core.event.NetworkDisabledEvent;
import com.googlecode.snoopyd.core.event.NetworkEnabledEvent;
import com.googlecode.snoopyd.core.event.ParentNodeDeadedEvent;
import com.googlecode.snoopyd.core.event.ParentSessionRecivedEvent;
import com.googlecode.snoopyd.core.event.ParentSessionSendedEvent;
import com.googlecode.snoopyd.core.event.ScheduleTimeComeEvent;
import com.googlecode.snoopyd.core.state.ActiveState;
import com.googlecode.snoopyd.core.state.OfflineState;
import com.googlecode.snoopyd.core.state.OnlineState;
import com.googlecode.snoopyd.driver.ISessionierPrx;
import com.googlecode.snoopyd.driver.ISessionierPrxHelper;
import com.googlecode.snoopyd.session.IKernelSessionPrx;
import com.googlecode.snoopyd.session.IKernelSessionPrxHelper;
import com.googlecode.snoopyd.session.KernelSession;
import com.googlecode.snoopyd.session.KernelSessionAdapter;
import com.googlecode.snoopyd.util.Identities;

public class PassiveHandler extends AbstractHandler implements KernelHandler {

	public PassiveHandler(Kernel kernel) {
		super(kernel);
	}

	@Override
	public void handle(NetworkEnabledEvent event) {

	}

	@Override
	public void handle(NetworkDisabledEvent event) {
		
		kernel.childs().clear();
		kernel.parents().clear();
	
		String proxy = Identities.toString(kernel.identity()) + ": "
				+ kernel.primaryEndpoints();

		ISessionierPrx prx = ISessionierPrxHelper.checkedCast(kernel
				.communicator().stringToProxy(proxy));

		IKernelSessionPrx selfSession = IKernelSessionPrxHelper
				.uncheckedCast(kernel.primary().addWithUUID(
						new KernelSessionAdapter(new KernelSession(kernel))));

		IKernelSessionPrx remoteSession = prx.createKernelSession(
				kernel.identity(), selfSession);

		kernel.parents().put(kernel.identity(), remoteSession);

		kernel.handle(new KernelStateChangedEvent(new OfflineState(kernel)));
	}

	@Override
	public void handle(DiscoverRecivedEvent event) {
		kernel.cache().put(event.identity(), event.context());
	}

	@Override
	public void handle(ParentSessionRecivedEvent event) {
		super.handle(event);
	}

	@Override
	public void handle(ParentSessionSendedEvent event) {
		super.handle(event);
		kernel.handle(new KernelStateChangedEvent(new ActiveState(kernel)));
	}

	@Override
	public void handle(ParentNodeDeadedEvent event) {
		kernel.cache().clear();
		kernel.parents().remove(event.identity());
		kernel.handle(new KernelStateChangedEvent(new OnlineState(kernel)));
	}

	@Override
	public void handle(ChildNodeDeadedEvent event) {
		
	}

	@Override
	public void handle(ScheduleTimeComeEvent event) {
		
	}
}