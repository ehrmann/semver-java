package com.davidehrmann.semver;

import java.util.Objects;

class XRangeVersion extends Version {
    final int prefixLength;

    XRangeVersion() {
        super(0, 0, 0);
        this.prefixLength = 0;
    }

    XRangeVersion(int major) {
        super(major, 0, 0);
        this.prefixLength = 1;
    }

    XRangeVersion(int major, int minor) {
        super(major, minor, 0);
        this.prefixLength = 2;
    }

    public XRangeVersion(int major, int minor, int patch) {
        super(major, minor, patch);
        this.prefixLength = 3;
    }

    @Override
    public String toString() {
        if (prefixLength == 0) {
            return "x";
        } else if (prefixLength == 1) {
            return getMajor() + ".x.x";
        } else if (prefixLength == 2) {
            return getMajor() + "." + getMinor() + ".x";
        } else if (prefixLength == 3) {
            return getMajor() + "." + getMinor() + "." + getPatch();
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && ((XRangeVersion) o).prefixLength == prefixLength;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), prefixLength);
    }
}
