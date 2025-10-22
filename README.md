String Analyzer API

The String Analyzer API is a lightweight and efficient RESTful service built with Java and Spring Boot.
It analyzes text strings by computing insightful metrics such as length, word count, unique characters, character frequency, palindrome detection, and SHA-256 hashing.
This project demonstrates clean API design, efficient data handling, and modern backend best practices.

Overview

This API provides developers with an easy way to analyze and store string data.
Each input string is processed to extract key properties and then stored securely using a hash-based unique identifier.
Users can retrieve, filter, and query analyzed strings based on attributes such as length, word count, or palindrome status.

The design prioritizes scalability, simplicity, and fast response times.

Key Features

 Analyze strings and generate detailed statistics

 Automatically detect palindromic strings

 Count unique characters and compute character frequencies

 Generate a SHA-256 hash for secure identification

 Retrieve and filter stored results using query parameters and natural language queries

 Includes an in-memory H2 database for quick testing and development

 Technologies Used                               Description                     
 Java 17                                         Core programming language   
 Spring Boot 3.x                                 Framework for rapid API development
 H2 Database                                     Lightweight in-memory database
 Lombok                                          Reduces Java boilerplate code
 Apache Commons Codec                            Provides hashing utilities (SHA-256)
 Maven                                           Dependency management and build automation


 Setup & Local Execution
 These instructions guide you through setting up and running the service locally.

 Prerequisites

  Java 17 or higher

  Maven (or use the included Maven Wrapper, ./mvnw)

  Running Locally
    Clone the repository and navigate to the root directory.
     git clone [https://github.com/Kelvinjaston/stringAPI.git](https://github.com/Kelvinjaston/stringAPI.git)
     cd stringAPI

Clean and Build the executable JAR:
  We use clean package to ensure a robust, single-file deployment artifact.
    ./mvnw clean package

The API will start on: http://localhost:8080

 API Endpoints
The base URL for the API is http://localhost:8080/strings

 Create/Analyze String

 Method

Path

Description

Success

Errors

POST

/strings

Analyze a new string. Requires JSON body: {"value": "..."}.

201 Created

409 Conflict, 422 Unprocessable Entity, 400 Bad Request

2. Get All Strings with Filtering

Method

Path

Description

Success

Errors

GET

/strings

Retrieve all strings with optional query filters (e.g., ?is_palindrome=true&min_length=10).

200 OK

400 Bad Request

3. Natural Language Filtering

Method

Path

Description

Success

Errors

GET

/strings/filter-by-natural-language?query={...}

Filters based on a natural language query.

200 OK

400 Bad Request, 422 Unprocessable Entity

4. Get Specific String

Method

Path

Description

Success

Errors

GET

/strings/{string_value}

Retrieve details by the string's raw value.

200 OK

404 Not Found

5. Delete String

Method

Path

Description

Success

Errors

DELETE

/strings/{string_value}

Delete a stored string by its value.

204 No Content

404 Not Found


Example Response

This is the standard output format for successful POST and GET requests:

{
  "id": "5bc23a1d...",
  "value": "madam",
  "properties": {
    "length": 5,
    "is_palindrome": true,
    "unique_characters": 3,
    "word_count": 1,
    "sha256_hash": "5bc23a1d1b6e...",
    "character_frequency_map": {
      "m": 2,
      "a": 2,
      "d": 1
    }
  },
  "created_at": "2025-10-22T10:15:30Z"
}


 
 


 


      


 



 
