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

public class AdapterManager {
	
	private Map<Class<?>, DriverAdapter> adapters;
	
	public AdapterManager() {
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
}
