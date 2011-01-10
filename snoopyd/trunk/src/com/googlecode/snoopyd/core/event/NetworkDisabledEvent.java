package com.googlecode.snoopyd.core.event;


public class NetworkDisabledEvent implements KernelEvent {

	@Override
	public String name() {
		return this.getClass().getSimpleName();
	}
}