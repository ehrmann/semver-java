package com.davidehrmann.semver;

import java.util.Objects;

class XRangeComparator extends BaseVersionSpec {

    private final XRangeVersion xRangeVersion;

    XRangeComparator(XRangeVersion xRangeVersion) {
        this.xRangeVersion = Objects.requireNonNull(xRangeVersion);
    }

    @Override
    public boolean isSatisfiedBy(Version ver) {
        boolean result = true;
        switch (xRangeVersion.prefixLength) {
            case 3:
                result = ver.getPatch() == this.xRangeVersion.getPatch();
            case 2:
                result = result && ver.getMinor() == this.xRangeVersion.getMinor();
            case 1:
                result = result && ver.getMajor() == this.xRangeVersion.getMajor();
        }

        return result;
    }

    @Override
    public boolean isLatest() {
        return false;
    }

    @Override
    public String toString() {
        return xRangeVersion.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return Objects.equals(xRangeVersion, ((XRangeComparator)o).xRangeVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), xRangeVersion.hashCode());
    }
}
