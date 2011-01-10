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
import com.googlecode.snoopyd.core.Kernel.KernelInfo;
import com.googlecode.snoopyd.core.event.NetworkDisabledEvent;
import com.googlecode.snoopyd.core.event.NetworkEnabledEvent;
import com.googlecode.snoopyd.driver.Discoverer;
import com.googlecode.snoopyd.session.IKernelSessionPrx;
import com.googlecode.snoopyd.session.IKernelSessionPrxHelper;
import com.googlecode.snoopyd.session.ISessionManagerPrx;
import com.googlecode.snoopyd.session.ISessionManagerPrxHelper;
import com.googlecode.snoopyd.session.KernelSession;
import com.googlecode.snoopyd.session.KernelSessionAdapter;
import com.googlecode.snoopyd.session.SessionManager;
import com.googlecode.snoopyd.util.Identities;

public class SuspenseHandler extends AbstractHandler implements KernelHandler {

	private Kernel kernel;

	public SuspenseHandler(Kernel kernel) {
		this.kernel = kernel;
	}

	@Override
	public void handle(NetworkEnabledEvent event) {
		boolean stateChanged = false;

		while (!stateChanged) {

			int targetRate = 0;
			Ice.Identity targetId = null;

			Discoverer discoverer = (Discoverer) kernel
					.driver(Discoverer.class);
			Map<Ice.Identity, KernelInfo> cache = discoverer.cache();

			if (cache.size() == 0) {

				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {

				}

				continue;
			}

			for (Ice.Identity identity : cache.keySet()) {
				KernelInfo info = cache.get(identity);

				if (info.rate > targetRate) {
					targetRate = info.rate;
					targetId = info.identity;
				}
			}

			KernelInfo targetInfo = cache.get(targetId);

			String proxy = Identities.toString(targetInfo.identity) + ": "
					+ targetInfo.primary;

			ISessionManagerPrx prx = ISessionManagerPrxHelper
					.checkedCast(kernel.communicator().stringToProxy(proxy));

			IKernelSessionPrx selfSession = IKernelSessionPrxHelper
					.uncheckedCast(kernel.primary()
							.addWithUUID(
									new KernelSessionAdapter(new KernelSession(
											kernel))));

			IKernelSessionPrx remoteSession = prx.createKernelSession(
					kernel.identity(), selfSession);

			((SessionManager) kernel.manager(SessionManager.class)).add(
					targetId, remoteSession);

			stateChanged = true;

			// kernel.toogle(new PassiveState(kernel));

			// kernel.toogle(new PassiveMode(kernel));
			// kernel.toogle(new SeveringState(kernel));
		}
	}

	@Override
	public void handle(NetworkDisabledEvent event) {

		String proxy = Identities.toString(kernel.identity()) + ": "
				+ kernel.primaryEndpoints();

		ISessionManagerPrx prx = ISessionManagerPrxHelper.checkedCast(kernel
				.communicator().stringToProxy(proxy));

		IKernelSessionPrx selfSession = IKernelSessionPrxHelper
				.uncheckedCast(kernel.primary().addWithUUID(
						new KernelSessionAdapter(new KernelSession(kernel))));

		IKernelSessionPrx remoteSession = prx.createKernelSession(
				kernel.identity(), selfSession);

		((SessionManager) kernel.manager(SessionManager.class)).add(
				kernel.identity(), remoteSession);

		// kernel.toogle(new OfflineHandler());
		// kernel.handle(new Kernel.StateChangedEvent());

	}

	// @Override
	// public void handle(KernelEvent event) {
	// logger.debug("not handled " + event.getClass().getSimpleName());
	// }
}