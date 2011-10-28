#!/bin/sh
#
# This script starts fovast.  You could just type java -jar fovast.jar
#

# Figure out the project base.
PRG="$0"

while [ -h "$PRG" ]; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '.*/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`/"$link"
    fi
done
DIR=`dirname "$PRG"`/
FOVAST_BASE=`(cd $DIR; pwd)`
export FOVAST_BASE

# Make sure that JAVA_HOME is set.
if [ -z "${JAVA_HOME}" ]; then
    echo "JAVA_HOME environment variable must be set."
    exit
fi

cmd="${JAVA_HOME}/bin/java -Xmx256m -jar $FOVAST_BASE/fovast.jar"

#echo $cmd

exec $cmd
