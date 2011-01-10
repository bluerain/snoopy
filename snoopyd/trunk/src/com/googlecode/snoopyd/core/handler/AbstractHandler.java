package com.googlecode.snoopyd.core.handler;

import com.googlecode.snoopyd.core.event.KernelEvent;
import com.googlecode.snoopyd.core.event.NetworkDisabledEvent;
import com.googlecode.snoopyd.core.event.NetworkEnabledEvent;

public abstract class AbstractHandler implements KernelHandler {

	@Override
	public void handle(KernelEvent event) {

		if (event instanceof NetworkEnabledEvent) {
			handle((NetworkEnabledEvent) event);
		} else if (event instanceof NetworkDisabledEvent) {
			handle((NetworkDisabledEvent) event);
		} else {
			// logger.debug("can not handle event: " + event.name());
		}
	}

}
