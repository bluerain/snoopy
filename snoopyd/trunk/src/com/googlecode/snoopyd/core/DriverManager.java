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

package com.googlecode.snoopyd.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.googlecode.snoopyd.driver.Activable;
import com.googlecode.snoopyd.driver.Driver;
import com.googlecode.snoopyd.driver.Loadable;

public class DriverManager {

	private static Logger logger = Logger.getLogger(DriverManager.class);

	private Map<Class<?>, Driver> drivers;

	public DriverManager() {
		this.drivers = new HashMap<Class<?>, Driver>();
	}

	public void add(Class<?> clazz, Driver driver) {
		drivers.put(clazz, driver);
		
	}

	public void remove(Class<?> clazz) {
		drivers.remove(clazz);
	}

	public Driver fetch(Class<?> clazz) {
		return drivers.get(clazz);
	}

	public void load() {
		for (Driver drv: drivers.values()) {
			try {
				((Loadable) drv).load();
			} catch (ClassCastException ex) {}
			
		}
	}
	
	public void activate() {
		for (Driver drv: drivers.values()) {
			try {
			((Activable) drv).activate();
			} catch (ClassCastException ex) {}
		}
	}
	
	
}
