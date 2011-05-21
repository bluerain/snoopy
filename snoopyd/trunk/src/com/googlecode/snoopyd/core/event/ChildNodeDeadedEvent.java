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
package com.googlecode.snoopyd.core.event;

import Ice.Identity;

public class ChildNodeDeadedEvent implements KernelEvent {

	private Ice.Identity identity;
	
	public ChildNodeDeadedEvent(Identity identity) {
		this.identity = identity;
	}

	public Ice.Identity identity() {
		return identity;
	}
	
	@Override
	public String name() {
		return this.getClass().getSimpleName();
	}
}
