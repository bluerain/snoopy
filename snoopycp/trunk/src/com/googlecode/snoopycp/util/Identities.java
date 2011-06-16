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
package com.googlecode.snoopycp.util;

import Ice.Identity;

public final class Identities {

    /**
     * Give you identity with random UID like 154d2630-fafd-4bcb-9cac-dec42ec4ba9c
     * and prefix <i>domain</i>
     * @param domain category of identity
     * @return identity object
     */
    public static Identity randomIdentity(String domain) {
        return new Identity(java.util.UUID.randomUUID().toString(), domain);
    }

    /**
     * Give you full string view of identity like
     * dev/154d2630-fafd-4bcb-9cac-dec42ec4ba9c
     * @param identity object to transfer to string
     * @return string view of <i>identity</i>
     */
    public static String toString(Identity identity) {
        return identity.category + "/" + identity.name;
    }


    public static Identity stringToIdentity(String identity) {
        String args[] = identity.split("/");
        if (args.length > 1) {
            return new Identity(args[1], args[0]);
        } else {
            return new Identity(args[0], "");
        }
    }

    /**
     * Compare two identity
     * @param id1 first object to compare
     * @param id2 second object to compare
     * @return true if objects are equal or false
     */
    public static boolean equals(Identity id1, Identity id2) {
        return id1.name.equals(id2.name) && id1.category.equals(id2.category);
    }

    public static Identity xor(Identity id1, Identity id2) {
        // ex: 154d2630-fafd-4bcb-9cac-dec42ec4ba9c

        String id1Part[] = id1.name.split("-");
        String id2Part[] = id2.name.split("-");

        String resultPart[] = new String[5];

        resultPart[0] = Long.toHexString(Long.valueOf(id1Part[0], 16) ^ Long.valueOf(id2Part[0], 16));
        while (resultPart[0].length() < 8) {
            resultPart[0] = "0" + resultPart[0];
        }
        resultPart[1] = Long.toHexString(Long.valueOf(id1Part[1], 16) ^ Long.valueOf(id2Part[1], 16));
        while (resultPart[1].length() < 4) {
            resultPart[1] = "0" + resultPart[1];
        }
        resultPart[2] = Long.toHexString(Long.valueOf(id1Part[2], 16) ^ Long.valueOf(id2Part[2], 16));
        while (resultPart[2].length() < 4) {
            resultPart[2] = "0" + resultPart[2];
        }
        resultPart[3] = Long.toHexString(Long.valueOf(id1Part[3], 16) ^ Long.valueOf(id2Part[3], 16));
        while (resultPart[3].length() < 4) {
            resultPart[3] = "0" + resultPart[3];
        }
        resultPart[4] = Long.toHexString(Long.valueOf(id1Part[4], 16) ^ Long.valueOf(id2Part[4], 16));
        while (resultPart[4].length() < 12) {
            resultPart[4] = "0" + resultPart[4];
        }

        String result = resultPart[0] + "-" + resultPart[1] + "-" + resultPart[2] + "-" + resultPart[3] + "-" + resultPart[4];

        return new Identity(result, id1.category);
    }
}
