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

package com.googlecode.snoopyd.util;

import Ice.Identity;

public final class Identities {
	
	public static Identity randomIdentity(String domain) {
		return new Identity(java.util.UUID.randomUUID().toString(), domain);		
	}
	
	public static String toString(Identity identity) {
		return identity.category + "/" + identity.name;
	}
	
	public static Identity clone(Identity identity) {
		return null;
	}
	
	public static Identity stringToIdentity(String identity) {
		String args[] = identity.split("/");
		return new Identity(args[0], args.length > 1 ? args[1] : "");
	}
	
	public static boolean isEquals(Identity id1, Identity id2) {
		return id1.name.equals(id2.name) && id1.category.equals(id2.category);
	}
}
