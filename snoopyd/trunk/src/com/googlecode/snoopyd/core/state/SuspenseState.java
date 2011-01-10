package com.googlecode.snoopyd.core.state;

import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.handler.KernelHandler;
import com.googlecode.snoopyd.core.handler.SuspenseHandler;

public class SuspenseState implements KernelState {

	private Kernel kernel;

	private KernelHandler handler;

	public SuspenseState(Kernel kernel) {
		this.kernel = kernel;
		this.handler = null;
	}

	@Override
	public KernelHandler handler() {
		if (handler == null) {
			synchronized (this) {
				handler = new SuspenseHandler(kernel);
			}
		}
		return handler;
	}
}
