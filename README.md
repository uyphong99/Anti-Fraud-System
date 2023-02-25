# Spring Anti-Fraud-System

This project demonstrates (in a simplified form) the principles of anti-fraud systems in the financial sector. For this
project, we will work on a system with an expanded role model, a set of REST endpoints responsible for interacting with
users, and an internal transaction validation logic based on a set of heuristic rules.

It's based on a track by [Jetbrains Academy](https://hyperskill.org/projects/232).

## Requirements

To build and run this project you need:

- [JDK 17](https://www.openjdk.java.net/projects/jdk/17/)
- [Gradle 7.2](https://gradle.org/install/)

## How to use

1. Clone this repository

```shell
git clone https://github.com/uyphong99/Anti-Fraud-System
```

_You can use an in-memory database (like [H2](https://www.h2database.com/html/main.html)) by configuring
the `build.gradle` and `application.properties` files._

3. Build and run the project

```shell
./gradlew build
./gradlew bootRun
```


The endpoints can be accessed using a browser or a tool that allows you to send HTTP requests
like [Postman](https://www.getpostman.com/). There are several endpoints that you can use to interact with the system.
Request the according endpoint in a format shown in the examples below.

**Please note, that the first user you create will be an admin. This can only be changed in the database.**

### Processes

- [Signup new user](#signup)
- [Login as existing user](#login)
- [Delete existing user](#delete-user)
- [Get a user list](#get-user-list)
- [Update a users role](#update-user-role)
- [Update a users access level](#update-user-access)
- [Post a new transaction](#post-transaction)
- [Save a suspicious ip](#save-suspicious-ip)
- [Delete a suspicious ip](#delete-suspicious-ip)
- [Save a stolen card number](#save-stolen-card-number)
- [Delete a stolen card number](#delete-stolen-card-number)
- [Add transaction feedback](#add-transaction-feedback)
- [Get transaction history for a given card number](#get-transaction-history)

## API Endpoints

|                                               | Anonymous | MERCHANT | ADMINISTRATOR | SUPPORT |
|-----------------------------------------------|-----------|----------|---------------|---------|
| POST /api/auth/user                           | +         | +        | +             | +       |
| DELETE /api/auth/user/{username}              | -         | -        | +             | -       |
| GET /api/auth/list                            | -         | -        | +             | +       |
| PUT /api/auth/role                            | -         | -        | +             | -       |
| PUT /api/auth/access                          | -         | -        | +             | -       |
| POST /api/antifraud/transaction               | -         | +        | -             | -       |
| POST, DELETE, GET api/antifraud/suspicious-ip | -         | -        | -             | +       |
| POST, DELETE, GET api/antifraud/stolencard    | -         | -        | -             | +       |
| GET /api/antifraud/history                    | -         | -        | -             | +       |
| PUT /api/antifraud/transaction                | -         | -        | -             | +       |

_'+' means the user with the role above can access that endpoint. '-' means the user with the role above does not have
access to that endpoint._

### Region codes

| Code | Region                           |
|------|----------------------------------|
| EAP  | East Asia and Pacific            |
| ECA  | Europe and Central Asia          |
| HIC  | High-Income countries            |
| LAC  | Latin America and the Caribbean  |
| MENA | The Middle East and North Africa |
| SA   | South Asia                       |
| SSA  | Sub-Saharan Africa               |

### Examples

The following examples are using the JSON format.

#### Signup

```
POST /api/auth/user
{
   "name": "<String value, not empty>",
   "username": "<String value, not empty>",
   "password": "<String value, not empty>"
}
```

Response:

```
{
    "id": 1,
    "name": "John Doe",
    "username": "JohnDoe",
    "role": "ADMINISTRATOR"
}
```

#### Login

```
POST /api/auth/login
{
   "username": "<String value, not empty>",
   "password": "<String value, not empty>"
}
```

Response:

```
{
    "id": 1,
    "name": "John Doe",
    "username": "JohnDoe",
    "role": "ADMINISTRATOR"
}
```

_Note: This endpoint should be used to authenticate a user with http basic authentication. The response will be stored
in the browser's local storage._

#### Delete user

```
DELETE /api/auth/user/{username}
```

Response:

```
{
   "username": "JohnDoe",
   "status": "Deleted successfully!"
}
```

#### Get user list

```
GET /api/auth/list
```

Response:

```
[
    {
        "id": <user1 id>,
        "name": "<user1 name>",
        "username": "<user1 username>",
        "role": "<user1 role>"
    },
     ...
    {
        "id": <userN id>,
        "name": "<userN name>",
        "username": "<userN username>",
        "role": "<userN role>"
    }
]
```

#### Update user role

```
PUT /api/auth/role
{
   "username": "<String value, not empty>",
   "role": "<String value, not empty>"
}
```

Response:

```
{
   "id": <Long value, not empty>,
   "name": "<String value, not empty>",
   "username": "<String value, not empty>",
   "role": "<String value, not empty>"
}
```

#### Update user access

```
PUT /api/auth/access
{
   "username": "<String value, not empty>",
   "operation": "<[LOCK, UNLOCK]>"  // determines whether the user will be activated or deactivated
}
```

Response:

```
{
    "status": "User <username> <[locked, unlocked]>!"
}
```

#### Post transaction

```
POST /api/antifraud/transaction
{
  "amount": <Long>,
  "ip": "<String value, not empty>",
  "number": "<String value, not empty>",
  "region": "<String value, not empty>",
  "date": "yyyy-MM-ddTHH:mm:ss"
}
```

Response:

```
{
   "result": "ALLOWED",
   "info": "none"
}
```

#### Save suspicious IP

```
POST /api/antifraud/suspicious-ip
{
  "ip": "<String value, not empty>"
}
```

Response:

```
{
   "id": "<Long value, not empty>",
   "ip": "<String value, not empty>"
}
```

#### Delete suspicious IP

```
DELETE /api/antifraud/suspicious-ip/{ip}
```

Response:

```
{
   "status": "IP <ip address> successfully removed!"
}
```

#### Save stolen card number

```
POST /api/antifraud/stolencard
{
  "number": "<String value, not empty>"
}
```

Response:

```
{
   "id": "<Long value, not empty>",
   "number": "<String value, not empty>"
}
```

#### Delete stolen card number

```
DELETE /api/antifraud/stolencard/{number}
```

Response:

```
{
   "status": "Card <number> successfully removed!"
}
```

#### Add transaction feedback

```
PUT /api/antifraud/transaction
{
   "transactionId": <Long>,
   "feedback": "<String>"
}
```

_Feedback can be 'ALLOWED', 'MANUAL_PROCESSING' or 'PROHIBITED'_.

Response:

```
{
  "transactionId": <Long>,
  "amount": <Long>,
  "ip": "<String value, not empty>",
  "number": "<String value, not empty>",
  "region": "<String value, not empty>",
  "date": "yyyy-MM-ddTHH:mm:ss",
  "result": "<String>",
  "feedback": "<String>"
}
```

#### Get transaction history

```
GET /api/antifraud/history/{number}
```

Response:

```
[
    {
      "transactionId": <Long>,
      "amount": <Long>,
      "ip": "<String value, not empty>",
      "number": number,
      "region": "<String value, not empty>",
      "date": "yyyy-MM-ddTHH:mm:ss",
      "result": "<String>",
      "feedback": "<String>"
    },
     ...
    {
      "transactionId": <Long>,
      "amount": <Long>,
      "ip": "<String value, not empty>",
      "number": number,
      "region": "<String value, not empty>",
      "date": "yyyy-MM-ddTHH:mm:ss",
      "result": "<String>",
      "feedback": "<String>"
    }
]
```

_Note that these are just basic examples of the most common endpoints. The full list of endpoints is available in the
table above._

## Architecture

The system is built on a [Spring Framework](https://spring.io/) application context. The application itself follows the
model-view-controller pattern. The application consists of the following components:

- **Authentication**: The Authentication component is responsible for managing user authentication. It is responsible
  for validating user credentials and creating a session for the user.
- **Controller**: The Controller component is responsible for handling requests from the user.
- **Entity**: The Entity components are responsible for managing the different data models.
- **Repository**: The repository components are responsible for managing the data storage.
- **Service**: The service layer manages the main business logic.
- **Security**: The security layer is responsible for managing the access control and the authorization.
- **Util**: The utility layer contains various helper classes.

## Stack

- Java 17
- Gradle 7.5
- Spring Boot 
- H2 Database (Version 1.4.200)

## Dependencies

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Boot Web Starter ](https://spring.io/projects/spring-boot-web)
- [Spring Boot Actuator ](https://spring.io/projects/spring-boot-actuator)
- [Spring Boot Data JPA ](https://spring.io/projects/spring-boot-data-jpa)
- [Spring Boot Security](https://spring.io/projects/spring-boot-security)
- [H2 Database 1.4.200](https://www.h2database.com/)~~
- [Lombok 1.18.24](https://projectlombok.org/)
- [Spring Boot Test 2.7.0](https://spring.io/projects/spring-boot-test)
- [Spring Security Test 5.6.0](https://spring.io/projects/spring-security-test)
- [JUnit 5.8.1](https://junit.org/junit5/)
