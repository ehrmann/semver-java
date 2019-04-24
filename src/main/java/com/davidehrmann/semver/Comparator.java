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
