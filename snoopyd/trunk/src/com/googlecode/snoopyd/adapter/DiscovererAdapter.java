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
import Ice.ObjectAdapter;

import com.googlecode.snoopyd.driver.Discoverer;
import com.googlecode.snoopyd.driver.Driver;
import com.googlecode.snoopyd.driver._IDiscovererDisp;

public class DiscovererAdapter extends _IDiscovererDisp implements DriverAdapter {

	private static Logger logger = Logger.getLogger(DiscovererAdapter.class);
	
	public static final String NAME = "discoverer";
	
	private String name;
	private Ice.Identity identity;
	
	private Discoverer discoverer;
	
	public DiscovererAdapter(String name, Ice.Identity identity, Discoverer discoverer) {
		this.discoverer = discoverer;
		this.name = name;
		this.identity = identity;
	}

	@Override
	public void discover(Current __current) {
		
		// __current.ctx
		// identity
		// rate
		// proxy
		//
		
		//logger.info("current adapter " + __current.adapter.getName());
		logger.info("endpoints " + __current.adapter.getPublishedEndpoints()[0]);
		logger.info("..identity = " + __current.ctx.get("identity"));
		logger.info("..rate = " + __current.ctx.get("rate"));
		logger.info("..primary = " + __current.ctx.get("primary"));
		logger.info("..secaondary = " + __current.ctx.get("secondary"));
		
		//logger.info("current con " + __current.con.toString());
	}

	@Override
	public void request(Current __current) {

	}		

	@Override
	public void offer(Current __current) {

	}

	@Override
	public void pack(Current __current) {
		
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