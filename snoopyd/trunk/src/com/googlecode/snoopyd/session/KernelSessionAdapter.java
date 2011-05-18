/**
 * Copyright 2011 Snoopy Project 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlecode.snoopyd.session;

import Ice.Current;

import com.googlecode.snoopyd.driver.IModulerPrx;
import com.googlecode.snoopyd.driver.IResulterPrx;
import com.googlecode.snoopyd.driver.ISchedulerPrx;

public class KernelSessionAdapter extends _IKernelSessionDisp {

	private KernelSession kernelSession;
	
	public KernelSessionAdapter(KernelSession kernelSession) {
		this.kernelSession = kernelSession;
	}

	@Override
	public void destroy(Current __current) {
		
	}

	@Override
	public void refresh(Current __current) {

	}

	@Override
	public IModulerPrx moduler(Current __current) {
		return kernelSession.moduler();
	}

	@Override
	public IResulterPrx resulter(Current __current) {
		return kernelSession.resulter();
	}

	@Override
	public ISchedulerPrx scheduler(Current __current) {
		return kernelSession.scheduler();
	}
	
	
}
