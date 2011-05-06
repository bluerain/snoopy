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
package com.googlecode.snoopyd.module;

import java.util.UUID;

public class PythonModule extends AbstractModule implements Module {

	public PythonModule(UUID uudi, String name) {
		super(uudi, name);
	}

	@Override
	public Object invoke(Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

}
