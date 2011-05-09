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

package com.googlecode.snoopyd;

public final class Defaults {
	
	public static final String APP_NAME = "snoopyd";
	public static final String APP_VER = "0.0.0";
	
	public static final String CONFIGURATION = "snoopyd.conf";
	
	public static final String PRIMARY_ADAPTER_NAME = "PrimaryAdapter";
	public static final String SECONDARY_ADAPTER_NAME = "SecondaryAdapter";
	
	public static final int DISCOVER_INTERVAL = 5000;
	public static final int DISCOVER_TIMEOUT = 6 * DISCOVER_INTERVAL;
	
	public static final int ALIVE_INTERVAL = 15000;
	
	public static final int MODULER_INTERVAL = 15000;
	
	public static final String KERNEL_THREAD_NAME = "Kernel-Thread";
	public static final String ALIVER_THREAD_NAME = "Aliver-Thread";
	public static final String DISCOVERER_THREAD_NAME = "Discoverer-Thread";
	public static final String INVOKER_THREAD_NAME = "Invoker-Thread";
	public static final String MODULER_THREAD_NAME = "Moduler-Thread";

	/*
	 * This is rate from my first laptop (Samsung R20).    
	 */
	public static final int BASELINE_RATE = 912;
}
