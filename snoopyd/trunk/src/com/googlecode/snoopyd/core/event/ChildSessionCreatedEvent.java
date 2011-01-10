package com.googlecode.snoopyd.core.event;

public class ChildSessionCreatedEvent implements KernelEvent {

	@Override
	public String name() {
		return this.getClass().getSimpleName();
	}
}
