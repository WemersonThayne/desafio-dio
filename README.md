## Basic Spring CRUD Project with TestContainer, PostgreSQL, Swagger, Flyway, and Docker

___ 

This project demonstrates the creation of a RESTful API using the Spring Boot framework to perform basic CRUD operations. It utilizes PostgreSQL as the database, TestContainers for integration testing, Swagger for API documentation, Flyway for database version control, and Docker + Docker Compose for easy management and deployment of the application.

### Prerequisites

Make sure you have the following tools installed on your machine:
- Java JDK 17 or higher
- Maven
- Docker
- Docker Compose

___ 

### Database Configuration

This project uses PostgreSQL as the database. Make sure you have Docker installed and running on your machine. Execute the following command to start PostgreSQL using Docker Compose:

```
docker-compose up -d 
```
This will start a PostgreSQL container using the configurations defined in the docker-compose.yml file.


### Running the Project

To run the project, follow these steps:

- Clone this repository to your local machine. 
- Navigate to the project's root directory. 
- Compile the project using Maven:

```
mvn clean install 
```

Run the generated JAR:

```
java -jar target/spring-crud-demo.jar
```

This will start the Spring Boot application.

### Integration Testing e Testing Unit

Integration tests are performed using TestContainers, which provides temporary Docker containers during test execution. To run the integration tests, execute the following command:

```
mvn test
```

This ensures that the application communicates correctly with the PostgreSQL database.


### API Documentation with Swagger

API documentation is automatically generated using Swagger. After starting the application, you can access the API documentation at:

```
http://localhost:8080/swagger-ui.html
```

### Database Version Control with Flyway

Flyway is used to control the database version and manage migrations. SQL migrations are placed in the src/main/resources/db/migration directory. Flyway will automatically apply these migrations during the application startup.

### Dockerizing the Application

The application can be easily dockerized using Docker. A Dockerfile is provided in the project's root directory to package the application as a Docker image. To build the Docker image, execute the following command:

```
docker build -t spring-crud-demo .
```
After building the image, you can run the application as a Docker container:

```
docker run -p 8080:8080 spring-crud-demo
```

This will start the Spring Boot application within a Docker container.


## Contributing

Feel free to contribute improvements to this project. Fork this repository, make your changes, and submit a pull request. We are open to suggestions and bug fixes.

This project is provided under the MIT License.

### Testcontainers support

This project uses [Testcontainers at development time](https://docs.spring.io/spring-boot/docs/3.2.2/reference/html/features.html#features.testing.testcontainers.at-development-time).

Testcontainers has been configured to use the following Docker images:


Please review the tags of the used images and set them to the same as you're running in production.

