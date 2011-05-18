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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.googlecode.snoopyd.Defaults;
import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.event.ScheduleTimeComeEvent;
import com.googlecode.snoopyd.core.state.ActiveState;
import com.googlecode.snoopyd.core.state.KernelListener;
import com.googlecode.snoopyd.core.state.KernelState;

public class Scheduler extends AbstractDriver implements Driver, Startable,
		KernelListener {

	public static enum ScheduleState {
		ON, OFF
	}

	private static Logger logger = Logger.getLogger(Scheduler.class);

	private Map<String, List<Long>> schedule;
	private Map<String, ScheduleState> states;
	private Map<String, Timer> timers;
	private Map<String, List<String>> params;

	private boolean started;

	private Timer self;

	public Scheduler(Kernel kernel) {
		super(Scheduler.class.getSimpleName(), kernel);

		this.schedule = new HashMap<String, List<Long>>();
		this.states = new HashMap<String, Scheduler.ScheduleState>();
		this.timers = new HashMap<String, Timer>();
		this.params = new HashMap<String, List<String>>();
	}

	public void schedule(String muid, long delay) {

		if (schedule.containsKey(muid)) {
			schedule.get(muid).add(delay);
		} else {
			schedule.put(muid, Arrays.asList(delay));
			states.put(muid, ScheduleState.ON);
		}
	}

	public Map<String, String> timetable() {
		return null;
	}

	public Map<String, String> statetable() {
		return null;
	}

	public void toogle(String muid) {
		final String fmuid = muid;
		ScheduleState state = states.get(muid);

		if (state == ScheduleState.OFF) {
			states.put(muid, ScheduleState.ON);

			final String[] prms = params.get(muid).toArray(
					new String[params.get(muid).size()]);

			Timer timer = new Timer(Defaults.TIMER_THREAD_NAME + "-" + muid);
			TimerTask timerTask = new TimerTask() {
				@Override
				public void run() {
					kernel.handle(new ScheduleTimeComeEvent(fmuid, prms));
				}
			};

			for (Long delay : schedule.get(muid)) {
				timer.schedule(timerTask, delay.longValue());
			}

			timers.put(muid, timer);
		} else {
			states.put(muid, ScheduleState.OFF);
			timers.get(muid).cancel();
		}
	}

	@Override
	public synchronized void start() {
		logger.debug("starting " + name);

		started = true;

	}

	@Override
	public synchronized void stop() {
		logger.debug("stoping " + name);

		self.cancel();
	}

	@Override
	public synchronized boolean started() {
		return started;
	}

	@Override
	public synchronized void restart() {

		logger.debug("restarting " + name);

		stop();
		start();
	}

	@Override
	public void stateChanged(KernelState currentState) {
		if (currentState instanceof ActiveState) {
			if (!started) {
				start();
			}
		} else {
			if (started) {
				stop();
			}
		}
	}
}
