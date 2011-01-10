package com.googlecode.snoopyd.core.state;

import com.googlecode.snoopyd.core.Kernel;
import com.googlecode.snoopyd.core.handler.KernelHandler;
import com.googlecode.snoopyd.core.handler.OfflineHandler;

public class OfflineState implements KernelState {

	private Kernel kernel;
	private KernelHandler handler;

	public OfflineState(Kernel kernel) {
		this.kernel = kernel;
		this.handler = null;
	}

	@Override
	public KernelHandler handler() {
		if (handler == null) {
			synchronized (this) {
				handler = new OfflineHandler(kernel);
			}
		}
		return handler;
	}
}
