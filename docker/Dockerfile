#stage1
FROM alpine:3.20 AS base
ARG BASE_DIR
ARG BUILD_PATH
ARG FULL_PATH=${BASE_DIR}/${BUILD_PATH}
LABEL maintainer="Alexey N. <https://github.com/AlexLakers>"
RUN apk add git openjdk21

RUN git clone https://github.com/AlexLakers/web-node-pdm.git
WORKDIR $BASE_DIR
RUN git checkout prod
RUN ./mvnw package

#stage 2
FROM alpine:3.20 AS result
RUN apk add openjdk21
ARG BASE_DIR
ARG BUILD_PATH
ARG FULL_PATH=${BASE_DIR}/${BUILD_PATH}
WORKDIR $BASE_DIR
COPY --from=base $FULL_PATH $BASE_DIR/app.jar
COPY application-dev.yaml .
ENTRYPOINT ["java","-jar","./app.jar"]
CMD ["--spring.config.location=classpath:/application.yaml,file:application-dev.yaml"]
EXPOSE 8085




