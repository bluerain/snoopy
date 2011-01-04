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

import Ice.Current;
import Ice.Identity;

public class SessionManagerAdapter extends _ISessionManagerDisp {
	
	private SessionManager sessionManager;
	
	public SessionManagerAdapter(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	@Override
	public IKernelSessionPrx createKernelSession(Identity identity,
			IKernelSessionPrx selfSession, Current __current) {

		return sessionManager.createKernelSession(identity, selfSession);
	}

	@Override
	public IUserSessionPrx createUserSession(Identity identity,
			IUserSessionPrx selfSession, Current __current) {
	
		return null;
	}
	
	
}
