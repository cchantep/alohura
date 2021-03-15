#! /bin/bash

set -e

echo "[INFO] Check the source format"

sbt ++$SCALA_VERSION scalariformFormat test:scalariformFormat > /dev/null
git diff --exit-code || (cat >> /dev/stdout <<EOF
[ERROR] Scalariform check failed, see differences above.
To fix, format your sources using sbt scalariformFormat test:scalariformFormat before submitting a pull request.
Additionally, please squash your commits (eg, use git commit --amend) if you're going to update this pull request.
EOF
false
)

export _JAVA_OPTIONS="-Xmx1G"

sbt ++$SCALA_VERSION error test:compile warn testOnly scapegoat
