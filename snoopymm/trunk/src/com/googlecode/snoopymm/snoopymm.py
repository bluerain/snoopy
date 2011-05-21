# 
# Copyright 2011 Snoopy Project 
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# You may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#      http://www.apache.org/licenses/LICENSE-2.0
#      
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

import sys
import Ice
import os
import re
from xml.dom.minidom import parse

import com.googlecode.snoopymm

class ModuleManager(com.googlecode.snoopymm.IModuleManager): 

	def __init__(self, path, config):
		self.modulesDir = path
		self.modulesConfig = config
		
		self.modules = self.fetch()
		
	def fetch(self, current=None):
		modules = {}
				
		xml = parse(self.modulesConfig)
		for module in xml.getElementsByTagName("module"):
			muid = module.attributes["muid"].nodeValue
			clazz = module.attributes["class"].nodeValue
			modules[muid] = clazz
			
		return modules

	def deploy(self, muid, code, current=None):
		script = open(os.path.join(self.modulesDir, muid + ".py"), "w")
		script.write(code)
		script.close()
		
		clazz = re.match(r"class (\w+)\(.*\):", code)
		clazz = clazz.group(1)
				
		xml = parse(self.modulesConfig)
		modules = xml.getElementsByTagName("modules")[0]
		module = xml.createElement("module")
		module.setAttribute("muid", muid)
		module.setAttribute("class", clazz)
		module.appendChild(xml.createElement("params"))
		modules.appendChild(module)
		
		modulesConfig = open(self.modulesConfig, "w") 
		modulesConfig.write(xml.toxml())
		modulesConfig.close()

		self.modules = self.fetch()
		
	def undeploy(self, muid, current=None):
		os.remove(os.path.join(self.modulesDir, muid + ".py"))
		
		moduleFound = False
		xml = parse(self.modulesConfig)
		modules = xml.getElementsByTagName("modules")[0]
		for module in modules.getElementsByTagName("module"):
			if module.attributes["muid"].nodeValue == muid:
				modules.removeChild(module)
				moduleFound = True
				
		
		if moduleFound:
			modulesConfig = open(self.modulesConfig, "w") 
			modulesConfig.write(xml.toxml())
			modulesConfig.close()
		else:
			print "module not found"

	def launch(self, muid, params, current=None):
		moduleName = self.modules[muid]
		
		dyncode = "module = __import__(muid); " + "module = module." + moduleName + "(); " + "result = module.invoke(params); "   
		exec(dyncode)
		
		return result

class Snoopymm(Ice.Application):
	
	def run(self, args):
		properties = self.communicator().getProperties()
		moduleManager = ModuleManager(properties.getProperty("Snoopy.ModulesDir"), properties.getProperty("Snoopy.ModulesConfig"))
		adapter = self.communicator().createObjectAdapter("Adapter")
		adapter.add(moduleManager, self.communicator().stringToIdentity("ModuleManager"))
		adapter.activate()
		self.communicator().waitForShutdown()
		self.communicator().destroy()
	
		return 0

def main(args):
	snoopymm = Snoopymm();
	sys.exit(snoopymm.main(args[1:], args[1]))

if __name__ == "__main__": main(sys.argv)
