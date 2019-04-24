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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class NodeVersionSpec {

    private static final Pattern PATCH_X_RANGE_PATTERN = Pattern.compile(
            "([1-9]\\d*|0)" +
                    "[.]" + "([1-9]\\d*|0)" +
                    "(?:[.][xX*])?"
    );

    private static final Pattern MINOR_X_RANGE_PATTERN = Pattern.compile(
            "([1-9]\\d*|0)" +
                    "(?:[.][xX*])?" +
                    "(?:[.][xX*])?"
    );
    private static final Pattern MAJOR_X_RANGE_PATTERN = Pattern.compile("[xX*]" + "(?:[.][xX*])?" + "(?:[.][xX*])?");
    private static final Pattern BINARY_OPERATOR_PATTERN = Pattern.compile("\\s*([|]{2}|\\s+)\\s*");
    private static final Pattern HYPHEN_RANGE_PATTERN = Pattern.compile("\\s*-\\s*");
    private static final Pattern OPERATOR_PATTERN = Pattern.compile("([<>]=?|=|[~^])\\s*");

    @SuppressWarnings("WeakerAccess")
    public static NodeVersionSpec LATEST = new NodeVersionSpec() {
        @Override
        public boolean isSatisfiedBy(Version ver) {
            return false;
        }

        @Override
        public boolean isLatest() {
            return true;
        }

    };

    @SuppressWarnings("WeakerAccess")
    public static NodeVersionSpec of(String versionSpec) {
        if (versionSpec.isEmpty() || versionSpec.trim().isEmpty()) {
            return new Comparator(Comparator.ComparatorOperator.GTE, new Version(0, 0, 0));
        } else if ("latest".equalsIgnoreCase(versionSpec.trim())) {
            return LATEST;
        }

        List<Object> tokens = new ArrayList<>();

        Matcher matcher = OPERATOR_PATTERN.matcher(versionSpec).useAnchoringBounds(true);

        int end = versionSpec.length();
        int start = 0;
        while (start < end) {
            if (matcher.usePattern(OPERATOR_PATTERN).find(start) && matcher.start() == start) {
                switch (matcher.group(1)) {
                    case ">":
                        tokens.add(Comparator.ComparatorOperator.GT);
                        break;
                    case ">=":
                        tokens.add(Comparator.ComparatorOperator.GTE);
                        break;
                    case "<":
                        tokens.add(Comparator.ComparatorOperator.LT);
                        break;
                    case "<=":
                        tokens.add(Comparator.ComparatorOperator.LTE);
                        break;
                    case "=":
                        tokens.add(Comparator.ComparatorOperator.EQ);
                        break;
                    case "~":
                        tokens.add(UnaryRange.TILDE);
                        break;
                    case "^":
                        tokens.add(UnaryRange.CARET);
                        break;
                    default:
                        throw new RuntimeException("Internal bug");
                }
            } else if (matcher.usePattern(Version.VERSION_PATTERN).find(start) && matcher.start() == start) {
                tokens.add(Version.of(matcher.group()));
            } else if (matcher.usePattern(PATCH_X_RANGE_PATTERN).find(start) && matcher.start() == start) {
                int majorVersion = Integer.parseInt(matcher.group(1));
                int minorVersion = Integer.parseInt(matcher.group(2));
                tokens.add(new XRangeVersion(majorVersion, minorVersion));
            } else if (matcher.usePattern(MINOR_X_RANGE_PATTERN).find(start) && matcher.start() == start) {
                int majorVersion = Integer.parseInt(matcher.group(1));
                tokens.add(new XRangeVersion(majorVersion));
            } else if (matcher.usePattern(MAJOR_X_RANGE_PATTERN).find(start) && matcher.start() == start) {
                tokens.add(new XRangeVersion());
            } else if (matcher.usePattern(HYPHEN_RANGE_PATTERN).find(start) && matcher.start() == start) {
                // TODO: make this more elegant?
                tokens.add("-");
            } else if (matcher.usePattern(BINARY_OPERATOR_PATTERN).find(start) && matcher.start() == start) {
                if (matcher.group(1).equals("||")) {
                    tokens.add(BinaryOperation.BinaryComparatorOperator.UNION);
                } else {
                    tokens.add(BinaryOperation.BinaryComparatorOperator.INTERSECTION);
                }
            } else {
                throw new IllegalArgumentException("Failed parse version spec '" + versionSpec + "' at char " + start + " : unexpected token");
            }

            start = matcher.end();
        }

        // Bind unary operators
        for (int i = 0; i < tokens.size() - 1; i++) {
            Object o = tokens.get(i);
            if (o instanceof UnaryRange || o instanceof Comparator.ComparatorOperator) {
                Object next = tokens.get(i + 1);
                if (!(next instanceof Version)) {
                    throw new IllegalArgumentException("Version expected after " + o);
                }

                if (o instanceof UnaryRange) {
                    switch ((UnaryRange) o) {
                        case TILDE:
                            tokens.set(i, new TildeComparator((Version) next));
                            break;
                        case CARET:
                            tokens.set(i, new CaretComparator((Version) next));
                            break;
                        default:
                            throw new RuntimeException("Unsupported UnaryRange: " + o);
                    }
                } else {
                    if (Comparator.ComparatorOperator.EQ.equals(o)) {
                        tokens.set(i, next);
                    } else {
                        tokens.set(i, new Comparator((Comparator.ComparatorOperator) o, (Version) next));
                    }
                }

                tokens.set(i + 1, null);
                i += 1;
            }
        }

        Object last = tokens.isEmpty() ? null : tokens.get(tokens.size() - 1);
        if (last instanceof UnaryRange || last instanceof Comparator.ComparatorOperator) {
            throw new IllegalArgumentException("Version expected after " + last);
        }

        tokens.removeAll(Collections.singleton(null));

        // Bind range operators
        for (int i = 1; i < tokens.size() - 1; i++) {
            if ("-".equals(tokens.get(i))) {
                Object left = tokens.get(i - 1);
                Object right = tokens.get(i + 1);
                if (!(left instanceof Version)) {
                    throw new IllegalArgumentException("Expected xRangeVersion, got '" + left + "'");
                } else if (!(right instanceof Version)) {
                    throw new IllegalArgumentException("Expected xRangeVersion, got '" + right + "'");
                } else {
                    NodeVersionSpec rightVersionSpec;

                    // Since ranges are inclusive, an upper bound of 1.2 is more like <1.3.0.  Handle this.
                    if (right instanceof XRangeVersion) {
                        XRangeVersion rightVersion = (XRangeVersion) right;
                        if (rightVersion.prefixLength == 1) {
                            rightVersionSpec = new Comparator(Comparator.ComparatorOperator.LT, new Version(rightVersion.getMajor() + 1, 0, 0));
                        } else if (rightVersion.prefixLength == 2) {
                            rightVersionSpec = new Comparator(Comparator.ComparatorOperator.LT, new Version(rightVersion.getMajor(), rightVersion.getMinor() + 1, 0));
                        } else if (rightVersion.prefixLength == 3) {
                            rightVersionSpec = new Comparator(Comparator.ComparatorOperator.LT, new Version(rightVersion.getMajor(), rightVersion.getMinor(), rightVersion.getPatch() + 1));
                        } else {
                            rightVersionSpec = new Comparator(Comparator.ComparatorOperator.GTE, new Version(0, 0, 0));
                        }
                    } else {
                        rightVersionSpec = new Comparator(Comparator.ComparatorOperator.LTE, (Version) right);
                    }

                    tokens.set(i - 1, null);
                    tokens.set(i + 1, null);
                    tokens.set(i,
                            new BinaryOperation(
                                    new Comparator(Comparator.ComparatorOperator.GTE, (Version) left),
                                    rightVersionSpec,
                                    BinaryOperation.BinaryComparatorOperator.INTERSECTION));
                    i += 2;
                }
            }
        }

        tokens.removeAll(Collections.singleton(null));

        if (tokens.contains("-")) {
            throw new IllegalArgumentException("Unmatched range");
        }

        // Bind intersections
        for (BinaryOperation.BinaryComparatorOperator operator :
                BinaryOperation.BinaryComparatorOperator.values()) {
            boolean operatorFound;
            do {
                operatorFound = false;
                for (int i = 1; i < tokens.size() - 1; i++) {
                    if (operator.equals(tokens.get(i))) {
                        Object left = tokens.get(i - 1);
                        if (left instanceof XRangeVersion) {
                            XRangeVersion xRangeVersion = (XRangeVersion) left;
                            NodeVersionSpec upperVersionSpec;
                            if (xRangeVersion.prefixLength == 1) {
                                upperVersionSpec = new Comparator(Comparator.ComparatorOperator.LT, new Version(xRangeVersion.getMajor() + 1, 0, 0));
                            } else if (xRangeVersion.prefixLength == 2) {
                                upperVersionSpec = new Comparator(Comparator.ComparatorOperator.LT, new Version(xRangeVersion.getMajor(), xRangeVersion.getMinor() + 1, 0));
                            } else if (xRangeVersion.prefixLength == 3) {
                                upperVersionSpec = new Comparator(Comparator.ComparatorOperator.LT, new Version(xRangeVersion.getMajor(), xRangeVersion.getMinor(), xRangeVersion.getPatch() + 1));
                            } else {
                                upperVersionSpec = new Comparator(Comparator.ComparatorOperator.GTE, new Version(0, 0, 0));
                            }

                            left = new BinaryOperation(new Comparator(Comparator.ComparatorOperator.GTE, xRangeVersion), upperVersionSpec, BinaryOperation.BinaryComparatorOperator.INTERSECTION);
                        } else if (left instanceof Version) {
                            left = new Comparator(Comparator.ComparatorOperator.EQ, (Version) left);
                        } else if (!(left instanceof NodeVersionSpec)) {
                            throw new IllegalArgumentException("Unexpected token before " + operator + ": '" + left + "'");
                        }

                        Object right = tokens.get(i + 1);
                        if (right instanceof XRangeVersion) {
                            XRangeVersion xRangeVersion = (XRangeVersion) right;
                            NodeVersionSpec upperVersionSpec;
                            if (xRangeVersion.prefixLength == 1) {
                                upperVersionSpec = new Comparator(Comparator.ComparatorOperator.LT, new Version(xRangeVersion.getMajor() + 1, 0, 0));
                            } else if (xRangeVersion.prefixLength == 2) {
                                upperVersionSpec = new Comparator(Comparator.ComparatorOperator.LT, new Version(xRangeVersion.getMajor(), xRangeVersion.getMinor() + 1, 0));
                            } else if (xRangeVersion.prefixLength == 3) {
                                upperVersionSpec = new Comparator(Comparator.ComparatorOperator.LT, new Version(xRangeVersion.getMajor(), xRangeVersion.getMinor(), xRangeVersion.getPatch() + 1));
                            } else {
                                upperVersionSpec = new Comparator(Comparator.ComparatorOperator.GTE, new Version(0, 0, 0));
                            }

                            right = new BinaryOperation(new Comparator(Comparator.ComparatorOperator.GTE, xRangeVersion), upperVersionSpec, BinaryOperation.BinaryComparatorOperator.INTERSECTION);
                        } else if (right instanceof Version) {
                            right = new Comparator(Comparator.ComparatorOperator.EQ, (Version) right);
                        } else if (!(right instanceof NodeVersionSpec)) {
                            throw new IllegalArgumentException("Unexpected token after " + operator + ": '" + right + "'");
                        }

                        tokens.set(i - 1, null);
                        tokens.set(i + 1, null);
                        tokens.set(i, new BinaryOperation((NodeVersionSpec) left, (NodeVersionSpec) right, operator));

                        operatorFound = true;
                        i += 2;
                    }
                }

                tokens.removeAll(Collections.singleton(null));
            } while (operatorFound);
        }

        if (tokens.size() != 1) {
            throw new IllegalArgumentException("Oops");
        } else if (tokens.get(0) instanceof XRangeVersion) {
            return new XRangeComparator((XRangeVersion) tokens.get(0));
        } else if (tokens.get(0) instanceof Version) {
            return new Comparator(Comparator.ComparatorOperator.EQ, (Version) tokens.get(0));
        } else if (!(tokens.get(0) instanceof NodeVersionSpec)) {
            throw new IllegalArgumentException("Oops");
        } else {
            return (NodeVersionSpec) tokens.get(0);
        }
    }

    NodeVersionSpec() {

    }

    public abstract boolean isSatisfiedBy(Version ver);

    public abstract boolean isLatest();

    protected enum UnaryRange {
        TILDE,
        CARET,
    }

}
