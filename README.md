# Logger

The web application is developed using Spring boot and is built using Maven. The package `com.challenge` encloses the `Main` class which performs all
the functions owning to the size of the application. `Main` is also a `Rest Controller` since we are making an HTTP endpoint and no User interface is involved.

## Request Counter

Every minute, the application should write the total number of requests to a log file. If during the span of 1
minute, the same id is provided more than once, count it only once. This also means that the recorded count every minute is equal to the number of unique IDs 
recieved by the HTTP endpoint. 

For simplicity, we maintain a list which stores all the unique IDs recieved. When logging, the count of the array is logged.

## The scheduled job

The log is written every minute. So we can use `Scheduled` decorator to schedule a method which is triggered every x milliseconds (60,000 in this case). 
The `Main` class also needs to be marked with decorator `EnableScheduling` to implement this. 

## Calling the Endpoint

`endpoint` is an optional query parameter, upon recieving a non-null value, is used to make a GET request using `HttpURLConnection`. 
The request can fail due to multiple reasons - an error code can be returned, malformed URL, connection error. Each of these scenarios are seperately 
handled and logged. 

## The `pom.xml`

Project Object Model file for the Maven project is configured with `org.springframework.boot` dependancy and additional configuration for obtaining a
`app.jar` file which can be executed using `java -jar app.jar` command. The latest build can be found in the root of the repository. 

## Going forward

The implementation can be further optimized by implementing thread safe operations on the list, moving from the file to a database etc.
