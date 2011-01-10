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
