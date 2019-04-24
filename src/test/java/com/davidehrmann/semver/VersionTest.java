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
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(Enclosed.class)
public class VersionTest {

    public static class ParsingTest {
        @Test
        public void testVersionParsing() {
            assertEquals(new Version(1, 2, 3), Version.of("1.2.3"));
            assertEquals(new Version(1, 2, 3), Version.of("v1.2.3"));
            assertEquals(new Version(1, 2, 3, "beta-9", null), Version.of("1.2.3-beta-9"));
            assertEquals(new Version(1, 2, 3, null, "sha1-deadbeef"), Version.of("1.2.3+sha1-deadbeef"));
            assertEquals(new Version(1, 2, 3, "1.3-beta-9", "sha1-deadbeef"), Version.of("1.2.3-1.3-beta-9+sha1-deadbeef"));

            assertEquals(new Version(1, 2, 3, "resolveDependencies.bar", "baz.quz"), Version.of("1.2.3-resolveDependencies.bar+baz.quz"));
            assertEquals(new Version(1, 2, 3, "resolveDependencies.bar.baz", "qux.quux.corge"), Version.of("1.2.3-resolveDependencies.bar.baz+qux.quux.corge"));

            assertEquals(new Version(1, 2, 3, "123.aZ0-.1-2", "456.bY1-.3-4"), Version.of("1.2.3-123.aZ0-.1-2+456.bY1-.3-4"));

            assertEquals(new Version(2, 0, 0, "rc.0", null), Version.of("2.0.0-rc.0"));
        }
    }

    public static class CompareToTest {
        @Test
        public void testComparisons() {
            assertEquals(0, Version.of("1.2.3").compareTo(Version.of("1.2.3")));
            assertThat(Version.of("1.2.3"), greaterThan(Version.of("1.2.2")));
            assertThat(Version.of("1.2.2"), lessThan(Version.of("1.2.3")));

            assertThat(Version.of("1.3.3"), greaterThan(Version.of("1.2.3")));
            assertThat(Version.of("1.2.3"), lessThan(Version.of("1.3.3")));

            assertThat(Version.of("2.0.0"), greaterThan(Version.of("1.2.3")));
            assertThat(Version.of("1.2.3"), lessThan(Version.of("2.0.0")));

            assertThat(Version.of("1.2.3"), greaterThan(Version.of("1.2.3-beta1")));
            assertThat(Version.of("1.2.3-beta2"), greaterThan(Version.of("1.2.3-beta1")));
            assertThat(Version.of("1.2.3-beta1"), lessThan(Version.of("1.2.3")));
            assertThat(Version.of("1.2.3-beta1"), lessThan(Version.of("1.2.3-beta2")));

            List<Version> expectedPrereleaseSort = Arrays.asList(
                    Version.of("1.0.0-alpha"),
                    Version.of("1.0.0-alpha.1"),
                    Version.of("1.0.0-alpha.beta"),
                    Version.of("1.0.0-beta"),
                    Version.of("1.0.0-beta.2"),
                    Version.of("1.0.0-beta.11"),
                    Version.of("1.0.0-rc.1"),
                    Version.of("1.0.0"));

            List<Version> prereleases = new ArrayList<>(expectedPrereleaseSort);
            Collections.reverse(prereleases);
            Collections.sort(prereleases);

            assertEquals(expectedPrereleaseSort, prereleases);
        }
    }

    @RunWith(Parameterized.class)
    public static class BadPrereleaseTest {
        @Parameterized.Parameters(name = "prerelease = {0}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][] {
                    {"+"}, {"$"}, {"007"}
            });
        }

        @Parameterized.Parameter
        public String prerelease;

        @Test(expected = IllegalArgumentException.class)
        public void testBadPrerelease() {
            new Version(1, 2, 3, prerelease, null);
        }
    }
}
