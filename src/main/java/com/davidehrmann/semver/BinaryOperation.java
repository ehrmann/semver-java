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
