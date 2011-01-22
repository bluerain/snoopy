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

import org.apache.log4j.Logger;
import org.hyperic.sigar.NetInfo;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.googlecode.snoopyd.core.Kernel;

public class Hoster extends AbstractDriver implements Driver {

	private static Logger logger = Logger.getLogger(Hoster.class);
	
	private Sigar sigar;
	
	public Hoster(Kernel kernel) {
		super(Networker.class.getSimpleName(), kernel);
		
		this.sigar = new Sigar();
	}
	
	public Map<String, String> context() {
		
		Map<String, String> result = new HashMap<String, String>();
		
		try {
			
			NetInfo netInfo = sigar.getNetInfo();

			result = netInfo.toMap();
			
		} catch (SigarException e) {
			logger.error(e.getMessage());
		}
		
		return result;
		
	}

}
