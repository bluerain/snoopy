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

import java.util.Map;

import Ice.Current;
import Ice.Identity;

import com.googlecode.snoopyd.driver.ISchedulerPrx;
import com.googlecode.snoopyd.driver.Scheduler;
import com.googlecode.snoopyd.driver._ISchedulerDisp;

public class SchedulerAdapter extends _ISchedulerDisp {

	private Scheduler scheduler;
	
	public SchedulerAdapter(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	@Override
	public Map<String, String> timetable(Current __current) {
		return scheduler.timetable();
	}

	@Override
	public Map<String, String> statetable(Current __current) {
		return scheduler.statetable();
	}

	@Override
	public void schedule(String muid, long delay, Current __current) {
		scheduler.schedule(muid, delay);
	}

	@Override
	public void toogle(String muid, Current __current) {
		scheduler.toogle(muid);
	}

	@Override
	public void synchronize(Identity identity, ISchedulerPrx remoteScheduler,
			Current __current) {
		scheduler.synchronize(identity, remoteScheduler);
	}

	@Override
	public Map<String, String> paramtable(Current __current) {
		return scheduler.paramtable();
	}
}
