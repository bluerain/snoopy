package com.googlecode.snoopyd.core.state;

import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.handler.KernelHandler;
import com.googlecode.snoopyd.core.handler.PassiveHandler;

public class PassiveState implements KernelState {

	private Kernel kernel;

	private KernelHandler handler;

	public PassiveState(Kernel kernel) {
		this.kernel = kernel;
		this.handler = null;
	}

	@Override
	public KernelHandler handler() {
		if (handler == null) {
			synchronized (this) {
				handler = new PassiveHandler(kernel);
			}
		}
		return handler;
	}
}
