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

import org.apache.log4j.Logger;

import com.googlecode.snoopyd.Defaults;
import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.Kernel.KernelException;
import com.googlecode.snoopyd.core.event.ChildSessionRecivedEvent;
import com.googlecode.snoopyd.core.event.ChildSessionSendedEvent;
import com.googlecode.snoopyd.core.event.DiscoverRecivedEvent;
import com.googlecode.snoopyd.core.event.ExceptionEvent;
import com.googlecode.snoopyd.core.event.InvokationEvent;
import com.googlecode.snoopyd.core.event.KernelEvent;
import com.googlecode.snoopyd.core.event.KernelStateChangedEvent;
import com.googlecode.snoopyd.core.event.NetworkDisabledEvent;
import com.googlecode.snoopyd.core.event.NetworkEnabledEvent;
import com.googlecode.snoopyd.core.event.ParentNodeDeadedEvent;
import com.googlecode.snoopyd.core.event.ScheduleTimeComeEvent;
import com.googlecode.snoopyd.core.event.SnoopydStartedEvent;
import com.googlecode.snoopyd.core.event.SnoopydTerminatedEvent;

public abstract class AbstractHandler implements KernelHandler {

	private static Logger logger = Logger.getLogger(AbstractHandler.class);
	
	protected Kernel kernel;
	
	public AbstractHandler(Kernel kernel) {
		this.kernel = kernel;
	}

	@Override
	public void handle(KernelEvent event) {

		if (event instanceof NetworkEnabledEvent) {
			
			handle((NetworkEnabledEvent) event);
			
		} else if (event instanceof NetworkDisabledEvent) {
			
			handle((NetworkDisabledEvent) event);
		
		} else if (event instanceof ChildSessionSendedEvent) {
		
			handle((ChildSessionSendedEvent) event);
		
		} else if (event instanceof ChildSessionRecivedEvent) { 
		
			handle((ChildSessionRecivedEvent) event);
		
		} else if (event instanceof DiscoverRecivedEvent) {
		
			handle((DiscoverRecivedEvent) event);
		
		} else if (event instanceof ParentNodeDeadedEvent) {
		
			handle((ParentNodeDeadedEvent) event);
		
		} else if (event instanceof SnoopydStartedEvent) {
		
			handle((SnoopydStartedEvent) event);
		
		} else if (event instanceof SnoopydTerminatedEvent) {
		
			handle((SnoopydTerminatedEvent) event);
		
		} else if (event instanceof KernelStateChangedEvent) {

			handle((KernelStateChangedEvent) event);
			
		} else if (event instanceof InvokationEvent) {
			
			handle((InvokationEvent) event);
			
		} else if (event instanceof ScheduleTimeComeEvent) {
			
			handle((ScheduleTimeComeEvent) event);
		
		} else if (event instanceof ExceptionEvent) {
			
			handle((ExceptionEvent) event);
			
		} else {
		
			logger.warn("not found handler for " + event.name());
		
		}
	}
	
	@Override
	public void handle(ExceptionEvent event) {
		
		if (event.exception() instanceof KernelException) {
		
			throw (KernelException) event.exception();
		
		} else {
			
			kernel.proccess(event.exception());
		}
	}

	@Override
	public void handle(KernelStateChangedEvent event) {
		kernel.toogle(event.state());
	}
	
	@Override
	public void handle(InvokationEvent event) {
		
		Thread thread = new Thread(event, Defaults.INVOKATION_THREAD_NAME + "-" + java.util.UUID.randomUUID().toString());
		thread.start();

	}

	@Override
	public void handle(SnoopydStartedEvent event) {
		kernel.init();
	}

	@Override
	public void handle(SnoopydTerminatedEvent event) {
    	
		kernel.dispose();
    	
    	synchronized (event) {
    		event.notify();
		}
	}
}
