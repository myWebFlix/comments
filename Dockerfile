FROM adoptopenjdk:14-jre-hotspot

RUN mkdir /app

WORKDIR /app

ADD ./api/target/comments-api-1.0-SNAPSHOT.jar /app

EXPOSE 8080

CMD ["java", "-jar", "comments-api-1.0-SNAPSHOT.jar"]
