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

import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.event.ChildSessionRecivedEvent;
import com.googlecode.snoopyd.core.event.ChildSessionSendedEvent;
import com.googlecode.snoopyd.core.state.KernelListener;
import com.googlecode.snoopyd.core.state.KernelState;
import com.googlecode.snoopyd.session.IKernelSessionPrx;
import com.googlecode.snoopyd.session.IKernelSessionPrxHelper;
import com.googlecode.snoopyd.session.IUserSessionPrx;
import com.googlecode.snoopyd.session.KernelSession;
import com.googlecode.snoopyd.session.KernelSessionAdapter;

public class Sessionier extends AbstractDriver implements Driver, Activable, Restartable, KernelListener {
	
	public static final String NAME = "sessionier";
	
	public Sessionier(String name, Kernel kernel) {
		super(name, kernel);
	}
	
	public IKernelSessionPrx createKernelSession(Ice.Identity identity,
			IKernelSessionPrx selfSession) {

		kernel.handle(new ChildSessionRecivedEvent(selfSession));
		
		IKernelSessionPrx prx = IKernelSessionPrxHelper.uncheckedCast(kernel
				.primary().addWithUUID(
						new KernelSessionAdapter(new KernelSession(kernel))));

		
		kernel.handle(new ChildSessionSendedEvent(prx));

		return prx;
	}

	public IUserSessionPrx createUserSession(Ice.Identity identity,
			IUserSessionPrx selfSession) {

		return null;
	}
	
	@Override
	public void restart() {

	}

	@Override
	public void activate() {

	}

	@Override
	public void deactivate() {

	}

	@Override
	public void stateChanged(KernelState currentState) {
		// TODO Auto-generated method stub
		
	}
}
