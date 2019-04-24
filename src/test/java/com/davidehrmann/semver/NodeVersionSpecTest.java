/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
 */

package com.davidehrmann.semver;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NodeVersionSpecTest {
    @Test
    public void testSimpleVersions() {
        NodeVersionSpec spec = NodeVersionSpec.of("3.2.1");

        assertTrue(spec.isSatisfiedBy(Version.of("3.2.1")));
        assertFalse(spec.isSatisfiedBy(Version.of("3.2.0")));
        assertFalse(spec.isSatisfiedBy(Version.of("3.2.2")));
        assertEquals("=3.2.1", spec.toString());

        spec = NodeVersionSpec.of("=3.2.1");

        assertTrue(spec.isSatisfiedBy(Version.of("3.2.1")));
        assertFalse(spec.isSatisfiedBy(Version.of("3.2.0")));
        assertFalse(spec.isSatisfiedBy(Version.of("3.2.2")));
        assertEquals("=3.2.1", spec.toString());
    }

    @Test
        public void testGTERanges() {
        NodeVersionSpec spec = NodeVersionSpec.of(">=1.2.7");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.7")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.8")));
        assertTrue(spec.isSatisfiedBy(Version.of("2.5.3")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.3.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.6")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.1.0")));
        assertEquals(">=1.2.7", spec.toString());
    }

    @Test
    public void testGTRanges() {
        NodeVersionSpec spec = NodeVersionSpec.of(">1.2.7");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.8")));
        assertTrue(spec.isSatisfiedBy(Version.of("2.5.3")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.3.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.6")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.1.0")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.7")));
        assertEquals(">1.2.7", spec.toString());
    }

    @Test
    public void testLTERanges() {
        NodeVersionSpec spec = NodeVersionSpec.of("<=1.2.7");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.6")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.1.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.7")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.8")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.5.3")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.3.9")));
        assertEquals("<=1.2.7", spec.toString());

        // TODO: Behavior for <=1.2.x isn't defined
    }

    @Test
    public void testLTRanges() {
        NodeVersionSpec spec = NodeVersionSpec.of("<1.2.7");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.6")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.1.0")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.7")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.8")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.5.3")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.3.9")));
        assertEquals("<1.2.7", spec.toString());
    }

    @Test
    public void testHyphenRanges() {
        NodeVersionSpec spec = NodeVersionSpec.of("1.2.3 - 1.2.5");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.3")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.4")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.5")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.2")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.6")));
        assertEquals("1.2.3 - 1.2.5", spec.toString());
    }

    @Test
    public void testHyphenXRanges() {
        NodeVersionSpec spec = NodeVersionSpec.of("1 - 2.3.4");
        assertTrue(spec.isSatisfiedBy(Version.of("1.0.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.9.9")));
        assertTrue(spec.isSatisfiedBy(Version.of("2.0.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("2.3.4")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.3.5")));
        assertFalse(spec.isSatisfiedBy(Version.of("0.0.9")));
        assertEquals("1.x.x - 2.3.4", spec.toString());


        spec = NodeVersionSpec.of("1.2 - 2.3.4");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.9.9")));
        assertTrue(spec.isSatisfiedBy(Version.of("2.0.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("2.3.4")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.3.5")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.1.9")));
        assertEquals("1.2.x - 2.3.4", spec.toString());

        spec = NodeVersionSpec.of("1.2.3 - 2.3");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.3")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.4")));
        assertTrue(spec.isSatisfiedBy(Version.of("2.3.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.2")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.4.0")));
        assertEquals(">=1.2.3 <2.4.0", spec.toString());

        spec = NodeVersionSpec.of("1.2.3 - 2");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.3")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.4")));
        assertTrue(spec.isSatisfiedBy(Version.of("2.3.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.2")));
        assertFalse(spec.isSatisfiedBy(Version.of("3.0.0")));
        assertEquals(">=1.2.3 <3.0.0", spec.toString());

        spec = NodeVersionSpec.of("1.2.x - 2.3.4");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("2.3.4")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.3.5")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.1.9")));
        assertEquals("1.2.x - 2.3.4", spec.toString());

        spec = NodeVersionSpec.of("1.2.3 - 2.x.x");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.3")));
        assertTrue(spec.isSatisfiedBy(Version.of("2.3.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.2")));
        assertFalse(spec.isSatisfiedBy(Version.of("3.0.0")));
        assertEquals(">=1.2.3 <3.0.0", spec.toString());
    }

    @Test
    public void testXRanges() {
        NodeVersionSpec spec;

        spec = NodeVersionSpec.of("*");
        assertTrue(spec.isSatisfiedBy(Version.of("0.0.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("9.9.9")));
        assertEquals("x", spec.toString());

        spec = NodeVersionSpec.of("");
        assertTrue(spec.isSatisfiedBy(Version.of("0.0.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("9.9.9")));
        assertEquals(">=0.0.0", spec.toString());

        spec = NodeVersionSpec.of("x");
        assertTrue(spec.isSatisfiedBy(Version.of("0.0.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("9.9.9")));
        assertEquals("x", spec.toString());

        spec = NodeVersionSpec.of("X");
        assertTrue(spec.isSatisfiedBy(Version.of("0.0.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("9.9.9")));
        assertEquals("x", spec.toString());

        spec = NodeVersionSpec.of("x.X.*");
        assertTrue(spec.isSatisfiedBy(Version.of("0.0.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("9.9.9")));
        assertEquals("x", spec.toString());

        spec = NodeVersionSpec.of("1.x");
        assertTrue(spec.isSatisfiedBy(Version.of("1.0.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.9.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("0.9.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.0.0")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.0.0-alpha.2")));
        assertEquals("1.x.x", spec.toString());

        spec = NodeVersionSpec.of("1.2.*");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.1.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.3.0")));
        assertEquals("1.2.x", spec.toString());

        spec = NodeVersionSpec.of("1.2.X");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.1.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.3.0")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.3.0-beta")));
        assertEquals("1.2.x", spec.toString());
    }

    @Test
    public void testTildeRanges() {
        NodeVersionSpec spec;

        spec = NodeVersionSpec.of("~1.2.3");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.3")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.2")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.3.0")));
        assertEquals("~1.2.3", spec.toString());

        spec = NodeVersionSpec.of("~1.2");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.1.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.3.0")));
        assertEquals("~1.2.x", spec.toString());

        spec = NodeVersionSpec.of("~1");
        assertTrue(spec.isSatisfiedBy(Version.of("1.0.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.9.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.0.0")));
        assertFalse(spec.isSatisfiedBy(Version.of("0.9.9")));
        assertEquals("~1.x.x", spec.toString());

        spec = NodeVersionSpec.of("~0.2.3");
        assertTrue(spec.isSatisfiedBy(Version.of("0.2.3")));
        assertTrue(spec.isSatisfiedBy(Version.of("0.2.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("0.2.2")));
        assertFalse(spec.isSatisfiedBy(Version.of("0.3.0")));
        assertEquals("~0.2.3", spec.toString());

        spec = NodeVersionSpec.of("~0.2");
        assertTrue(spec.isSatisfiedBy(Version.of("0.2.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("0.2.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("0.1.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("0.3.0")));
        assertEquals("~0.2.x", spec.toString());

        spec = NodeVersionSpec.of("~0");
        assertTrue(spec.isSatisfiedBy(Version.of("0.0.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("0.9.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.0.0")));
        assertEquals("~0.x.x", spec.toString());

        spec = NodeVersionSpec.of("~1.2.3-beta.2");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.3-beta.2")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.3-beta.4")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.3")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.2")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.3.0")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.4-beta.2")));
        assertEquals("~1.2.3-beta.2", spec.toString());
    }

    @Test
    public void testCaretRange() {
        NodeVersionSpec spec;

        spec = NodeVersionSpec.of("^1.2.3");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.3")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.9.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.2")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.0.0")));
        assertEquals("^1.2.3", spec.toString());

        spec = NodeVersionSpec.of("^0.2.3");
        assertTrue(spec.isSatisfiedBy(Version.of("0.2.3")));
        assertTrue(spec.isSatisfiedBy(Version.of("0.2.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("0.2.2")));
        assertFalse(spec.isSatisfiedBy(Version.of("0.3.0")));
        assertEquals("^0.2.3", spec.toString());

        spec = NodeVersionSpec.of("^0.0.3");
        assertTrue(spec.isSatisfiedBy(Version.of("0.0.3")));
        assertFalse(spec.isSatisfiedBy(Version.of("0.0.2")));
        assertFalse(spec.isSatisfiedBy(Version.of("0.0.4")));
        assertFalse(spec.isSatisfiedBy(Version.of("0.0.3-beta.2")));
        assertEquals("^0.0.3", spec.toString());

        spec = NodeVersionSpec.of("^1.2.3-beta.2");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.3-beta.2")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.3-beta.4")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.3")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.9.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.4-beta.4")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.2")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.0.0")));
        assertEquals("^1.2.3-beta.2", spec.toString());

        spec = NodeVersionSpec.of("^0.0.3-beta");
        assertTrue(spec.isSatisfiedBy(Version.of("0.0.3")));
        assertTrue(spec.isSatisfiedBy(Version.of("0.0.3-pr.2")));
        assertFalse(spec.isSatisfiedBy(Version.of("0.0.2")));
        assertFalse(spec.isSatisfiedBy(Version.of("0.0.4")));
        assertEquals("^0.0.3-beta", spec.toString());

        spec = NodeVersionSpec.of("^1.2.x");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.9.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.1.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.0.0")));
        assertEquals("^1.2.x", spec.toString());

        spec = NodeVersionSpec.of("^0.0.x");
        assertTrue(spec.isSatisfiedBy(Version.of("0.0.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("0.0.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.1.0")));
        assertEquals("^0.0.x", spec.toString());

        spec = NodeVersionSpec.of("^0.0");
        assertTrue(spec.isSatisfiedBy(Version.of("0.0.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("0.0.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.1.0")));
        assertEquals("^0.0.x", spec.toString());

        spec = NodeVersionSpec.of("^1.x");
        assertTrue(spec.isSatisfiedBy(Version.of("1.0.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.9.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("0.9.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.0.0")));
        assertEquals("^1.x.x", spec.toString());

        spec = NodeVersionSpec.of("^0.x");
        assertTrue(spec.isSatisfiedBy(Version.of("0.0.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("0.9.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.0.0")));
        assertEquals("^0.x.x", spec.toString());

        spec = NodeVersionSpec.of("^1.2.3-beta.2");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.3-beta.4")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.4-beta.4")));
        assertEquals("^1.2.3-beta.2", spec.toString());
    }

    @Test
    public void testIntersections() {
        NodeVersionSpec spec;

        spec = NodeVersionSpec.of(">=1.2.3 <2.3.4");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.3")));
        assertTrue(spec.isSatisfiedBy(Version.of("2.3.3")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.2")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.3.4")));
        assertEquals(">=1.2.3 <2.3.4", spec.toString());

        spec = NodeVersionSpec.of(">=1.2.1 <2.3.8 >=1.2.2 <=2.3.6");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.2")));
        assertTrue(spec.isSatisfiedBy(Version.of("2.3.6")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.1")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.3.7")));
        assertEquals(">=1.2.1 <2.3.8 1.2.2 - 2.3.6", spec.toString());

        spec = NodeVersionSpec.of("1.x 1.2.3");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.3")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.2")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.4")));
        assertEquals(">=1.x.x <2.0.0 =1.2.3", spec.toString());

        spec = NodeVersionSpec.of("x x.x x.x.x 1.x 1.2.x");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.1.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.3.0")));
        assertEquals(">=x >=0.0.0 >=x >=0.0.0 >=x >=0.0.0 >=1.x.x <2.0.0 >=1.2.x <1.3.0", spec.toString());
    }

    @Test
    public void testUnions() {
        NodeVersionSpec spec;

        spec = NodeVersionSpec.of("1.2.3 || 1.2.5");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.3")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.5")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.2")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.3.4")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.3.6")));
        assertEquals("=1.2.3 || =1.2.5", spec.toString());

        spec = NodeVersionSpec.of("1.x || 2.4.x");
        assertTrue(spec.isSatisfiedBy(Version.of("1.0.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.9.9")));
        assertTrue(spec.isSatisfiedBy(Version.of("2.4.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("2.4.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("0.9.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.0.0")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.3.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.5.0")));
        assertEquals(">=1.x.x <2.0.0 || >=2.4.x <2.5.0", spec.toString());

        spec = NodeVersionSpec.of("1.x || 2.4.x || 3.5.6 - 3.5.8 || 0.0.1");
        assertTrue(spec.isSatisfiedBy(Version.of("1.0.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.9.9")));
        assertTrue(spec.isSatisfiedBy(Version.of("2.4.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("2.4.9")));
        assertTrue(spec.isSatisfiedBy(Version.of("3.5.6")));
        assertTrue(spec.isSatisfiedBy(Version.of("3.5.8")));
        assertTrue(spec.isSatisfiedBy(Version.of("0.0.1")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.0.0")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.3.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.5.0")));
        assertFalse(spec.isSatisfiedBy(Version.of("3.5.5")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.5.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("0.0.2")));
        assertEquals(">=1.x.x <2.0.0 || >=2.4.x <2.5.0 || 3.5.6 - 3.5.8 || =0.0.1", spec.toString());
    }

    @Test
    public void testOperatorPrecedence() {
        NodeVersionSpec spec;

        spec = NodeVersionSpec.of("x 1.2.3 1.x 1.2.x 1.2.2 - 1.2.3 || >=1.2.5 <1.3 || >2.4.0 <=3 || ^4.2 || ~6.2");
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.3")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.5")));
        assertTrue(spec.isSatisfiedBy(Version.of("1.2.9")));
        assertTrue(spec.isSatisfiedBy(Version.of("2.4.1")));
        assertTrue(spec.isSatisfiedBy(Version.of("3.0.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("4.2.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("4.9.9")));
        assertTrue(spec.isSatisfiedBy(Version.of("6.2.0")));
        assertTrue(spec.isSatisfiedBy(Version.of("6.2.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.2")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.4")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.2.4")));
        assertFalse(spec.isSatisfiedBy(Version.of("1.3.0")));
        assertFalse(spec.isSatisfiedBy(Version.of("2.4.0")));
        assertFalse(spec.isSatisfiedBy(Version.of("3.0.1")));
        assertFalse(spec.isSatisfiedBy(Version.of("4.1.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("5.0.0")));
        assertFalse(spec.isSatisfiedBy(Version.of("6.1.9")));
        assertFalse(spec.isSatisfiedBy(Version.of("6.3.0")));
        assertEquals(">=x >=0.0.0 =1.2.3 >=1.x.x <2.0.0 >=1.2.x <1.3.0 1.2.2 - 1.2.3 || >=1.2.5 <1.3.x || >2.4.0 <=3.x.x || ^4.2.x || ~6.2.x", spec.toString());
    }

    @Test
    public void testEquals() {
        assertEquals(NodeVersionSpec.of("1.1.2"), NodeVersionSpec.of("1.1.2"));
    }

    @Test
    public void testLatest() {
        assertTrue(NodeVersionSpec.LATEST.isLatest());
    }
}
