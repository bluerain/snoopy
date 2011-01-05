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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.driver.Resetable;
import com.googlecode.snoopyd.manager.AbstractManager;
import com.googlecode.snoopyd.manager.Manager;

public class AdapterManager extends AbstractManager implements Manager, Resetable {
	
	public static final String NAME = "adaptermanager";
	
	private Map<Class<?>, DriverAdapter> adapters;
	
	public AdapterManager(String name, Kernel kernel) {
		super(name, kernel);
		this.adapters = new HashMap<Class<?>, DriverAdapter>();
	}
	
	public void add(Class<?> clazz, DriverAdapter object) {
		adapters.put(clazz, object);
	}
	
	public DriverAdapter get(Class<?> clazz) {
		return adapters.get(clazz);
	}
	
	public Collection<DriverAdapter> getAll() {
		return Collections.unmodifiableCollection(adapters.values());
	}

	public void activateAll() {
	}
	
	public void deactivateAll() {
	}

	@Override
	public void reset() {

	}
}
