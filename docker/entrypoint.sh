#!/bin/sh

case $1 in
  '' )
    cd ${PROTOTYPE_HOME} && java ${JAVA_OPTS} \
    	-jar ${PROTOTYPE_HOME}/${PROTOTYPE_ARTIFACT_CANONICAL_NAME}.jar --spring.config.location=${PROTOTYPE_HOME}/conf/
    ;;
  *)
    $1
    ;;
esac
