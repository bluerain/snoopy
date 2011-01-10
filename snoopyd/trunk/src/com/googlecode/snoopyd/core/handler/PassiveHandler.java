package com.googlecode.snoopyd.core.handler;

import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.event.NetworkDisabledEvent;
import com.googlecode.snoopyd.core.event.NetworkEnabledEvent;

public class PassiveHandler extends AbstractHandler implements
		KernelHandler {

	public PassiveHandler(Kernel kernel) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handle(NetworkEnabledEvent event) {

	}

	@Override
	public void handle(NetworkDisabledEvent event) {
		// TODO Auto-generated method stub

	}

}