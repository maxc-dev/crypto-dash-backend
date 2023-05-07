FROM openjdk:11
EXPOSE 8000:8000
EXPOSE 9092:9092
EXPOSE 2181:2181
RUN mkdir /app
COPY ./build/libs/*-all.jar /app/ktor-docker-sample.jar
ENTRYPOINT ["java","-jar","/app/ktor-docker-sample.jar"]