package com.googlecode.snoopyd.core.handler;

import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.event.KernelEvent;
import com.googlecode.snoopyd.core.event.NetworkDisabledEvent;
import com.googlecode.snoopyd.core.event.NetworkEnabledEvent;

public class OfflineHandler extends AbstractHandler implements
		KernelHandler {

	private Kernel kernel;

	public OfflineHandler(Kernel kernel) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handle(KernelEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handle(NetworkEnabledEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handle(NetworkDisabledEvent event) {
		// TODO Auto-generated method stub

	}

	// @Override
	// public void handle(KernelEvent event) {
	// logger.debug("not handled " + event.getClass().getSimpleName());
	// }
}