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

import com.googlecode.snoopycp.Defaults;
import com.googlecode.snoopycp.ui.View;
import com.googlecode.snoopycp.core.Domain;
import com.googlecode.snoopyd.driver.IModulerPrx;
import org.apache.log4j.Logger;


public class Coordinator {

//    public static class Refresher extends Thread {
//
//        private View view;
//
//        public Refresher(View view) {
//            this.view = view;
//        }
//
//        @Override
//        public void run() {
//
//            for (;;) {
//
//                view.update(null, null);
//
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException ex) {
//                }
//            }
//        }
//    }
    
    public static Logger logger = Logger.getLogger(Coordinator.class);

    private Domain domain;
    private View view;
    IModulerPrx moduler;

    public Coordinator(Domain domain, View view) {
        this.domain = domain;
        this.view = view;

        domain.addObserver(view);
    }

    public void launch() {

        //Refresher refresher = new Refresher(view);
        //refresher.start();

        view.setTitle("[" + domain.name() + "] " + Defaults.APP_NAME + " " + Defaults.APP_VER);
        
        view.setLocationRelativeTo(null);
        view.setVisible(true);

        synchronized(this) {
            try {
                wait();
            } catch (InterruptedException ex) {
                logger.error(ex.getMessage());
            }
        }
    }

    public void terminate() {

        synchronized(this) {
            notify();
        }
    }
    
    public void forceStart() {
        
    }
}
