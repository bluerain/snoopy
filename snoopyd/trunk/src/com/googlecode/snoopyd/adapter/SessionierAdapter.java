package com.googlecode.snoopyd.adapter;

import org.apache.log4j.Logger;

import Ice.Current;
import Ice.Identity;

import com.googlecode.snoopyd.driver.Driver;
import com.googlecode.snoopyd.driver.Sessionier;
import com.googlecode.snoopyd.driver._ISessionierDisp;
import com.googlecode.snoopyd.session.IKernelSessionPrx;
import com.googlecode.snoopyd.session.IUserSessionPrx;

public class SessionierAdapter extends _ISessionierDisp implements Adapter {

	private static Logger logger = Logger.getLogger(SessionierAdapter.class);

	private String name;
	private Ice.Identity identity;

	private Sessionier sessionier;

	public SessionierAdapter(Identity identity, Sessionier sessionier) {

		this.name = SessionierAdapter.class.getSimpleName();
		this.identity = identity;
		this.sessionier = sessionier;
	}

	@Override
	public IKernelSessionPrx createKernelSession(Identity identity,
			IKernelSessionPrx selfSession, Current __current) {

		return sessionier.createKernelSession(identity, selfSession);
	}

	@Override
	public IUserSessionPrx createUserSession(Identity identity,
			IUserSessionPrx selfSession, Current __current) {

		return sessionier.createUserSession(identity, selfSession);
	}

	@Override
	public Driver driver() {
		return sessionier;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Identity identity() {
		return identity;
	}
}
