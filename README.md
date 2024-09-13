# Book Service API

## Description
Book Service API is a RESTful API for managing a collection of books. It supports basic CRUD operations (Create, Read, Update, Delete), as well as filtering books by author, title, and genre. The application is built with Java, using Spring Boot, Spring Data JPA, and an H2 in-memory database.

## Features
- **Get Book by ID**: Retrieves a book's details by its ID.
- **Create a New Book**: Allows adding a new book to the collection.
- **Update Book Information**: Updates the details of an existing book by its ID.
- **Delete Book**: Removes a book from the collection using its ID.
- **Get All Books**: Returns a paginated list of all books.
- **Search Books by Filter**: Filters books by author, title, or genre.

## Technologies
- **Java 17**
- **Spring Boot**
- **Spring Data JPA**
- **H2 Database**
- **Lombok**
- **MapStruct**
- **JUnit 5** for testing

## Installation and Running the Application

1. **Clone the repository:**
  
   git clone https://github.com/your-username/book-service-api.git
   
   mvn clean install

## API Endpoints

 **Get Book by ID** 
 
 GET api/v1/books/{id}

 Response: Returns the book with the specified ID.

 ##

 **Create a New Book**

 POST api/v1/books
 ```
 {
  "title": "The Great Gatsby",
  "author": "F. Scott Fitzgerald",
  "genre": "Fiction",
  "publicationYear": "1925-04-10",
  "ISBN": "2222222222222"
}
```
Response: Returns the created book along with the location URL.

##

**Update Book by ID**

PUT api/v1/books/{id}

 ```
 {
  "title": "Test Task",
  "author": "Serhii Vashkulat",
  "genre": "Java",
  "publicationYear": "2000-04-10",
  "ISBN": "1111111111111"
}
```

Response: Returns the updated book.

##

**Delete Book by ID**

DELETE api/v1/books/{id}

Response: No content on success (204).

##

**Get All Books (Paginated)**

GET api/v1/books?page=0&size=10

Response: Returns a paginated list of books.

##

**Search Books by Filter**

GET api/v1/books/filter?author={author}&title={title}&genre={genre}

Response: Returns a list of books matching the specified filters.

