FROM openjdk:11
EXPOSE 8000:8000
RUN mkdir /app
COPY ./build/libs/*-all.jar /app/ktor-docker-sample.jar
ENTRYPOINT ["java","-jar","/app/ktor-docker-sample.jar"]