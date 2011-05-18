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

import com.googlecode.snoopycp.util.Identities;
import java.util.Map;
import org.apache.log4j.Logger;

public class Discoverer {

    public static Logger logger = Logger.getLogger(Discoverer.class);

    private Domain domain;

    public Discoverer(Domain domain) {
        this.domain = domain;
    }

    public void discover(Ice.Identity identity, Map<String, String> context) {

        logger.debug("discoverer recieved from " + Identities.toString(identity));
        domain.cacheit(identity, context);
        
        //System.out.println("OS type: " + context.get("os"));

    }

}
