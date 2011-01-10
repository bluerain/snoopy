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
import com.googlecode.snoopyd.core.event.NetworkDisabledEvent;
import com.googlecode.snoopyd.core.event.NetworkEnabledEvent;
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

public class SuspenseHandler extends AbstractHandler implements KernelHandler {

	public static final int TRYS = 3;
	public static final int SLEEP = 10000;

	private Kernel kernel;

	public SuspenseHandler(Kernel kernel) {
		this.kernel = kernel;
	}

	@Override
	public void handle(NetworkEnabledEvent event) {

		boolean connected = false;

		for (int i = 0; i < TRYS; i++) {

			int targetRate = 0;
			Ice.Identity targetId = null;

			Map<Ice.Identity, Map<String, String>> cache = kernel.cache();

			if (cache.size() == 0) {
				try {
					Thread.sleep(SLEEP);
				} catch (InterruptedException e) {

				}

				continue;
			}

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

			ISessionierPrx prx = ISessionierPrxHelper.checkedCast(kernel.communicator().stringToProxy(proxy));

			IKernelSessionPrx selfSession = IKernelSessionPrxHelper
					.uncheckedCast(kernel.primary()
							.addWithUUID(
									new KernelSessionAdapter(new KernelSession(
											kernel))));

			IKernelSessionPrx remoteSession = prx.createKernelSession(
					kernel.identity(), selfSession);

			
			kernel.parents().put(targetId, remoteSession);

			connected = true;
			kernel.toogle(new PassiveState(kernel));
			break;
		}

		if (!connected) {

			String proxy = Identities.toString(kernel.identity()) + ": "
					+ kernel.primaryEndpoints();

			ISessionierPrx prx = ISessionierPrxHelper.checkedCast(kernel.communicator().stringToProxy(proxy));

			IKernelSessionPrx selfSession = IKernelSessionPrxHelper
					.uncheckedCast(kernel.primary()
							.addWithUUID(
									new KernelSessionAdapter(new KernelSession(
											kernel))));

			IKernelSessionPrx remoteSession = prx.createKernelSession(
					kernel.identity(), selfSession);

			kernel.parents().put(kernel.identity(), remoteSession);
			
			kernel.toogle(new ActiveState(kernel));
		}
	}

	@Override
	public void handle(NetworkDisabledEvent event) {

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

		kernel.toogle(new OfflineState(kernel));
	}

	@Override
	public void handle(ChildSessionSendedEvent event) {
		
	}
	
}