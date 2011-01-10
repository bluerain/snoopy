package com.googlecode.snoopyd.core.handler;

import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.event.NetworkDisabledEvent;
import com.googlecode.snoopyd.core.event.NetworkEnabledEvent;

public class ActiveHandler extends AbstractHandler implements
		KernelHandler {

	public ActiveHandler(Kernel kernel) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handle(NetworkEnabledEvent event) {

	}

	@Override
	public void handle(NetworkDisabledEvent event) {

	}

	// @Override
	// public void handle(KernelEvent event) {
	// logger.debug("not handled " + event.getClass().getSimpleName());
	// }
}