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

import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.event.ChildSessionSendedEvent;
import com.googlecode.snoopyd.core.event.DiscoverRecivedEvent;
import com.googlecode.snoopyd.core.event.NetworkDisabledEvent;
import com.googlecode.snoopyd.core.event.NetworkEnabledEvent;
import com.googlecode.snoopyd.core.state.ActiveState;
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

	public OnlineHandler(Kernel kernel) {
		this.kernel = kernel;
	}

	@Override
	public void handle(NetworkEnabledEvent event) {

	}

	@Override
	public void handle(NetworkDisabledEvent event) {

	}

	@Override
	public void handle(ChildSessionSendedEvent event) {

	}

	@Override
	public void handle(DiscoverRecivedEvent event) {

		int oldSize = kernel.cache().size();
		kernel.cache().put(event.identity(), event.context());
		int newSize = kernel.cache().size();

		if (oldSize == newSize) {

			int targetRate = 0;
			Ice.Identity targetId = null;

			Map<Ice.Identity, Map<String, String>> cache = kernel.cache();

			for (Ice.Identity identity : cache.keySet()) {
				Map<String, String> context = cache.get(identity);

				if (Integer.valueOf(context.get("rate")) > targetRate) {
					targetRate = Integer.valueOf(context.get("rate"));
					targetId = identity;
				}
			}

			Map<String, String> targetInfo = cache.get(targetId);

			String proxy = Identities.toString(targetId) + ": "
					+ targetInfo.get("primary");

			ISessionierPrx prx = ISessionierPrxHelper.checkedCast(kernel
					.communicator().stringToProxy(proxy));

			IKernelSessionPrx selfSession = IKernelSessionPrxHelper
					.uncheckedCast(kernel.primary()
							.addWithUUID(
									new KernelSessionAdapter(new KernelSession(
											kernel))));

			IKernelSessionPrx remoteSession = prx.createKernelSession(
					kernel.identity(), selfSession);

			kernel.parents().put(targetId, remoteSession);

			if (Identities.equals(kernel.identity(), targetId)) {
				kernel.toogle(new ActiveState(kernel));
			} else {
				kernel.toogle(new PassiveState(kernel));
			}
		}
	}
}
