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

class BinaryOperation extends BaseVersionSpec {
    private final BinaryComparatorOperator operator;
    private final NodeVersionSpec leftOperand;
    private final NodeVersionSpec rightOperand;

    BinaryOperation(NodeVersionSpec leftOperand, NodeVersionSpec rightOperand,
                    BinaryComparatorOperator operator) {
        this.leftOperand = Objects.requireNonNull(leftOperand);
        this.rightOperand = Objects.requireNonNull(rightOperand);
        this.operator = Objects.requireNonNull(operator);
    }

    @Override
    public boolean isSatisfiedBy(Version ver) {
        switch (this.operator) {
            case UNION:
                return this.leftOperand.isSatisfiedBy(ver) || this.rightOperand.isSatisfiedBy(ver);
            case INTERSECTION:
                return this.leftOperand.isSatisfiedBy(ver) && this.rightOperand.isSatisfiedBy(ver);
            default:
                throw new RuntimeException();
        }
    }

    @Override
    public String toString() {

        switch (this.operator) {
            case UNION:
                return this.leftOperand + " || " + this.rightOperand;
            case INTERSECTION:
                // Intrinsic to handle 1.2.3 - 1.6.0
                if (leftOperand instanceof Comparator
                        && rightOperand instanceof Comparator
                        && ((Comparator) leftOperand).getComparatorOperator() == Comparator.ComparatorOperator.GTE
                        && ((Comparator) rightOperand).getComparatorOperator() == Comparator.ComparatorOperator.LTE) {
                    return ((Comparator) leftOperand).getVersion() + " - " + ((Comparator) rightOperand).getVersion();
                }

                // TODO: intrinsic to handle 1.2.3 - 2.x (GTE, LT)

                // There isn't a way to enforce logical operator precedence in node-semver, so
                // grouping is never needed.
                return this.leftOperand + " " + this.rightOperand;
            default:
                throw new RuntimeException();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftOperand, rightOperand, operator);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        } else if (obj == this) {
            return true;
        } else {
            BinaryOperation binaryOperation = (BinaryOperation) obj;
            return leftOperand.equals(binaryOperation.leftOperand) &&
                    rightOperand.equals(binaryOperation.rightOperand) &&
                    operator.equals(binaryOperation.operator);
        }
    }

    protected enum BinaryComparatorOperator {
        // These are in order of precedence
        INTERSECTION,
        UNION,
    }
}
