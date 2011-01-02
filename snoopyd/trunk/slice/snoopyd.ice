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

module com { module googlecode { module snoopyd {

	module driver
	{
 		interface IController
 		{
 			int rate();
 			void shutdown(); 		
 		};
 		
 		interface IConnector 
 		{
 			void connect();
 			void disconnect();
 		};
 		
 		interface IDiscoverer 
 		{
 			void discover();
 			void request();
 			void offer();
 			void pack();
 		};
 		
	}; 

	module core 
	{
	
		interface ISession
		{                                              
			
		};
		
		interface IKernelSession extends ISession
		{
			driver::IConnector connector();
		};
		
		interface IUserSession extends ISession
		{
			driver::IController controller();
		};
		
		interface ISessionManager 
		{
			IKernelSession createKernelSession();
			IUserSession createUserSession();
		
		};
	};
	
}; }; };

#endif
