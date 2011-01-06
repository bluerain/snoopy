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

import org.apache.log4j.Logger;

import Ice.Identity;

import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.driver.Restartable;
import com.googlecode.snoopyd.manager.AbstractManager;
import com.googlecode.snoopyd.manager.Manager;

public class SessionManager extends AbstractManager implements Manager,
		Restartable {

	private static Logger logger = Logger.getLogger(SessionManager.class);

	public static final String NAME = "sessionmanager";

	private Map<Ice.Identity, ISessionPrx> parents;

	private Map<Ice.Identity, ISessionPrx> childs;

	public SessionManager(String name, Kernel kernel) {
		super(name, kernel);

		this.childs = new HashMap<Identity, ISessionPrx>();
		this.parents = new HashMap<Identity, ISessionPrx>();
	}

	public IKernelSessionPrx createKernelSession(Ice.Identity identity,
			IKernelSessionPrx selfSession) {

		childs.put(identity, selfSession);

		IKernelSessionPrx prx = IKernelSessionPrxHelper.uncheckedCast(kernel
				.primary().addWithUUID(
						new KernelSessionAdapter(new KernelSession(kernel))));

		kernel.toogle(new Kernel.ActiveMode(kernel));
		kernel.toogle(new Kernel.SeveringState(kernel));

		kernel.reset();

		return prx;
	}

	public IUserSessionPrx createUserSession(Ice.Identity identity,
			IUserSessionPrx selfSession) {

		// childs <- swap

		// Identity sessionIdentity = Identities.or(kernel.identity(), swap);

		// IUserSessionPrx prx =
		// IUserSessionPrxHelper.uncheckedCast(kernel.primary().add(new
		// UserSession(), sessionIdentity));
		// childs.add(prx);

		// return prx;

		return null;
	}
	
	@Override
	public void restart() {
		parents.clear();
		childs.clear();
	}

	public Map<Ice.Identity, ISessionPrx> childs() {
		return Collections.unmodifiableMap(childs);
	}

	public ISessionPrx child(Ice.Identity identity) {
		return childs.get(identity);
	}

	public Map<Ice.Identity, ISessionPrx> parents() {
		return Collections.unmodifiableMap(parents);
	}

	public ISessionPrx parent(Ice.Identity identity) {
		return parents.get(identity);
	}

	public void add(Ice.Identity identity, ISessionPrx parent) {
		parents.put(identity, parent);
	}

	public void removeChild(Ice.Identity identity) {
		childs.remove(identity);
	}

	public void removeParent(Ice.Identity identity) {
		parents.remove(identity);
	}

}
