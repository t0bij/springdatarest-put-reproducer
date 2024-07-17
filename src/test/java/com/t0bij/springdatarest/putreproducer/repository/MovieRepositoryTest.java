package com.t0bij.springdatarest.putreproducer.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class MovieRepositoryTest {

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @PersistenceContext
    private EntityManager entityManager;

    private static long countOccurrences(String logs, String keyword) {
        return logs.lines()
                .filter(line -> line.contains(keyword))
                .count();
    }

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }

    public void assertSqlOperationCounts(long expectedDeleteCount, long expectedInsertCount, long expectedUpdateCount) {

        String logs = outputStreamCaptor.toString();
        System.err.println(logs);

        long actualDeleteCount = countOccurrences(logs, "Hibernate: delete");
        long actualInsertCount = countOccurrences(logs, "Hibernate: insert");
        long actualUpdateCount = countOccurrences(logs, "Hibernate: update");

        assertEquals(expectedDeleteCount, actualDeleteCount, "Expected " + expectedDeleteCount + " delete operations in the logs.");
        assertEquals(expectedInsertCount, actualInsertCount, "Expected " + expectedInsertCount + " insert operations in the logs.");
        assertEquals(expectedUpdateCount, actualUpdateCount, "Expected " + expectedUpdateCount + " update operations in the logs.");
    }

    @Test
    public void testGetMovieById() throws Exception {
        mockMvc.perform(get("/api/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Inception"))
                .andExpect(jsonPath("$.ratings").isArray())
                .andExpect(jsonPath("$.ratings.length()").value(2))
                .andExpect(jsonPath("$.ratings[0].ratingPlatformId").value(1))
                .andExpect(jsonPath("$.ratings[0].score").value(9))
                .andExpect(jsonPath("$.ratings[1].ratingPlatformId").value(2))
                .andExpect(jsonPath("$.ratings[1].score").value(8));

        assertSqlOperationCounts(0, 0, 0);
    }


    @Test
    public void testUpdateMovieTitle() throws Exception {
        String updatedMovieJson = """
                {
                    "name": "Inception - Update",
                    "ratings": [
                        {
                            "ratingPlatformId": 1,
                            "score": 9
                        },
                        {
                            "ratingPlatformId": 2,
                            "score": 8
                        }
                    ]
                }
                """;

        mockMvc.perform(put("/api/movies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedMovieJson))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Inception - Update"))
                .andExpect(jsonPath("$.ratings").isArray())
                .andExpect(jsonPath("$.ratings.length()").value(2))
                .andExpect(jsonPath("$.ratings[0].ratingPlatformId").value(1))
                .andExpect(jsonPath("$.ratings[0].score").value(9))
                .andExpect(jsonPath("$.ratings[1].ratingPlatformId").value(2))
                .andExpect(jsonPath("$.ratings[1].score").value(8));

        assertSqlOperationCounts(0, 0, 1);
    }

    @Test
    public void testChangeRatingsAndOrder() throws Exception {
        String updatedMovieJson = """
                {
                    "name": "Inception",
                    "ratings": [
                        {
                            "ratingPlatformId": 2,
                            "score": 2
                        },
                        {
                            "ratingPlatformId": 1,
                            "score": 1
                        }
                    ]
                }
                """;

        mockMvc.perform(put("/api/movies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedMovieJson))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Inception"))
                .andExpect(jsonPath("$.ratings").isArray())
                .andExpect(jsonPath("$.ratings.length()").value(2))
                .andExpect(jsonPath("$.ratings[0].ratingPlatformId").value(1))
                .andExpect(jsonPath("$.ratings[0].score").value(1))
                .andExpect(jsonPath("$.ratings[1].ratingPlatformId").value(2))
                .andExpect(jsonPath("$.ratings[1].score").value(2));

        assertSqlOperationCounts(0, 0, 2);
    }

    @Test
    public void testDeleteAllRatings() throws Exception {
        String updatedMovieJson = """
                {
                    "name": "Inception",
                    "ratings": [
                    ]
                }
                """;

        mockMvc.perform(put("/api/movies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedMovieJson))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Inception"))
                .andExpect(jsonPath("$.ratings").isArray())
                .andExpect(jsonPath("$.ratings.length()").value(0));

        assertSqlOperationCounts(2, 0, 0);
    }

    @Test
    public void testInsertNewRating() throws Exception {
        String updatedMovieJson = """
                {
                    "name": "Inception",
                    "ratings": [
                        {
                            "ratingPlatformId": 1,
                            "score": 9
                        },
                        {
                            "ratingPlatformId": 3,
                            "score": 3
                        },
                        {
                            "ratingPlatformId": 2,
                            "score": 8
                        }
                    ]
                }
                """;

        mockMvc.perform(put("/api/movies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedMovieJson))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Inception"))
                .andExpect(jsonPath("$.ratings").isArray())
                .andExpect(jsonPath("$.ratings.length()").value(3))
                .andExpect(jsonPath("$.ratings[0].ratingPlatformId").value(1))
                .andExpect(jsonPath("$.ratings[0].score").value(9))
                .andExpect(jsonPath("$.ratings[1].ratingPlatformId").value(2))
                .andExpect(jsonPath("$.ratings[1].score").value(8))
                .andExpect(jsonPath("$.ratings[2].ratingPlatformId").value(3))
                .andExpect(jsonPath("$.ratings[2].score").value(3));

        assertSqlOperationCounts(0, 1, 0);
    }

    @Test
    public void testReplaceRating() throws Exception {
        String updatedMovieJson = """
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
                """;

        mockMvc.perform(put("/api/movies/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedMovieJson))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/movies/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("The Dark Knight"))
                .andExpect(jsonPath("$.ratings").isArray())
                .andExpect(jsonPath("$.ratings.length()").value(1))
                .andExpect(jsonPath("$.ratings[0].ratingPlatformId").value(2))
                .andExpect(jsonPath("$.ratings[0].score").value(2));

        assertSqlOperationCounts(1, 1, 0);
    }

    @Test
    public void testInsertMultipleRatingsMiddle() throws Exception {
        String updatedMovieJson = """
                {
                    "name": "The Dark Knight",
                    "ratings": [
                        {
                            "ratingPlatformId": 1,
                            "score": 10
                        },
                        {
                            "ratingPlatformId": 3,
                            "score": 3
                        }
                    ]
                }
                """;

        mockMvc.perform(put("/api/movies/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedMovieJson))
                .andExpect(status().isNoContent());

        updatedMovieJson = """
                {
                    "name": "The Dark Knight",
                    "ratings": [
                        {
                            "ratingPlatformId": 1,
                            "score": 10
                        },
                        {
                            "ratingPlatformId": 3,
                            "score": 3
                        },
                        {
                            "ratingPlatformId": 2,
                            "score": 2
                        }
                    ]
                }
                """;

        mockMvc.perform(put("/api/movies/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedMovieJson))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/movies/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("The Dark Knight"))
                .andExpect(jsonPath("$.ratings").isArray())
                .andExpect(jsonPath("$.ratings.length()").value(3))
                .andExpect(jsonPath("$.ratings[0].ratingPlatformId").value(1))
                .andExpect(jsonPath("$.ratings[0].score").value(10))
                .andExpect(jsonPath("$.ratings[1].ratingPlatformId").value(2))
                .andExpect(jsonPath("$.ratings[1].score").value(2))
                .andExpect(jsonPath("$.ratings[2].ratingPlatformId").value(3))
                .andExpect(jsonPath("$.ratings[2].score").value(3));

        assertSqlOperationCounts(0, 2, 0);
    }

}