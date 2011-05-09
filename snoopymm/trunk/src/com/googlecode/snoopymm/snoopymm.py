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
import com.googlecode.snoopymm

class ModuleManager(com.googlecode.snoopymm.IModuleManager): 
	def __init__(self, path):
		self.modulesDir = path

	def deploy(self, muid, code, current=None):
		pass
	
	def undeploy(self, muid, current=None):
		pass

	def fetch(self, current=None):
		modules = {}
		files = os.listdir(self.modulesDir)
		for file in files:
			if (file.split(".")[1] == "py"):
				muid = file.split(".")[0]
				for line in open(os.path.join(self.modulesDir, file), "r").readlines():
					if line.find("(object)"):
						classes = line.split("(object)")[0].split(" ") 
						if len(classes) == 2:
							modules[muid] = classes[1]
							break

		return modules

	def launch(self, muid, params, current=None):
		moduleName = self.fetch()[muid]
		dyncode = "module = __import__(muid); " + "module = module." + moduleName + "(); " + "result = module.invoke(params); "   
		exec(dyncode)
		return result
    
class Snoopymm(Ice.Application):
	def run(self, args):
		moduleManager = ModuleManager(self.communicator().getProperties().getProperty("Snoopy.ModulesDir"))
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
