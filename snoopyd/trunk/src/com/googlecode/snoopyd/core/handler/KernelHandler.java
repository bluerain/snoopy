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

import com.googlecode.snoopyd.core.event.ChildNodeDeadedEvent;
import com.googlecode.snoopyd.core.event.ChildSessionRecivedEvent;
import com.googlecode.snoopyd.core.event.ChildSessionSendedEvent;
import com.googlecode.snoopyd.core.event.DiscoverRecivedEvent;
import com.googlecode.snoopyd.core.event.ExceptionEvent;
import com.googlecode.snoopyd.core.event.ForceStartEvent;
import com.googlecode.snoopyd.core.event.InvokationEvent;
import com.googlecode.snoopyd.core.event.KernelEvent;
import com.googlecode.snoopyd.core.event.KernelReconfiguredEvent;
import com.googlecode.snoopyd.core.event.KernelStateChangedEvent;
import com.googlecode.snoopyd.core.event.NetworkDisabledEvent;
import com.googlecode.snoopyd.core.event.NetworkEnabledEvent;
import com.googlecode.snoopyd.core.event.ParentNodeDeadedEvent;
import com.googlecode.snoopyd.core.event.ParentSessionRecivedEvent;
import com.googlecode.snoopyd.core.event.ParentSessionSendedEvent;
import com.googlecode.snoopyd.core.event.ResultRecievedEvent;
import com.googlecode.snoopyd.core.event.ScheduleTimeComeEvent;
import com.googlecode.snoopyd.core.event.ScheduleUpdatedEvent;
import com.googlecode.snoopyd.core.event.SnoopydStartedEvent;
import com.googlecode.snoopyd.core.event.SnoopydTerminatedEvent;

public interface KernelHandler {
	
	public void handle(KernelEvent event);
	
	public void handle(NetworkEnabledEvent event);
	
	public void handle(NetworkDisabledEvent event);
	
	public void handle(ChildSessionSendedEvent event);
	
	public void handle(ChildSessionRecivedEvent event);
	
	public void handle(DiscoverRecivedEvent event);
	
	public void handle(ParentNodeDeadedEvent event);
	
	public void handle(ChildNodeDeadedEvent event);
	
	public void handle(SnoopydStartedEvent event);
	
	public void handle(SnoopydTerminatedEvent event);
	
	public void handle(KernelStateChangedEvent event);
	
	public void handle(InvokationEvent event);
	
	public void handle(ScheduleTimeComeEvent event);
	
	public void handle(ExceptionEvent event);
	
	public void handle(ScheduleUpdatedEvent event);
	
	public void handle(ParentSessionRecivedEvent event);
	
	public void handle(ParentSessionSendedEvent event);
	
	public void handle(ResultRecievedEvent event);
	
	public void handle(ForceStartEvent event);
	
	public void handle(KernelReconfiguredEvent event);

}