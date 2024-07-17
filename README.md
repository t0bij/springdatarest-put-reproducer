# Spring Data REST - PUT Problem Reproducer

## Summary of the Problem

The main entity `Movie` has a `OneToMany` relation to `Ratings`. `Ratings` has a composite primary key with `movieID` and `ratingPlatformId`. Only the `Movie` entity has a REST repository, and IDs are exposed. A single movie API exists where GET works fine, but updating via PUT doesn't. Updating the ratings list gives unexpected results. For example:

**Before:**
```json
{
   "id": 2,
   "name": "The Dark Knight",
   "ratings": [
      {
         "ratingPlatformId": 1,
         "score": 10
      }
   ]
}
```

**PUT Request:**
```json
{
   "id": 2,
   "name": "The Dark Knight",
   "ratings": [
      {
         "ratingPlatformId": 2,
         "score": 2
      }
   ]
}
```

**Expectation:**
The rating for ratingPlatformId 1 should be deleted, and a new one with ratingPlatformId 2 should be created.

**Actual Result:**
```json
{
   "id": 2,
   "name": "The Dark Knight",
   "ratings": [
      {
         "ratingPlatformId": 1,
         "score": 2
      }
   ]
}
```

I debugged a bit and ratingPlatformId is not considered at all during the PUT operation and gets skipped here as it is an ID:

https://github.com/spring-projects/spring-data-rest/blob/aaadc344ab1bef4ed98cb0dbf6ca8ebd7c9262ff/spring-data-rest-webmvc/src/main/java/org/springframework/data/rest/webmvc/json/DomainObjectReader.java#L688-L690

The only working setup is inspired by [AresEkb](https://github.com/AresEkb) from [this issue](https://github.com/spring-projects/spring-data-rest/issues/2324) and involves:

- Using a `HashMap` for the `OneToMany` ratings and writing `getRatings` and `setRatings` methods manually.
  https://github.com/t0bij/springdatarest-put-reproducer/blob/23b7ddcfe0545daa7496b54f979a6de048ad8df4/src/main/java/com/t0bij/springdatarest/putreproducer/model/Movie.java#L28-L41

- Adding some AOP magic for `RepositoryEntityController.putItemResource` to set the movie in the ratings.
  https://github.com/t0bij/springdatarest-put-reproducer/blob/23b7ddcfe0545daa7496b54f979a6de048ad8df4/src/main/java/com/t0bij/springdatarest/putreproducer/aspect/MoviePutAspect.java#L20-L32

## Branches

- [main](https://github.com/t0bij/springdatarest-put-reproducer): Working "solution" with `HashMap` and AOP workarounds.
- [experimental/hash-set](https://github.com/t0bij/springdatarest-put-reproducer/tree/experimental/hash-set): Setup with `HashSet` and AOP, but adding list elements in the middle fails.
- [experimental/plain](https://github.com/t0bij/springdatarest-put-reproducer/tree/experimental/plain): Setup with `HashSet` and without any AOP or workarounds; many tests fail.

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

Run the tests with:
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

### TestReplaceRating

This test verifies the replacement of a rating. So deleting a raing for one platform and adding a new one for another platform in the same put request.

### TestInsertNewRating

This test verifies the insertion of new ratings for an existing movie.

### TestInsertMultipleRatingsMiddle

This test adds multiple ratings and one in the middle. Initially, there is a rating for `ratingPlatformId 1`. Then a rating for `ratingPlatformId 3` is added. Finally, a rating for `ratingPlatformId 2` is added, which is in the middle.
