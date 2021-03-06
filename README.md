![example workflow](https://img.shields.io/github/workflow/status/nhanby/3a1fb4202f203a43474838e83f2e414bdd535ead13f8b2d416611d0c4de0a7d1/work-order-service-ci-pipeline)

# SERVICE REQUEST PRIORITY QUEUING SERVICE 
### Getting Started
This application is a Springboot application packaged as a jar containing an embedded Tomcat 8 instance. For demo purposes the persistence tier was implemented using an embedded H2 in-memory database, therefore submitted service request data won't be persisted across application restarts. The application supports only https requests running on port 8443 and configured with a self signed certificate (localhost). Depending on the test client being used this certificate will either need to be accepted on an exception basis or else added as a trusted certificate in the client/system truststore. 

### Prerequisites
* Git client
* JDK 11.0.12+
* Maven 3.0+

### How to Run 
1. Clone this repository 
```
git clone https://github.com/nhanby/3a1fb4202f203a43474838e83f2e414bdd535ead13f8b2d416611d0c4de0a7d1.git
```
2. Build the project and run the tests by running 
```
mvn clean package
```
3. Once successfully built, you can run the service by one of these two methods:
```
java -jar target/workorderapi-0.0.1-SNAPSHOT.jar
```
or
```
mvn spring-boot:run
```
4. Check the stdout output to make sure no exceptions are thrown
5. If the application is succesfully started you should see the following output
```
2022-01-04 12:28:49.507  INFO 2209 --- [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8443 (https) with context path ''
2022-01-04 12:28:49.515  INFO 2209 --- [  restartedMain] c.a.w.WorkOrderApiApplication            : Started WorkOrderApiApplication in 8.305 seconds (JVM running for 8.639)
```
6. If required, add self-signed certificate as a trusted certificate in your test client of choice (Postman, Insomnia, curl, etc)
## PROJECT SUMMARY
This project is a implementation of a restful priority queuing service, which prioritizes submitted service requests based on the different ranking formulas associated with each of the service request classification types. Submitted requests are classified according to the below classification rules.

|    Classification Type      |     Rule     |
| ---------------------------- | --------------------------- |
|          Normal              |  ids not evenly divisible by 3 or 5  |
|         Priority             |     ids evenly divisible by 3        |
|           VIP                |     ids evenly divisible by 5        |
|        Management            |   ids evenly divisible by 3 and 5    |

The priority queue is sorted based on different ranking formulas associated with each of the different work order classifications. Note that Management requests will always be prioritised ahead of all non management requests. 

|    Classification Type      |       Formula       |
| ---------------------------- | --------------------------- |
|          Normal              |       # secs in queue       |
|         Priority             |        max(3, n * ln(n))    |
|           VIP                |        max(4, 2n * ln(n))   |
|        Management            |       # secs in queue       |
## REST APIs Endpoints
### Enqueue
Endpoint for submitting a new service request onto the Queue. The format of the submitted requests timeAdded field value is of the form "yyyy-MM-ddTHH:mm:ss".
#### Sample Request
```
POST /api/v1/workorders/enqueue
Accept: application/json
Content-Type: application/json

{ "requestorId": "6", "timeAdded": "2021-12-29T17:03:45"} 
```
#### Sample Response
```
HTTP/1.1 201 
Location: https://localhost:8443/api/v1/workorders/9
Content-Type: application/json

{
   "requestorId": 6,
   "timeAdded": "2021-12-29T17:03:45",
   "type": "PRIORITY"
}
```
### Dequeue
Endpoint for dequeuing the next highest priority service request from the Queue
#### Request
```
POST /api/v1/workorders/dequeue
Accept: application/json
```
#### Response
```
HTTP/1.1 200 
Content-Type: application/json

{
   "requestorId": 9,
   "timeAdded": "2022-01-01T23:30:38.426389",
   "type": "PRIORITY",
   "rank": 3986.4260763842653
}
```
### List Ids
Endpoint which lists the ids of all queued service requests sorted in priority ranking order
#### Request
```
GET /api/v1/workorders/listids
Accept: application/json
```
#### Response
```
HTTP/1.1 200
Content-Type: application/json

{
   "workOrderIds": [15,5,10,3,6,9,12,1,2,4,7,8,11,13,14]
}
```
### Get Queue Position
Endpoint for getting the position of a service request with a given requestorId in the queue 
#### Request
```
GET /api/v1/workorders/position/{requestorId}
Accept: application/json
```
#### Response
```
HTTP/1.1 200 
Content-Type: application/json

{
   "position": 2
}
```
### Delete Service Request
Endpoint for deleting a service request with a given requestorId from the queue 
#### Request
```
DELETE /api/v1/workorders/{requestorId}
Accept: application/json
```
#### Response
```
HTTP/1.1 204
```
### Get Average Wait Time
Endpoint which calculates the average (mean) wait time that service requests have been waiting in the queue based on a given currentTime value. The format of the supplied currentTime path variable should be of the form "yyyy-MM-ddTHH:mm:ss".
#### Request
```
GET /api/v1/workorders/avgWaitTime/{currentTime}
Accept: application/json
```
#### Response
```
HTTP/1.1 200 
Content-Type: application/json

{
   "averageWaitTime": 514316
}
```
#### To view Swagger 3 API docs
Run the server and browse to https://localhost:8443/swagger-ui.html
## Known Issues
* "java.security.NoSuchAlgorithmException: Algorithm HmacPBESHA256 not available" error will be encountered on earlier versions of JDK pre v11.0.12. The SSL keystore was generated using JDK16 keytool which uses this algorithm by default. Support for this algorithm has been added to versions JDK11.0.12+. Please refer to https://bugs.openjdk.java.net/browse/JDK-8267701 for further details. 
## Future Enhancements
* Extend exception handling capabilities to override framework generated error responses to ensure that implementation details aren't accidently leaked to clients
* Add support for handling invalid time format being supplied in the averageWaitTime API request
* Add logging to capture expected client exceptions 4xx as warnings (since these are expected scenarios) and all unexpected exceptions as errors (since these will need to be triaged)
* Implement oAUTH (recommended) or Http Basic API security mechanism depending on requirements
* Externalize secrets contained within the application configuration files and remove from repo
* Implement transaction management feature within the application to ensure the consistency of data is maintained as system is scaled
* Containerize the application
* Integrate spring actuator feature to enable monitoring integration capabilities
