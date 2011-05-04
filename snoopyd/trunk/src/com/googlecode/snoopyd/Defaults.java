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
	
	public static final String DEFAULT_CONFIGURATION = "snoopyd.conf";
	
	public static final String DEFAULT_PRIMARY_ADAPTER_NAME = "PrimaryAdapter";
	public static final String DEFAULT_SECONDARY_ADAPTER_NAME = "SecondaryAdapter";
	
	public static final int DEFAULT_DISCOVER_INTERVAL = 5000;
	public static final int DEFAULT_DISCOVER_TIMEOUT = 6 * DEFAULT_DISCOVER_INTERVAL;
	
	/*
	 * This is rate from my first laptop (Samsung R20).    
	 */
	public static final int DEFAULT_BASELINE_RATE = 912;
	

}
