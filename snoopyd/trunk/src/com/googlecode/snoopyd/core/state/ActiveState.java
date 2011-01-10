package com.googlecode.snoopyd.core.state;

import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.handler.ActiveHandler;
import com.googlecode.snoopyd.core.handler.KernelHandler;

public class ActiveState implements KernelState {

	private Kernel kernel;

	private KernelHandler handler;

	public ActiveState(Kernel kernel) {
		this.kernel = kernel;
		this.handler = null;
	}

	@Override
	public KernelHandler handler() {
		if (handler == null) {
			synchronized (this) {
				handler = new ActiveHandler(kernel);
			}
		}
		return handler;
	}
}
