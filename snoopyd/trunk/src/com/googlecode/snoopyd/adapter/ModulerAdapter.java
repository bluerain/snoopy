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
package com.googlecode.snoopyd.adapter;

import java.util.Map;

import Ice.Current;

import com.googlecode.snoopyd.driver.Moduler;
import com.googlecode.snoopyd.driver._IModulerDisp;
import com.googlecode.snoopymm.ModuleNotFoundException;

public class ModulerAdapter extends _IModulerDisp {

	private Moduler moduler;

	public ModulerAdapter(Moduler moduler) {
		this.moduler = moduler;
	}

	@Override
	public Map<String, String> fetch(Current __current) {
		return moduler.fetch();
	}

	@Override
	public void deploy(String muid, String code, Current __current) {
		moduler.deploy(muid, code);
	}

	@Override
	public void undeploy(String muid, Current __current) {
		try {
			moduler.undeploy(muid);
		} catch (ModuleNotFoundException ex) {

		}
	}

	@Override
	public String[] launch(String muid, String[] params, Current __current)
			throws com.googlecode.snoopyd.driver.ModuleNotFoundException {
		return moduler.launch(muid, params);
	}

	@Override
	public void force(String muid, String[] params, Current __current) {
		moduler.force(muid, params);
	}
}
