-- Insert Rating Platforms
INSERT INTO rating_platform (name)
VALUES ('IMDb');
INSERT INTO rating_platform (name)
VALUES ('Rotten Tomatoes');
INSERT INTO rating_platform (name)
VALUES ('Metacritic');

-- Insert Movies
INSERT INTO movie (name)
VALUES ('Inception');
INSERT INTO movie (name)
VALUES ('The Dark Knight');
INSERT INTO movie (name)
VALUES ('Interstellar');

-- Insert Ratings
INSERT INTO rating (movie_id, rating_platform_id, score)
VALUES ((SELECT id FROM movie WHERE name = 'Inception'),
        (SELECT id FROM rating_platform WHERE name = 'IMDb'),
        9);

INSERT INTO rating (movie_id, rating_platform_id, score)
VALUES ((SELECT id FROM movie WHERE name = 'Inception'),
        (SELECT id FROM rating_platform WHERE name = 'Rotten Tomatoes'),
        8);

INSERT INTO rating (movie_id, rating_platform_id, score)
VALUES ((SELECT id FROM movie WHERE name = 'The Dark Knight'),
        (SELECT id FROM rating_platform WHERE name = 'IMDb'),
        10);

INSERT INTO rating (movie_id, rating_platform_id, score)
VALUES ((SELECT id FROM movie WHERE name = 'Interstellar'),
        (SELECT id FROM rating_platform WHERE name = 'Metacritic'),
        9);
