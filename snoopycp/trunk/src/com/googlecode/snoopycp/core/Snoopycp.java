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

import com.googlecode.snoopycp.controller.Coordinator;
import com.googlecode.snoopycp.core.ui.View;
import com.googlecode.snoopycp.model.Domain;

import org.apache.log4j.Logger;

public class Snoopycp extends Ice.Application {

    public static Logger logger = Logger.getLogger(Snoopycp.class);
    
    public static final int EXIT_SUCCESS = 0;
    public static final int EXIT_FAILURE = 999;

    @Override
    public int run(String[] args) {

        Domain domain = new Domain(communicator());
        View view = new View(domain);
        Coordinator coordinator = new Coordinator(domain, view);

        view.setVisible(true);

        return EXIT_SUCCESS;
    }
}
