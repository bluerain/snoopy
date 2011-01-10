package com.googlecode.snoopyd.core.handler;

import com.googlecode.snoopyd.core.event.KernelEvent;
import com.googlecode.snoopyd.core.event.NetworkDisabledEvent;
import com.googlecode.snoopyd.core.event.NetworkEnabledEvent;

public interface KernelHandler {
	
	public void handle(KernelEvent event);
	
	public void handle(NetworkEnabledEvent event);
	
	public void handle(NetworkDisabledEvent event);
	
}