#!/bin/sh
#
# This script starts fovast.
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

SetClasspath() {
    CLASSPATH="."

    # If there are no arguments, then just include everything.
    if [ $# -eq 0 ]; then
        CLASSPATH=`echo ${LIB_DIR}/*.jar | tr ' ' ${SEP}`${SEP}${CLASSPATH}
        return
    fi
}

LIB_DIR=$FOVAST_BASE/lib
SEP=":"
SetClasspath

cmd="${JAVA_HOME}/bin/java -DFOVAST_PROJECT_BASE=${FOVAST_PROJECT_BASE} -Xmx256m -cp $CLASSPATH -jar $FOVAST_BASE/fovast.jar"

echo $cmd

#exec $cmd

