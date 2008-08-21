/*
 * Copyright (c) 2008, Piccolo2D project, http://piccolo2d.org
 * Copyright (c) 1998-2008, University of Maryland
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * None of the name of the University of Maryland, the name of the Piccolo2D project, or the names of its
 * contributors may be used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.umd.cs.piccolo;

import java.util.ArrayList;
import java.util.Iterator;

public class PerformanceLog {

    private ArrayList log = new ArrayList();
    private long testTime;

    public static class ZLogEntry {
        public String name;
        public long time;

        public ZLogEntry(String aName, long aTime) {
            name = aName;
            time = aTime;
        }
    }

    public void startTest() {
        Runtime.getRuntime().gc();
        testTime = System.currentTimeMillis();
    }

    public void endTest(String name) {
        testTime = System.currentTimeMillis() - testTime;
        addEntry(name, testTime);
        System.gc();
    }

    public void addEntry(String aName, long aTime) {
        log.add(new ZLogEntry(aName, aTime));
    }

    public void clear() {
        log.clear();
    }

    public void writeLog() {

        System.out.println();
        System.out.println("Test data for input into spreadsheet:");
        System.out.println();

        Iterator i = log.iterator();
        while (i.hasNext()) {
            ZLogEntry each = (ZLogEntry) i.next();
            System.out.println(each.time);
        }

        System.out.println();
        System.out.println("Labled test results, see above for simple column \n of times for input into spreadsheet:");
        System.out.println();

        i = log.iterator();
        while (i.hasNext()) {
            ZLogEntry each = (ZLogEntry) i.next();
            System.out.println(each.name + ", " + each.time);
        }
    }
}
