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

import Ice.Communicator;
import java.util.Map;

public class Domain {

    private Ice.Communicator communicator;
    private String name;

    private Ice.ObjectAdapter primaryAdapter;
    private Ice.ObjectAdapter secondaryAdapter;

    private Map<Ice.Identity, Map<String, String>> cache;
    private Map<Ice.Identity, String> sessions;

    public Domain(Communicator communicator, String name) {
        this.communicator = communicator;
        this.name = name;
    }

    public String name() {
        return name;
    }

    public Ice.Communicator communicator() {
        return communicator;
    }
}
