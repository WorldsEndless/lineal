FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/lineal.jar /lineal/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/lineal/app.jar"]
