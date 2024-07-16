# Spring Data REST - PUT Problem Reproducer

This project is a reproducer for a Spring Data REST issue.

## Summary of the Problem

When using Spring Data REST to manage entities with relationships, updates via PUT requests may not work as expected. Specifically, when updating a collection of related entities, the framework can mishandle the relationships. This reproducer demonstrates a workaround using a `Map` for the relation between `Movie` and `Rating`, and manually implementing methods like `setRatings` and `getRatings`. Normally, one would expect the framework to handle these relationships without such workarounds.

## Project Setup

### Entities

#### Movie

The `Movie` entity represents a film and contains the following fields:
- `id`: Unique identifier for the movie.
- `name`: The name of the movie.
- `ratings`: A collection of ratings associated with the movie, mapped using a `Map<Long, Rating>`.

#### Rating

The `Rating` entity represents a rating given to a movie on a specific platform and contains the following fields:
- `id`: Composite key consisting of `movieId` and `ratingPlatformId`.
- `movie`: Reference to the associated movie.
- `ratingPlatformId`: Identifier of the rating platform.
- `score`: The rating score given to the movie.

### Configuration

- **Expose Entity IDs**: The `exposeIdsFor` configuration is activated for both entities.
- **Single REST Repository**: Only one `RestRepository` exists for the `Movie` entity.

### Usage

The application provides REST endpoints to manage movies and their ratings. Here are some example endpoints:

**Get Movie by ID**:
```sh
GET /api/movies/{id}
```
**Example Response**:
```json
{
  "id": 2,
  "name": "The Dark Knight",
  "ratings": [
    {
      "movieId": 2,
      "ratingPlatformId": 1,
      "score": 10,
      "_links": {
        "movie": {
          "href": "http://localhost:8080/api/movies/2"
        }
      }
    }
  ],
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/movies/2"
    },
    "movie": {
      "href": "http://localhost:8080/api/movies/2"
    }
  }
}
```

**Update Movie**:
```sh
PUT /api/movies/{id}
```
**Example Request**:
```json
{
  "id": 2,
  "name": "The Dark Knight",
  "ratings": [
    {
      "movieId": 2,
      "ratingPlatformId": 1,
      "score": 10
    },
    {
      "movieId": 2,
      "ratingPlatformId": 2,
      "score": 2
    }
  ]
}
```

### Important Notes

The solution involves using a `Map` for the relation between `Movie` and `Rating`, and implementing methods like `setRatings` and `getRatings` manually. This approach is based on a solution proposed by [AresEkb](https://github.com/AresEkb), discussed [here](https://github.com/spring-projects/spring-data-rest/issues/2324).


## Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/t0bij/springdatarest-put-reproducer.git
    cd springdatarest-put-reproducer
    ```

2. Build the project:
    ```sh
    mvn clean install
    ```

3. Run the application:
    ```sh
    mvn spring-boot:run
    ```

4. Open the Swagger UI to interact with the API:
   [Swagger UI](http://localhost:8080/swagger-ui/index.html)
 
## Tests

Run the tests to ensure everything is working correctly:
```sh
mvn test
```

### TestGetMovieById

This test verifies that a movie with ID 1 can be retrieved correctly.

### TestUpdateMovieTitle

This test verifies that updating the title of a movie works as expected.

### TestChangeRatingsAndOrder

This test checks if changing the order of ratings in the request body is handled correctly and ensures that the order in the request body does not matter.

### TestDeleteAllRatings

This test ensures that all ratings of a movie can be deleted successfully.

### TestInsertNewRating

This test verifies the insertion of new ratings for an existing movie.

### TestInsertMultipleRatingsMiddle

This test adds multiple ratings and one in the middle. Initially, there is a rating for `ratingPlatformId 1`. Then a rating for `ratingPlatformId 3` is added. Finally, a rating for `ratingPlatformId 2` is added, which is in the middle.
