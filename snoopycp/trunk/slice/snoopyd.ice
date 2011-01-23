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
			
				
			void helloKernel();
		};
	
		interface IUserSession extends ISession
		{
			driver::IHoster* hoster();
			driver::IController* controller();
		
			void helloUser();
		};
	
	};
	
	module driver
	{
		
		dictionary<string, string> HostContext; 
		
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
 			HostContext context();
 		};
 		
 		interface IController
 		{
 			void shutdown(); 		
 		};
 		
	}; 

}; }; };

#endif
