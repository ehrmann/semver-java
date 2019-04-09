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

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("WeakerAccess")
public class Version implements Comparable<Version> {
    static final Pattern VERSION_PATTERN = Pattern.compile(
            "v?([1-9]\\d*|0)" + "[.]" +
                    "([1-9]\\d*|0)" + "[.]" +
                    "([1-9]\\d*|0)" +
                    "(?:-((?:0|[1-9][0-9]*|[0-9A-Za-z-]*[a-zA-Z-][0-9A-Za-z-]*)(?:[.](?:0|[1-9][0-9]*|[0-9A-Za-z-]*[a-zA-Z-][0-9A-Za-z-]*))*))?" +
                    "(?:[+]((?:0|[1-9][0-9]*|[0-9A-Za-z-]*[a-zA-Z-][0-9A-Za-z-]*)(?:[.](?:0|[1-9][0-9]*|[0-9A-Za-z-]*[a-zA-Z-][0-9A-Za-z-]*))*))?"
    );
    private static final Pattern PRERELEASE_BUILD_METADATA_PATTERN = Pattern.compile("(?:0|[1-9][0-9]*|[0-9A-Za-z-]*[a-zA-Z-][0-9A-Za-z-]*)(?:[.](?:0|[1-9][0-9]*|[0-9A-Za-z-]*[a-zA-Z-][0-9A-Za-z-]*))*");
    private final int major;
    private final int minor;
    private final int patch;
    private final String prerelease;
    private final String buildMetadata;

    public Version(int major, int minor, int patch) {
        this(major, minor, patch, null, null);
    }

    public Version(int major, int minor, int patch, String prerelease, String buildMetadata) {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException("Negative version number not allowed");
        }
        if (prerelease != null && !PRERELEASE_BUILD_METADATA_PATTERN.matcher(prerelease).matches()) {
            throw new IllegalArgumentException("Failed to parse pre-release string '" + prerelease + "'");
        }
        if (buildMetadata != null && !PRERELEASE_BUILD_METADATA_PATTERN.matcher(buildMetadata).matches()) {
            throw new IllegalArgumentException("Failed to parse build metadata string '" + buildMetadata + "'");
        }

        this.major = major;
        this.minor = minor;
        this.patch = patch;

        this.buildMetadata = buildMetadata;
        this.prerelease = prerelease;
    }

    public static Version fromString(String ver) {
        Matcher matcher = VERSION_PATTERN.matcher(ver);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Failed to parse version string: " + ver);
        }

        int major = Integer.parseInt(matcher.group(1));
        int minor = Integer.parseInt(matcher.group(2));
        int patch = Integer.parseInt(matcher.group(3));
        String prerelease = matcher.group(4);
        String buildMetadata = matcher.group(5);

        return new Version(major, minor, patch, prerelease, buildMetadata);
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public String getPrerelease() {
        return prerelease;
    }

    public String getBuildMetadata() {
        return buildMetadata;
    }

    @Override
    public int compareTo(Version version) {
        int diff;

        diff = this.major - version.major;
        if (diff != 0) {
            return diff;
        }

        diff = this.minor - version.minor;
        if (diff != 0) {
            return diff;
        }

        diff = this.patch - version.patch;
        if (diff != 0) {
            return diff;
        }

        if (this.prerelease == null && version.prerelease != null) {
            return 1;
        } else if (this.prerelease != null && version.prerelease == null) {
            return -1;
        } else if (this.prerelease != null) {
            diff = this.prerelease.compareTo(version.prerelease);
            if (diff != 0) {
                return diff;
            }
        }

        if (this.buildMetadata == null && version.buildMetadata != null) {
            return -1;
        } else if (this.buildMetadata != null && version.buildMetadata == null) {
            return 1;
        } else if (this.buildMetadata != null) {
            return this.buildMetadata.compareTo(version.buildMetadata);
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        String result = major + "." + minor + "." + patch;
        if (prerelease != null) {
            result = result + "-" + prerelease;
        }
        if (buildMetadata != null) {
            result = result + "+" + buildMetadata;
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Version version = (Version) o;
        return major == version.major && minor == version.minor && patch == version.patch &&
                Objects.equals(buildMetadata, version.buildMetadata) && Objects.equals(prerelease, version.prerelease);
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch, prerelease, buildMetadata);
    }
}
