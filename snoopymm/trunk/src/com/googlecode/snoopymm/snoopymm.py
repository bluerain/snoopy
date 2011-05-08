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

class ModuleManager(object): 
    
    def __init__(self):
        pass
    
    def inokve(self, arg):
        pass

class Snoopymm(Ice.Application):
    def run(self, args):
        print "here"
        return 0
    
def main(args):
    snoopymm = Snoopymm();
    sys.exit(snoopymm.main(args[1:], args[1]))

if __name__ == "__main__": main(sys.argv)
