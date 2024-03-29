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

import Ice.ConnectionRefusedException;

import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.event.ChildNodeDeadedEvent;
import com.googlecode.snoopyd.core.event.DiscoverRecivedEvent;
import com.googlecode.snoopyd.core.event.InvokationEvent;
import com.googlecode.snoopyd.core.event.KernelStateChangedEvent;
import com.googlecode.snoopyd.core.event.NetworkDisabledEvent;
import com.googlecode.snoopyd.core.event.NetworkEnabledEvent;
import com.googlecode.snoopyd.core.event.ParentNodeDeadedEvent;
import com.googlecode.snoopyd.core.event.ResultRecievedEvent;
import com.googlecode.snoopyd.core.event.ScheduleTimeComeEvent;
import com.googlecode.snoopyd.core.state.OfflineState;
import com.googlecode.snoopyd.core.state.OnlineState;
import com.googlecode.snoopyd.driver.IModulerPrx;
import com.googlecode.snoopyd.driver.ISessionierPrx;
import com.googlecode.snoopyd.driver.ISessionierPrxHelper;
import com.googlecode.snoopyd.driver.Resulter;
import com.googlecode.snoopyd.driver.Scheduler;
import com.googlecode.snoopyd.session.IKernelSessionPrx;
import com.googlecode.snoopyd.session.IKernelSessionPrxHelper;
import com.googlecode.snoopyd.session.KernelSession;
import com.googlecode.snoopyd.session.KernelSessionAdapter;
import com.googlecode.snoopyd.util.Identities;

public class ActiveHandler extends AbstractHandler implements KernelHandler {

	public ActiveHandler(Kernel kernel) {
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
	public void handle(ParentNodeDeadedEvent event) {

		kernel.cache().clear();
		kernel.parents().remove(event.identity());
		kernel.handle(new KernelStateChangedEvent(new OnlineState(kernel)));
	}

	@Override
	public void handle(ChildNodeDeadedEvent event) {

		kernel.childs().remove(event.identity());
		Scheduler scheduler = (Scheduler) kernel.driver(Scheduler.class);
		scheduler.cancel(event.identity());
	}

	@Override
	public void handle(ScheduleTimeComeEvent event) {

		final ScheduleTimeComeEvent fevent = event;

		kernel.handle(new InvokationEvent() {

			@Override
			public void run() {
				try {

					IKernelSessionPrx session = (IKernelSessionPrx) kernel
							.childs().get(fevent.identity());
					IModulerPrx moduler = session.moduler();

					try {

						String result[] = moduler.launch(fevent.muid(),
								fevent.params());
						kernel.handle(new ResultRecievedEvent(
								fevent.identity(), fevent.muid(), result));

					} catch (com.googlecode.snoopyd.driver.ModuleNotFoundException ex) {
					
					}
					
				} catch (ConnectionRefusedException ex) {
					// kernel.handle(new ExceptionEvent(new
					// KernelException("could not connect to module manager")));
				} catch (Exception ex) {
					
				}
			}
		});
	}

	@Override
	public void handle(ResultRecievedEvent event) {
		super.handle(event);

		Resulter resulter = (Resulter) kernel.driver(Resulter.class);

		String hostname = kernel.cache().get(event.identity()).get("hostname");
		String osname = kernel.cache().get(event.identity()).get("os");

		IKernelSessionPrx remoteSession = (IKernelSessionPrx) kernel.childs()
				.get(event.identity());

		String module = remoteSession.moduler().fetch().get(event.muid());

		resulter.store(hostname, osname, module, event.result());
	}
}