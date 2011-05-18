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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
		ScheduleState state = states.get(muid);

		if (state == ScheduleState.OFF) {
			states.put(muid, ScheduleState.ON);
		} else {
			states.put(muid, ScheduleState.OFF);
		}
		
		update();
	}

	@Override
	public synchronized void start() {
		logger.debug("starting " + name);

		started = true;
		
		loadScheduleConfig();
		update();

	}

	@Override
	public synchronized void stop() {
		logger.debug("stoping " + name);
		
		for (Timer timer: timers.values()) {
			timer.cancel();
		}
		
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
	
	private void update() {

		for (String muid: schedule.keySet()) {
			if (states.get(muid) == ScheduleState.ON) {
				final String fmuid = muid;
				final String[] prms = params.get(muid).toArray(
						new String[params.get(muid).size()]);
				
				Timer timer = new Timer(Defaults.TIMER_THREAD_NAME + "-" + muid);

				for (Long delay : schedule.get(muid)) {
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							kernel.handle(new ScheduleTimeComeEvent(fmuid, prms));	
						}
					}, 0, delay.longValue());
				}

				timers.put(muid, timer);
				
			} else {
				
			}
		}
	}
	
	private void loadScheduleConfig() {
		try {
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(new File(kernel.properties().getProperty("Snoopy.SchedulerConfig")));
			
			Element root = dom.getDocumentElement();
			
			NodeList modules = root.getElementsByTagName("module");
			for (int i=0; i<modules.getLength(); i++) {
				Element module = (Element) modules.item(i);
				String muid = module.getAttribute("muid");
				String state = module.getAttribute("state");
				
				List<Long> delaysList = new ArrayList<Long>();
				NodeList delays = module.getElementsByTagName("delay");
				for (int j=0; j<delays.getLength(); j++) {
					Element delay  = (Element) delays.item(i);
					String value = delay.getAttribute("value");
					delaysList.add(Long.parseLong(value));
				}
				
				List<String> paramsList = new ArrayList<String>();
				NodeList prms = module.getElementsByTagName("param");
				for (int j=0; j<prms.getLength(); j++) {
					Element param  = (Element) prms.item(i);
					String value = param.getAttribute("value");
					paramsList.add(value);
				}
				
				schedule.put(muid, delaysList);
				params.put(muid, paramsList);
				
				if (state.equals("ON")) {
					states.put(muid, ScheduleState.ON);
				} else {
					states.put(muid, ScheduleState.OFF);
				}
			}

		} catch (ParserConfigurationException ex) {

		} catch (IOException ex) {
			
		} catch (SAXException ex) {
			
		}
	}
}
