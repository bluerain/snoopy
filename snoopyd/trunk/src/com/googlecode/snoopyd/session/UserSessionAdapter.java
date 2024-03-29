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

import com.googlecode.snoopyd.driver.IConfigurerPrx;
import com.googlecode.snoopyd.driver.IControllerPrx;
import com.googlecode.snoopyd.driver.IHosterPrx;
import com.googlecode.snoopyd.driver.IModulerPrx;
import com.googlecode.snoopyd.driver.ISchedulerPrx;

import Ice.Current;

public class UserSessionAdapter extends _IUserSessionDisp {
	
	private UserSession userSession;

	public UserSessionAdapter(UserSession userSession) {
		this.userSession = userSession;
	}

	@Override
	public void destroy(Current __current) {
		
	}

	@Override
	public void refresh(Current __current) {
		
	}

	@Override
	public IHosterPrx hoster(Current __current) {
		return userSession.hoster();
	}

	@Override
	public IControllerPrx controller(Current __current) {
		return userSession.controller();
	}

	@Override
	public IModulerPrx moduler(Current __current) {
		return userSession.moduler();
	}

	@Override
	public IConfigurerPrx configurer(Current __current) {
		return userSession.configurer();
	}

	@Override
	public ISchedulerPrx scheduler(Current __current) {
		return userSession.scheduler();
	}
}
