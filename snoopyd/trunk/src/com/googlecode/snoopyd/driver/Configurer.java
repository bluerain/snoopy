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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.event.KernelReconfiguredEvent;

public class Configurer extends AbstractDriver implements Driver {
	
	private static Logger logger = Logger.getLogger(Configurer.class);
	
	public Configurer(Kernel kernel) {
		super(Configurer.class.getSimpleName(), kernel);
	}
	
	public void reconfigure(Map<String, String> configuration) {
		kernel.handle(new KernelReconfiguredEvent(configuration));
	}
	
	public Map<String, String> configuration() {
		Properties prop =  kernel.configuration();

		Map<String, String> result = new HashMap<String, String>();
		for (Object key: prop.keySet()) {
			result.put(key.toString(), prop.getProperty(key.toString()).toString());
		}
		
		return result;
	}
	
}
