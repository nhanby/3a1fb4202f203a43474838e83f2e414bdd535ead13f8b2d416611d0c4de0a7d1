![example workflow](https://img.shields.io/github/workflow/status/nhanby/3a1fb4202f203a43474838e83f2e414bdd535ead13f8b2d416611d0c4de0a7d1/work-order-service-ci-pipeline)

# SERVICE REQUEST PRIORITY QUEUING SERVICE 
This project is a implementation of a Restful queuing service, which prioritizes submitted service requests based on ranking formulas associated with each of the different service request classification types. Submitted requests are classified using the below classification rules.

|    Classification Type      |     Rule     |
| ---------------------------- | --------------------------- |
|          Normal              |  ids not evenly divisible by 3 or 5  |
|         Priority             |     ids evenly divisible by 3        |
|           VIP                |     ids evenly divisible by 5        |
|        Management            |   ids evenly divisible by 3 and 5    |

The priority queue is sorted based on different ranking formulas associated with each of the different work order classifications. Note that Management requests will be ranked ahead of all non management requests which are all ranked amongst themselves according to a function of time seconds in the queue.

|    Classification Type      |       Formula       |
| ---------------------------- | --------------------------- |
|          Normal              |       # secs in queue       |
|         Priority             |        max(3, n * ln(n))    |
|           VIP                |        max(4, 2n * ln(n))   |
|        Management            |       # secs in queue       |

## Getting Started
This application is a Springboot application packaged as a jar containing an embedded Tomcat 8 instance. For demo purposes the persistence tier has been implemented using an embedded H2 in-memory database, therefore submitted work order data will not be persisted across application restarts. 

### Requirements
* Git client
* JDK 11.0.12+
* Maven 3.0+

### How to Run 
1. Clone this repository 
2. Build the project and run the tests by running 
```mvn clean package```
3. Once successfully built, you can run the service by one of these two methods:
```
java -jar target/workorderapi-0.0.1-SNAPSHOT.jar
```
or
```
mvn spring-boot:run
```
4. Check the stdout to make sure no exceptions are thrown
5. If the application is succesfully started you should see the following output
```
2022-01-04 12:28:49.507  INFO 2209 --- [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8443 (https) with context path ''
2022-01-04 12:28:49.515  INFO 2209 --- [  restartedMain] c.a.w.WorkOrderApiApplication            : Started WorkOrderApiApplication in 8.305 seconds (JVM running for 8.639)
```

## REST APIs Endpoints
### Enqueue a service Request
```POST /api/v1/workorders/enqueue
Accept: application/json
Content-Type: application/json

{ "requestorId": "6", "timeAdded": "2021-12-29T17:03:45"} 
```

#### To view Swagger 2 API docs
Run the server and browse to localhost:8090/swagger-ui.html
