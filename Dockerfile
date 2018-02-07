
FROM openjdk:jdk-alpine

# app and environment variables
ENV PROTOTYPE_ARTIFACT_CANONICAL_NAME  java-prototype
ENV PROTOTYPE_ARTIFACT $PROTOTYPE_ARTIFACT_CANONICAL_NAME.jar
ENV PROTOTYPE_HOME_PARENT /opt/apps/java-prototype
ENV PROTOTYPE_HOME $PROTOTYPE_HOME_PARENT/$PROTOTYPE_ARTIFACT_CANONICAL_NAME
ENV PROTOTYPE_LOG_DIR $PROTOTYPE_HOME/logs

# app setup
RUN mkdir -p $PROTOTYPE_HOME/conf
RUN mkdir -p $PROTOTYPE_HOME/agent
RUN mkdir -p $PROTOTYPE_LOG_DIR

ADD target/$PROTOTYPE_ARTIFACT_CANONICAL_NAME*.jar \
  $PROTOTYPE_HOME/$PROTOTYPE_ARTIFACT_CANONICAL_NAME.jar

WORKDIR $PROTOTYPE_HOME

# Docker daemon log rotation
ADD docker/docker-container \
    /etc/logrotate.d/docker-container
ADD docker/entrypoint.sh /entrypoint.sh
ADD Dockerfile /
EXPOSE 8080
ENTRYPOINT ["/entrypoint.sh"]
CMD [""]
