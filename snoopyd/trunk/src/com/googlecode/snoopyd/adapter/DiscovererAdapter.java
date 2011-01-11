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

package com.googlecode.snoopyd.adapter;

import org.apache.log4j.Logger;

import Ice.Current;
import Ice.Identity;

import com.googlecode.snoopyd.driver.Discoverer;
import com.googlecode.snoopyd.driver.Driver;
import com.googlecode.snoopyd.driver._IDiscovererDisp;

public class DiscovererAdapter extends _IDiscovererDisp implements Adapter {

	private static Logger logger = Logger.getLogger(DiscovererAdapter.class);

	private String name;
	private Ice.Identity identity;

	private Discoverer discoverer;

	public DiscovererAdapter(Ice.Identity identity, Discoverer discoverer) {
		this.discoverer = discoverer;
		this.name = DiscovererAdapter.class.getSimpleName();
		this.identity = identity;
	}

	@Override
	public void discover(Ice.Identity identity, Current __current) {

		discoverer.discover(identity, __current.ctx);
	}

	@Override
	public Driver driver() {
		return discoverer;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Identity identity() {
		return identity;
	}
}
