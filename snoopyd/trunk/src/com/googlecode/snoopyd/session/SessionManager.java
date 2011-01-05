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

package com.googlecode.snoopyd.session;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import Ice.Identity;

import com.googlecode.snoopyd.core.Kernel;

public class SessionManager {
	
	private Kernel kernel;

	private ISessionPrx parent;
	private Map<Ice.Identity, ISessionPrx> childs;

	public SessionManager(Kernel kernel) {
		this.kernel = kernel;
		this.childs = new HashMap<Identity, ISessionPrx>();
	}
	
	public IKernelSessionPrx createKernelSession(Ice.Identity identity, IKernelSessionPrx selfSession) {
		
		childs.put(identity, selfSession);
		
		IKernelSessionPrx prx = IKernelSessionPrxHelper.uncheckedCast(kernel.primary().addWithUUID(new KernelSessionAdapter(new KernelSession(kernel))));
		
		kernel.toogle(new Kernel.ActiveMode(kernel));
		kernel.toogle(new Kernel.SeveringState(kernel));
		
		kernel.reset();
		
		return prx;
	}
	
	public IUserSessionPrx createUserSession(Ice.Identity identity, IUserSessionPrx swap) {
		
		// childs <- swap
		
		//Identity sessionIdentity = Identities.or(kernel.identity(), swap);
		
		
		//IUserSessionPrx prx = IUserSessionPrxHelper.uncheckedCast(kernel.primary().add(new UserSession(), sessionIdentity));
		//childs.add(prx);
		
		//return prx;
		
		return null;
	}
	
	public Map<Ice.Identity, ISessionPrx> childs() {
		return Collections.unmodifiableMap(childs);
	}
	
	public ISessionPrx child(Ice.Identity identity) {
		return childs.get(identity);
	}
	
	public ISessionPrx parent() {
		return parent;
	}
	
}
