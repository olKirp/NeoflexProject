FROM openjdk:17-oracle
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/application.jar"]