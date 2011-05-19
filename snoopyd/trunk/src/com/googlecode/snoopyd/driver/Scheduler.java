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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.googlecode.snoopyd.Defaults;
import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.event.ScheduleTimeComeEvent;
import com.googlecode.snoopyd.core.state.ActiveState;
import com.googlecode.snoopyd.core.state.KernelListener;
import com.googlecode.snoopyd.core.state.KernelState;
import com.googlecode.snoopyd.util.Identities;

public class Scheduler extends AbstractDriver implements Driver, Startable,
		Activable, KernelListener {

	public static enum ScheduleState {
		ON, OFF
	}

	public static class Schedule {

		private HashMap<String, List<Long>> timetable;
		private Map<String, ScheduleState> states;
		private Map<String, List<String>> params;

		public Schedule() {

			this.timetable = new HashMap<String, List<Long>>();
			this.states = new HashMap<String, Scheduler.ScheduleState>();
			this.params = new HashMap<String, List<String>>();
		}

		public HashMap<String, List<Long>> timetable() {
			return timetable;
		}

		public Map<String, ScheduleState> statetable() {
			return states;
		}

		public Map<String, List<String>> paramstable() {
			return params;
		}
	}

	private static Logger logger = Logger.getLogger(Scheduler.class);

	private Schedule self;
	private HashMap<Ice.Identity, Schedule> childs;

	private Map<String, Timer> timers;

	private boolean started;

	public Scheduler(Kernel kernel) {
		super(Scheduler.class.getSimpleName(), kernel);

		this.self = new Schedule();
		this.childs = new HashMap<Ice.Identity, Schedule>();

		this.timers = new HashMap<String, Timer>();

		loadScheduleConfig();
	}

	public void synchronize(Ice.Identity identity, ISchedulerPrx remoteScheduler) {

		logger.debug("synchronize shceduler with "
				+ Identities.toString(identity));

		Schedule childSchedule = new Schedule();

		Map<String, String> remoteTimetable = remoteScheduler.timetable();
		Map<String, String> remoteStatetable = remoteScheduler.statetable();
		Map<String, String> remoteParamtable = remoteScheduler.paramtable();

		for (String muid : remoteTimetable.keySet()) {
			List<Long> times = new ArrayList<Long>();
			for (String time : remoteTimetable.get(muid).split(";")) {
				times.add(Long.parseLong(time));
			}
			childSchedule.timetable().put(muid, times);
		}

		for (String muid : remoteStatetable.keySet()) {
			if (remoteStatetable.get(muid).equals("ON")) {
				childSchedule.statetable().put(muid, ScheduleState.ON);
			} else {
				childSchedule.statetable().put(muid, ScheduleState.OFF);
			}
		}

		for (String muid : remoteParamtable.keySet()) {
			List<String> params = new ArrayList<String>();
			for (String param : remoteParamtable.get(muid).split(";")) {
				params.add(param);
			}
			childSchedule.paramstable().put(muid, params);
		}

		childs.put(identity, childSchedule);

		update();
	}

	public void schedule(String muid, long delay) {

		// if (schedule.containsKey(muid)) {
		// schedule.get(muid).add(delay);
		// } else {
		// schedule.put(muid, Arrays.asList(delay));
		// states.put(muid, ScheduleState.ON);
		// }
	}

	public Map<String, String> timetable() {

		Map<String, String> result = new HashMap<String, String>();

		for (String muid : self.timetable().keySet()) {

			StringBuilder sb = new StringBuilder();
			for (Long time : self.timetable().get(muid)) {
				sb.append(time);
				sb.append(";");
			}

			result.put(muid, sb.toString());
		}

		return result;
	}

	public Map<String, String> statetable() {

		Map<String, String> result = new HashMap<String, String>();

		for (String muid : self.statetable().keySet()) {
			result.put(muid, self.statetable().get(muid).toString());
		}

		return result;
	}

	public Map<String, String> paramtable() {

		Map<String, String> result = new HashMap<String, String>();

		for (String muid : self.paramstable().keySet()) {

			StringBuilder sb = new StringBuilder();
			for (String param : self.paramstable().get(muid)) {
				sb.append(param);
				sb.append(";");
			}

			result.put(muid, sb.toString());
		}

		return result;
	}

	public void toogle(String muid) {
		// ScheduleState state = states.get(muid);
		//
		// if (state == ScheduleState.OFF) {
		// states.put(muid, ScheduleState.ON);
		// } else {
		// states.put(muid, ScheduleState.OFF);
		// }
		//
		// update();
	}

	@Override
	public synchronized void start() {
		logger.debug("starting " + name);

		started = true;
		// update();
	}

	@Override
	public synchronized void stop() {
		logger.debug("stoping " + name);

		for (String muid : timers.keySet()) {
			timers.get(muid).cancel();
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
	
	@Override
	public void activate() {
		loadScheduleConfig();
	}

	@Override
	public void deactivate() {
		saveScheduleConfig();
	}

	private void update() {

		logger.debug("updating scheduler");

		synchronized (timers) {

			for (String muid : timers.keySet()) {
				timers.get(muid).cancel();
			}

			timers.clear();

			for (Ice.Identity identity : childs.keySet()) {
				Schedule childSchedule = childs.get(identity);

				final Ice.Identity fidentity = identity;

				for (String muid : childSchedule.timetable().keySet()) {
					if (childSchedule.statetable().get(muid) == ScheduleState.ON) {
						final String fmuid = muid;
						final String[] fprms = childSchedule
								.paramstable()
								.get(muid)
								.toArray(
										new String[childSchedule.paramstable()
												.get(muid).size()]);

						Timer timer = new Timer(Defaults.TIMER_THREAD_NAME
								+ "-" + muid);
						for (Long delay : childSchedule.timetable().get(muid)) {
							timer.schedule(new TimerTask() {
								@Override
								public void run() {
									kernel.handle(new ScheduleTimeComeEvent(
											fidentity, fmuid, fprms));
								}
							}, 0, delay.longValue());
						}

						timers.put(muid, timer);
					}
				}
			}
		}
	}

	private void loadScheduleConfig() {
		try {

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(new File(kernel.properties().getProperty(
					"Snoopy.SchedulerConfig")));

			Element root = dom.getDocumentElement();

			NodeList modules = root.getElementsByTagName("module");
			for (int i = 0; i < modules.getLength(); i++) {
				Element module = (Element) modules.item(i);
				String muid = module.getAttribute("muid");
				String state = module.getAttribute("state");

				List<Long> delaysList = new ArrayList<Long>();
				NodeList delays = module.getElementsByTagName("delay");
				for (int j = 0; j < delays.getLength(); j++) {
					Element delay = (Element) delays.item(i);
					String value = delay.getAttribute("value");
					delaysList.add(Long.parseLong(value));
				}

				List<String> paramsList = new ArrayList<String>();
				NodeList prms = module.getElementsByTagName("param");
				for (int j = 0; j < prms.getLength(); j++) {
					Element param = (Element) prms.item(i);
					String value = param.getAttribute("value");
					paramsList.add(value);
				}

				self.timetable().put(muid, delaysList);
				self.paramstable().put(muid, paramsList);

				if (state.equals("ON")) {
					self.statetable().put(muid, ScheduleState.ON);
				} else {
					self.statetable().put(muid, ScheduleState.OFF);
				}
			}

		} catch (ParserConfigurationException ex) {

		} catch (IOException ex) {

		} catch (SAXException ex) {

		}
	}

	private void saveScheduleConfig() {
		
		try {
		
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();
			
			Element root = doc.createElement("modules");
			doc.appendChild(root);
			
			for (String muid: self.timetable().keySet()) {
				Element module = doc.createElement("module");
				module.setAttribute("muid", muid);
				if (self.statetable().get(muid) == ScheduleState.ON) {
					module.setAttribute("state", "ON");
				} else {
					module.setAttribute("state", "OFF");
				}
				
				Element schedule = doc.createElement("schedule");
				for (Long time: self.timetable().get(muid)) {
					Element delay = doc.createElement("delay");
					delay.setAttribute("value", time.toString());
					schedule.appendChild(delay);
				}
				
				module.appendChild(schedule);
				
				Element params = doc.createElement("params");
				for (String value: self.paramstable().get(muid)) {
					Element param = doc.createElement("param");
					param.setAttribute("value", value);
					params.appendChild(param);
				}
				
				module.appendChild(params);
				root.appendChild(module);
			}
			
			TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            trans.setOutputProperty(OutputKeys.STANDALONE, "no");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            
            DocumentType dt = doc.getDoctype();
		      if (dt != null) {
		        String pub = dt.getPublicId();
		        if (pub != null) {
		          trans.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, pub);
		        }
		        trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dt.getSystemId());
		      }

            OutputStreamWriter sw = new OutputStreamWriter(new FileOutputStream(new File(kernel.properties().getProperty(
					"Snoopy.SchedulerConfig"))));
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);
        
		} catch (ParserConfigurationException ex) {
			
		} catch (TransformerConfigurationException ex) {
			
		} catch (TransformerException ex) {
			
		} catch (FileNotFoundException ex) {
			
		}
	}
}
