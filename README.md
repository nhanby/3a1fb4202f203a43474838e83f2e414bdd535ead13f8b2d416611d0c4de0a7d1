![example workflow](https://img.shields.io/github/workflow/status/nhanby/3a1fb4202f203a43474838e83f2e414bdd535ead13f8b2d416611d0c4de0a7d1/work-order-service-ci-pipeline)

# WORK ORDER API SERVICE 
This application is a Springboot application packaged as a jar containing an embedded Tomcat 8 instance. For demo purposes the persistence tier has been implemented using an embedded H2 in-memory database, therefore submitted work order data will not be persisted across application restarts. 

## SUMMARY
This project is a implementation of a work order queuing service which prioritizes submitted service requests based on different ranking formulas associated with the service request classification type. Submitted work orders can be of four different classification types `Normal, Priority, VIP, and Management Override` derived based on the following rules associated with the id value. The priority queue is sorted based on different ranking formulas associated with each of the different work order classifications. Management override requests will be ranked ahead of all non management override requests and are ranked amongst themselves according to the number of seconds in the queue.

|    Classification Type       |     Classification Rule     | |    Classification Type       |       Ranking Formula       |
| ---------------------------- | --------------------------- | | ---------------------------- | --------------------------- |
|         Priority             |          ids % by 3         | |          Normal              |       # secs in queue       |
|           VIP                |          ids % by 5         | |         Priority             |        max(3, n * ln(n))    |
|      Management Override     |       ids % by 3 and 5      | |           VIP                |        max(4, 2n * ln(n))   |
|          Normal              |       ids !% by 3 or 5      | |    Management Override       |       # secs in queue       |     

## Requirements
* Git
* JDK 11.0.12+
* Maven 3.0+

## How to Run 
1. Clone this repository 
2. Build the project and run the tests by running 
```mvn clean package```
3. Once successfully built, you can run the service by one of these two methods:
```
java -jar target/workorderapi-0.0.1-SNAPSHOT.jar

or

mvn spring-boot:run
```
4. Check the stdout to make sure no exceptions are thrown

### REST APIs Endpoints

#### To view Swagger 2 API docs
Run the server and browse to localhost:8090/swagger-ui.html
