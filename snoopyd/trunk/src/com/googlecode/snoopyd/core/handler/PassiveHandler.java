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

package com.googlecode.snoopyd.core.handler;

import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.event.ChildSessionSendedEvent;
import com.googlecode.snoopyd.core.event.DiscoverRecivedEvent;
import com.googlecode.snoopyd.core.event.NetworkDisabledEvent;
import com.googlecode.snoopyd.core.event.NetworkEnabledEvent;

public class PassiveHandler extends AbstractHandler implements
		KernelHandler {

	private Kernel kernel;
	
	public PassiveHandler(Kernel kernel) {
		this.kernel = kernel;
	}

	@Override
	public void handle(NetworkEnabledEvent event) {

	}

	@Override
	public void handle(NetworkDisabledEvent event) {

	}

	@Override
	public void handle(ChildSessionSendedEvent event) {
		
	}

	@Override
	public void handle(DiscoverRecivedEvent event) {
		
	}
}