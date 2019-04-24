# semver-java
Java implementation of [Semantic Versioning](https://semver.org/) and
[Node Semantic Version specifiers](https://github.com/npm/node-semver)

## Use
### Versions

    new Version(1, 0, 1);
    new Version(1, 0, 1, "beta3", null);
    Version.of("1.0.1");

    Stream.of("1.0.1", "1.0.0-beta.2", "1.0.0-beta.1")
        .map(Version::of)
        .sorted()
        .collect(Collectors.toList()); // 1.0.0-beta.1, 1.0.0-beta.2, 1.0.1

### NodeVersionSpecs

    NodeVersionSpec.of(">=1.0").isSatisfiedBy(Version.of("1.5.0") // true
    NodeVersionSpec.of("latest").isSatisfiedBy(Version.of("1.5.0") // false
    NodeVersionSpec.of("latest").isLatest() // true
    NodeVersionSpec.of("*").isSatisfiedBy(Version.of("1.5.0") // true
    NodeVersionSpec.of(">=1.5 <2.0.0").isSatisfiedBy(Version.of("1.6.0") // true
