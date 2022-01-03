![example workflow](https://github.com/nhanby/3a1fb4202f203a43474838e83f2e414bdd535ead13f8b2d416611d0c4de0a7d1/actions/workflows/maven.yml/badge.svg)
![example workflow](https://img.shields.io/github/workflow/status/nhanby/3a1fb4202f203a43474838e83f2e414bdd535ead13f8b2d416611d0c4de0a7d1/work-order-service-ci-pipeline)

# PRIORITY QUEUE WORK ORDER REST SERVICE
REST APIs implemented using Spring Boot Multi Module Maven Project

## How to Run

* Build the project by running `mvn clean package` inside todo-app-parent module
* Once successfully built, run the service by using the following command:
```
java -jar  todo-app-rest/target/todo-app-rest-0.0.1-SNAPSHOT.jar
```

## REST APIs Endpoints
### Create a Todo Note resource
```
POST /todo
Accept: application/json
Content-Type: application/json

{
"title" : "Spring Boot",
"description" : "Setup spring boot application",
}

```

### Update a Todo Note resource
```
PUT /todo/{Note Id}
Accept: application/json
Content-Type: application/json

{
"id" : "id"
"title" : "Spring Boot",
"description" : "Setup spring boot application",
}

```

### Retrieve a list of Todo Notes
```
Get /todo
Accept: application/json
Content-Type: application/json

```

### Find a Todo Note Resource
```
Get /todo/{Note Id}
Accept: application/json
Content-Type: application/json
```

### Delete a Todo Note Resource
```
DELETE /todo/{Note Id}
Accept: application/json
Content-Type: application/json
```

### Get information about system health, configurations, etc.
```
http://localhost:8081/health
http://localhost:8081/env
http://localhost:8081/info
http://localhost:8081/metrics
http://localhost:8081/logfile

```
### To view Swagger 2 API docs
```
Run the server and browse to localhost:8080/swagger-ui.html
```
