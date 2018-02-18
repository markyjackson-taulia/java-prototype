FROM alpine/git as clone
ARG url
WORKDIR /app
RUN git clone ${url}

FROM maven:3.2-jdk-8 as build
ARG project
WORKDIR /app
COPY --from=clone /app/${project} /app
RUN mvn clean package

FROM openjdk:jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar /app



# app and environment variables
ARG MYSQL_ROOT_PASSWORD
ARG MYSQL_USER
ARG JAVAPROTOTYPE_MYSQL_CONNECTION_STRING

ENV LOG_DIR $WORKDIR/logs

# app setup
RUN mkdir -p $WORKDIR/conf
# Placeholder for logging and monitoring agent
RUN mkdir -p $WORKDIR/agent
RUN mkdir -p $LOG_DIR

# Docker daemon log rotation
ADD docker/docker-container \
    /etc/logrotate.d/docker-container
EXPOSE 8080
ENTRYPOINT ["sh", "-c"]
CMD ["java ${JAVA_OPTS} -jar *.jar --spring.config.location=${WORKDIR}/conf/"]
