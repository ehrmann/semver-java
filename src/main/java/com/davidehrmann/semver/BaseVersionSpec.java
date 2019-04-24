package com.davidehrmann.semver;

abstract class BaseVersionSpec extends NodeVersionSpec {

    @Override
    public boolean isLatest() {
        return false;
    }

}
