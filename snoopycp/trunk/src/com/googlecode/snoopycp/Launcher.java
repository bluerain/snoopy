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
package com.googlecode.snoopycp;

import com.googlecode.snoopycp.core.Snoopycp;
import javax.swing.JOptionPane;

public class Launcher {

    public static void main(String args[]) {

        int status = -1;
        Snoopycp snoopycp = new Snoopycp();
        try {
            status = snoopycp.main(Defaults.APP_NAME, args,
                    System.getProperty("snoopycp.configuration", Defaults.DEFAULT_CONFIGURATION));
        } catch (Ice.FileException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        System.exit(status);
    }
}
