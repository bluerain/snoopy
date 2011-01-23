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

package com.googlecode.snoopycp.core;

import Ice.Current;
import Ice.Identity;
import com.googlecode.snoopyd.driver._IDiscovererDisp;

public class DiscovererAdapter extends _IDiscovererDisp {

    private Discoverer discoverer;

    public DiscovererAdapter(Discoverer discoverer) {
        this.discoverer = discoverer;
    }

    public void discover(Identity identity, Current __current) {
        discoverer.discover(identity, __current.ctx);
    }

}
