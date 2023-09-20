FROM openjdk:17-oracle
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} conveyor.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/conveyor.jar"]