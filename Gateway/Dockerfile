FROM openjdk:17-oracle
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} gateway.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/gateway.jar"]