package com.googlecode.snoopyd.core.event;


public class NetworkEnabledEvent implements KernelEvent {

	@Override
	public String name() {
		return this.getClass().getSimpleName();
	}
}
