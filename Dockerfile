FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} eventmgr.jar
ENTRYPOINT ["java","-jar","/eventmgr.jar"]
EXPOSE 8001