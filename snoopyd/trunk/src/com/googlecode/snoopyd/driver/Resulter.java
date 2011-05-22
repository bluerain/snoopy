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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

import com.googlecode.snoopyd.Defaults;
import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.state.ActiveState;
import com.googlecode.snoopyd.core.state.KernelListener;
import com.googlecode.snoopyd.core.state.KernelState;

public class Resulter extends AbstractDriver implements Driver, Startable,
		Runnable, KernelListener {

	private static Logger logger = Logger.getLogger(Resulter.class);

	public static class Result {

		private String hostname;
		private String osname;
		private String module;
		private String[] result;

		public Result(String hostname, String osname, String module, String[] result) {
			this.hostname = hostname;
			this.osname = osname;
			this.module = module;
			this.result = result;
		}

		public String hostname() {
			return hostname;
		}
		
		public String osname() {
			return osname;
		}

		public String module() {
			return module;
		}

		public String[] result() {
			return result;
		}
	}

	private Connection connection;

	private Queue<Result> pool;

	private boolean started;

	public Resulter(Kernel kernel) {
		super(Resulter.class.getSimpleName(), kernel);

		this.pool = new LinkedList<Result>();
		this.started = false;
	}

	public synchronized void store(String hostname, String osname, String module,
			String[] result) {
		
		logger.debug("storing result " + Arrays.toString(result));

		pool.offer(new Result(hostname, osname, module, result));

		if (pool.size() > Defaults.RESULTER_THRESHHOLD) {
			notify();
		}
	}

	public synchronized void start() {
		logger.debug("starting " + name);

		try {

			String connectionurl = kernel.configuration().getProperty("connectionstring");
			
			logger.debug("use connection url: " + connectionurl);
			
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(connectionurl);

		} catch (ClassNotFoundException ex) {
			logger.error(ex.getMessage());
		} catch (SQLException ex) {
			logger.error(ex.getMessage());
		}

		Thread self = new Thread(this, Defaults.RESULTER_THREAD_NAME);
		self.start();

		started = true;
	}

	@Override
	public synchronized void stop() {
		logger.debug("stoping " + name);

		try {
			connection.close();
		} catch (SQLException ex) {

		} catch (NullPointerException ex) {
			
		}

		started = false;
		notify();

		try {
			wait();
		} catch (InterruptedException e) {
			logger.warn(e.getMessage());
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
	public void run() {

		for (; started;) {

			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException ignored) {
				}
			}

			for (; !pool.isEmpty() && started;) {

				Result result = pool.poll();

				try {
					
					StringBuilder sb = new StringBuilder();
					for (String res: result.result()) {
						sb.append(res);
						sb.append(";");
					}

					Statement stmt = connection.createStatement();
					stmt.executeUpdate(String.format("Call storeResult(\"%s\", \"%s\", \"%s\", \"%s\");", result.osname(), result.hostname(), result.module(), sb.toString()));
					stmt.close();

				} catch (SQLException ex) {
					logger.debug(ex.getMessage());
				}
			}
		}

		synchronized (this) {
			notify();
		}
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
