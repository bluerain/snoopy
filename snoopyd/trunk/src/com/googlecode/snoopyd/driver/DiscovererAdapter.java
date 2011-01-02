package com.googlecode.snoopyd.driver;

import com.googlecode.snoopyd.core.Identity;

import Ice.Current;

public class DiscovererAdapter extends _IDiscovererDisp {

	private Discoverer discoverer;
	
	public DiscovererAdapter(Discoverer discoverer) {
		super();
		this.discoverer = discoverer;
	}

	@Override
	public void discover(Current __current) {
		
		discoverer.discover(new Id);
		
		System.out.println("i am here2");
	}

	@Override
	public void request(Current __current) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void offer(Current __current) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pack(Current __current) {
		// TODO Auto-generated method stub
		
	}

}
