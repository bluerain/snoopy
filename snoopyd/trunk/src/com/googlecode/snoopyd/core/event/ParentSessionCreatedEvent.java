package com.googlecode.snoopyd.core.event;


public class ParentSessionCreatedEvent implements KernelEvent {

	@Override
	public String name() {
		return this.getClass().getSimpleName();
	}
}
