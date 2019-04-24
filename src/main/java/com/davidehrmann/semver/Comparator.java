package com.davidehrmann.semver;

import java.util.Objects;

class Comparator extends BaseVersionSpec {
    private final ComparatorOperator comparatorOperator;
    private final Version version;

    Comparator(ComparatorOperator comparatorOperator, Version version) {
        this.comparatorOperator = Objects.requireNonNull(comparatorOperator);
        this.version = Objects.requireNonNull(version);
    }

    @Override
    public boolean isSatisfiedBy(Version version) {
        if (version.getPrerelease() != null && this.version.getPrerelease() == null) {
            return false;
        }

        int diff = version.compareTo(this.version);
        switch (this.comparatorOperator) {
            case LT:
                return diff < 0;
            case LTE:
                return diff <= 0;
            case GT:
                return diff > 0;
            case GTE:
                return diff >= 0;
            case EQ:
                return diff == 0;
            default:
                throw new RuntimeException("Unrecognized Operator " + this.comparatorOperator);
        }
    }

    @Override
    public String toString() {
        switch (this.comparatorOperator) {
            case LT:
                return "<" + this.version;
            case LTE:
                return "<=" + this.version;
            case GT:
                return ">" + this.version;
            case GTE:
                return ">=" + this.version;
            case EQ:
                return "=" + this.version;
            default:
                throw new RuntimeException("Unrecognized Operator " + this.comparatorOperator);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        } else if (o == this) {
            return true;
        } else {
            Comparator comparator = (Comparator) o;
            return this.comparatorOperator.equals(comparator.comparatorOperator) && this.version.equals(comparator.version);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(comparatorOperator, version);
    }

    ComparatorOperator getComparatorOperator() {
        return comparatorOperator;
    }

    Version getVersion() {
        return version;
    }

    enum ComparatorOperator {
        LT,
        LTE,
        GT,
        GTE,
        EQ,
    }
}
