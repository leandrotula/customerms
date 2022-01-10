FROM adoptopenjdk/openjdk11:jre-11.0.9.1_1-alpine
LABEL maintainer="leandrotula@gmail.com"
VOLUME /main-app
ADD target/customer-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","/app.jar"]