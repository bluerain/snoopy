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

import com.googlecode.snoopyd.adapter.ConfigurerAdapter;
import com.googlecode.snoopyd.adapter.ControllerAdapter;
import com.googlecode.snoopyd.adapter.HosterAdapter;
import com.googlecode.snoopyd.adapter.ModulerAdapter;
import com.googlecode.snoopyd.adapter.SchedulerAdapter;
import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.driver.Configurer;
import com.googlecode.snoopyd.driver.Controller;
import com.googlecode.snoopyd.driver.Hoster;
import com.googlecode.snoopyd.driver.IConfigurerPrx;
import com.googlecode.snoopyd.driver.IConfigurerPrxHelper;
import com.googlecode.snoopyd.driver.IControllerPrx;
import com.googlecode.snoopyd.driver.IControllerPrxHelper;
import com.googlecode.snoopyd.driver.IHosterPrx;
import com.googlecode.snoopyd.driver.IHosterPrxHelper;
import com.googlecode.snoopyd.driver.IModulerPrx;
import com.googlecode.snoopyd.driver.IModulerPrxHelper;
import com.googlecode.snoopyd.driver.ISchedulerPrx;
import com.googlecode.snoopyd.driver.ISchedulerPrxHelper;
import com.googlecode.snoopyd.driver.Moduler;
import com.googlecode.snoopyd.driver.Scheduler;

public class UserSession {

	private Kernel kernel;

	public UserSession(Kernel kernel) {
		this.kernel = kernel;
	}

	public IHosterPrx hoster() {

		IHosterPrx remoteHoster = IHosterPrxHelper
				.uncheckedCast(kernel.primary()
						.addWithUUID(
								new HosterAdapter((Hoster) kernel
										.driver(Hoster.class))));

		return remoteHoster;

	}

	public IControllerPrx controller() {

		IControllerPrx remoteController = IControllerPrxHelper
				.uncheckedCast(kernel.primary().addWithUUID(
						new ControllerAdapter((Controller) kernel
								.driver(Controller.class))));

		return remoteController;
	}

	public IModulerPrx moduler() {

		IModulerPrx remoteModuler = IModulerPrxHelper.uncheckedCast(kernel
				.primary().addWithUUID(
						new ModulerAdapter((Moduler) kernel
								.driver(Moduler.class))));

		return remoteModuler;
	}

	public IConfigurerPrx configurer() {

		IConfigurerPrx remoteConfigurer = IConfigurerPrxHelper
				.uncheckedCast(kernel.primary().addWithUUID(
						new ConfigurerAdapter((Configurer) kernel
								.driver(Configurer.class))));

		return remoteConfigurer;
	}

	public ISchedulerPrx scheduler() {

		ISchedulerPrx remoteScheduler = ISchedulerPrxHelper
				.uncheckedCast(kernel.primary().addWithUUID(
						new SchedulerAdapter((Scheduler) kernel
								.driver(Scheduler.class))));

		return remoteScheduler;

	}
}
