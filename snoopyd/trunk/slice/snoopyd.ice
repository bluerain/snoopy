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

#ifndef SNOOPYD_ICE
#define SNOOPYD_ICE

#include <Ice/Identity.ice>

module com { module googlecode { module snoopyd {

	module driver
	{
		interface IHoster;
		interface IDiscoverer;
		interface ISessionier;
		interface IController;
		interface IModuler;
		interface IScheduler;
		interface IConfigurer;
			
	};

	module session 
	{
		interface ISession 
		{
			void refresh();
			void destroy();
		};
		
		interface IKernelSession extends ISession
		{
			driver::IModuler* moduler();
			driver::IScheduler* scheduler();
		};
	
		interface IUserSession extends ISession
		{
			driver::IHoster* hoster();
			driver::IController* controller();
			driver::IModuler* moduler();
			driver::IConfigurer* configurer();
			driver::IScheduler* scheduler();
		};
	
	};
	
	module driver
	{
		exception ModuleNotFoundException
		{
		};
		
		dictionary<string, string> StringMap; 
		sequence<string> StringArray;
		sequence<long> LongArray;
		
 		interface IDiscoverer 
 		{
 			void discover(Ice::Identity identity);
 		};
 		
 		interface ISessionier
 		{
 			session::IKernelSession* createKernelSession(Ice::Identity identity, session::IKernelSession* selfSession);
			session::IUserSession* createUserSession(Ice::Identity identity, session::IUserSession* selfSession);
 		};
 		
 		interface IHoster
 		{
 			StringMap context();
 		};
 		
 		interface IController
 		{
 			void shutdown(); 		
 		};
 		
 		interface IModuler
 		{
 			StringMap fetch();
 			void deploy(string muid, string code);
 			void undeploy(string muid) throws ModuleNotFoundException;
		
			StringArray launch(string muid, StringArray params) throws ModuleNotFoundException;
			
			void force(string muid, StringArray params);

 		};
 		
 		interface IScheduler
 		{
 			void synchronize(Ice::Identity identity, IScheduler* remoteScheduler);
 			 			
 			StringMap timetable();
 			StringMap statetable();
 			StringMap paramtable();
 			
 			void schedule(string muid, LongArray delays, StringArray params);
 			void unschedule(string muid) throws ModuleNotFoundException;
 			
 			void force(Ice::Identity identity, string muid, StringArray params);
 			
 			void toogle(string muid); 
 			
 		};
 		
 		interface IConfigurer
 		{
 			void reconfigure(StringMap configuration);
 			StringMap configuration();
 		};
 		
	}; 

}; }; };

#endif
