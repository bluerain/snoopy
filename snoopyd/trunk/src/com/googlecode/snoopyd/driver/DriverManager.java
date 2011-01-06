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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.manager.AbstractManager;
import com.googlecode.snoopyd.manager.Manager;


public class DriverManager extends AbstractManager implements Manager, Restartable {
	
	public static final String NAME = "drivermanager";
	
	private static Logger logger = Logger.getLogger(DriverManager.class);

	private Map<Class<?>, Driver> drivers;

	public DriverManager(String name, Kernel kernel) {
		super(name, kernel);
		
		this.drivers = new HashMap<Class<?>, Driver>();
	}

	public void add(Class<?> clazz, Driver driver) {
		drivers.put(clazz, driver);
	}

	public void remove(Class<?> clazz) {
		drivers.remove(clazz);
	}

	public Driver get(Class<?> clazz) {
		return drivers.get(clazz);
	}
	
	public Collection<Driver> getAll() {
		return Collections.unmodifiableCollection(drivers.values());
	}
	
	public void load(Class<?> clazz) {
		
	}

	public void loadAll() {
		for (Driver drv: drivers.values()) {
			if (drv instanceof Loadable) {
				((Loadable) drv).load();
			}
		}
	}
	
	public void unload(Class<?> clazz) {
		
	}
	
	public void unloadAll() {
		for (Driver drv: drivers.values()) {
			if (drv instanceof Loadable) {
				((Loadable) drv).unload();
			}
		}
	}
	
	public void activate(Class<?> clazz) {
		
	}
	
	public void activateAll() {
		for (Driver drv: drivers.values()) {
			if (drv instanceof Activable) {
				((Activable) drv).activate();
			}
		}
	}
	
	public void deactivate(Class<?> clazz) {
		
	}
	
	public void deactivateAll() {
		for (Driver drv: drivers.values()) {
			if (drv instanceof Activable) {
				((Activable) drv).deactivate();
			}
		}
	}

	@Override
	public void restart() {
		for (Driver drv: drivers.values()) {
			if (drv instanceof Restartable) {
				((Restartable) drv).restart();
			}
		}
	}
	
}
