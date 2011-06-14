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
package com.googlecode.snoopycp.controller;

import com.googlecode.listener.*;
import com.googlecode.snoopycp.Defaults;
import com.googlecode.snoopycp.ui.MainFrame;
import com.googlecode.snoopycp.core.Domain;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public class Coordinator {

    public static Logger logger = Logger.getLogger(Coordinator.class);
    private Domain domain;
    private MainFrame view;
    

    public Coordinator(Domain _domain, MainFrame view) {
        this.domain = _domain;
        this.view = view;
        this.view.setActionsOnPopup(this.packActions());
        domain.addObserver(view);
        view.setCoordinator(this);
    }

    public Domain domain() {
        return this.domain;
    }

    private Map<String, ActionListener> packActions() {
        Map<String, ActionListener> actions = new HashMap<String, ActionListener>();
        actions.put("NodeProperties", new NodePropertiesAL(view, domain));
        actions.put("ForceStart", new NodeForceStartAL(view, domain));
        actions.put("Shutdown", new NodeShutdownAL(view, domain));
        actions.put("ModuleProperties", new ModulePropertiesAL(view, domain));
        actions.put("HostResults", new HostResultsAL(view, this, logger));
        actions.put("ModuleResults", new ModuleResultsAL(view, this, logger));
        return actions;
    }

    public void launch() {

        view.setTitle("[" + domain.name() + "] " + Defaults.APP_NAME + " " + Defaults.APP_VER);

        view.setLocationRelativeTo(null);
        view.setVisible(true);

        synchronized (this) {
            try {
                // FIXME what this for?
                wait();
            } catch (InterruptedException ex) {
                logger.error(ex.getMessage());
            }
        }
    }

    public void terminate() {

        synchronized (this) {
            notify();
        }
    }

    

    
}
