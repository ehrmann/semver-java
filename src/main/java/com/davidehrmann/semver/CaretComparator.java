package com.davidehrmann.semver;

import java.util.Objects;

class CaretComparator extends BaseVersionSpec {
    private final Version version;

    CaretComparator(Version version) {
        this.version = Objects.requireNonNull(version);
    }

    @Override
    public boolean isSatisfiedBy(Version ver) {
        if (this.version instanceof XRangeVersion && ((XRangeVersion) this.version).prefixLength < 3) {
            XRangeVersion xRangeVersion = (XRangeVersion) this.version;
            if (ver.getPrerelease() != null) {
                return false;
            } else if (xRangeVersion.prefixLength == 0) {
                return true;
            } else if (xRangeVersion.getMajor() == 0 && xRangeVersion.prefixLength == 2) {
                return xRangeVersion.getMajor() == ver.getMajor()
                        && xRangeVersion.getMinor() == ver.getMinor();
            } else {
                return xRangeVersion.getMajor() == ver.getMajor()
                        && xRangeVersion.getMinor() <= ver.getMinor();
            }
        } else {
            if (this.version.getPrerelease() != null && ver.getPrerelease() != null) {
                return this.version.getMajor() == ver.getMajor()
                        && this.version.getMinor() == ver.getMinor()
                        && this.version.getPatch() == ver.getPatch();
            } else if (ver.getPrerelease() != null) {
                return false;
            } else if (this.version.getMajor() == 0) {
                if (this.version.getMinor() == 0) {
                    return this.version.getMajor() == ver.getMajor()
                            && this.version.getMinor() == ver.getMinor()
                            && this.version.getPatch() == ver.getPatch();
                } else {
                    return this.version.getMajor() == ver.getMajor()
                            && this.version.getMinor() == ver.getMinor()
                            && ver.compareTo(this.version) >= 0;
                }
            } else {
                return this.version.getMajor() == ver.getMajor()
                        && ver.compareTo(this.version) >= 0;
            }
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), this.version);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        } else if (obj == this) {
            return true;
        } else {
            return version.equals(((CaretComparator) obj).version);
        }
    }

    @Override
    public String toString() {
        return "^" + version;
    }
}
