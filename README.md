![example workflow](https://img.shields.io/github/workflow/status/nhanby/3a1fb4202f203a43474838e83f2e414bdd535ead13f8b2d416611d0c4de0a7d1/work-order-service-ci-pipeline)

# WORK ORDER API SERVICE SUMMARY
This project is a implementation of a work order queuing service which automates the prioritization of submitted service requests. Submitted work orders can be of four different types `normal, priority, VIP, and management override` derived based on the following rules associated with the id value.

1. IDs evenly divisible by 3 are priority IDs.
2. IDs evenly divisible by 5 are VIP IDs.
3. IDs evenly divisible by both 3 and 5 are management override IDs.
4. IDs not evenly divisible by 3 or 5 are normal IDs.

The priority queue is sorted based on the ranking formulas associated with the different work order classifications.

1. Normal IDs are given a rank equal to the number of seconds they've been in the queue.
2. Priority IDs are given a rank equal to the result of applying the following formula to the number of seconds they've been in the queue:
max(3; n log n)
3. VIP IDs are given a rank equal to the result of applying the following formula to the number of seconds they've been in the queue:
max(4; 2n log n)
4. Management Override IDs are always ranked ahead of all other IDs and are ranked among themselves according to the number of seconds they've been in the queue.

This application is a springboot application which is packaged as a jar containing an embedded Tomcat 8 instance. For demo purposes the persistence tier has been implemented using an embedded H2 in-memory database, therefore submitted work order data will not be persisted across application restarts. 

## Requirements
* Git
* JDK JDK 11.0.12+
* Maven 3.0+

## How to Run 
1. Clone this repository 
2. Build the project and run the tests by running ```mvn clean package```
3. Once successfully built, you can run the service by one of these two methods:
```
   	    java -jar target/workorderapi-0.0.1-SNAPSHOT.jar
or
        mvn spring-boot:run
```
4. Check the stdout or boot_example.log file to make sure no exceptions are thrown

### REST APIs Endpoints

#### To view Swagger 2 API docs
Run the server and browse to localhost:8090/swagger-ui.html
