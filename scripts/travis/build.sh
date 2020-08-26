#!/usr/bin/env bash
set -ev
pushd "$(dirname "${BASH_SOURCE[0]}")/../../"

source "$(dirname "${BASH_SOURCE[0]}")/build_functions.sh"

if isBranchBuild && [ "${TRAVIS_BRANCH}" = "master" ] && [ "${TRAVIS_BUILD_STAGE_NAME,,}" = "release" ] ; then
  # update ":latest" image tags on remote repositories by using the maven *internal* profile
  PROFILES="-Pinternal"
else
  # build the ":latest" image tags locally with the maven *communityDocker* profile
  PROFILES="-PcommunityDocker"
fi

mvn -B -V install -DskipTests -Dmaven.javadoc.skip=true "${PROFILES}"


