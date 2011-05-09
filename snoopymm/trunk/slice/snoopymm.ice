// **********************************************************************
// Copyright 2011 Snoopy Project 
//
// Licensed under the Apache License, Version 2.0 (the "License");
// You may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//      
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// **********************************************************************

#ifndef SNOOPYMM_ICE
#define SNOOPYMM_ICE

module com { module googlecode { module snoopymm {
		
	sequence<string> StringArray;
	dictionary<string, string> StringMap;

	exception ModuleNotFoundException
	{
	};
	
	interface IModuleManager
	{
		void deploy(string muid, string code);
		void undeploy(string muid) throws ModuleNotFoundException;
		
		StringMap fetch();
		
		StringArray launch(string muid, StringArray params) throws ModuleNotFoundException;	
	};


}; }; };

#endif
