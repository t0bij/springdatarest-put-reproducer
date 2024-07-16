-- Create Rating Platforms Table
CREATE TABLE rating_platform
(
    id   BIGINT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR2(255) NOT NULL,
    PRIMARY KEY (id)
);

-- Create Movies Table
CREATE TABLE movie
(
    id   BIGINT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR2(255) NOT NULL,
    PRIMARY KEY (id)
);

-- Create Ratings Table
CREATE TABLE rating
(
    movie_id           BIGINT  NOT NULL,
    rating_platform_id BIGINT  NOT NULL,
    score              INTEGER NOT NULL CHECK (score BETWEEN 1 AND 10),
    PRIMARY KEY (movie_id, rating_platform_id),
    FOREIGN KEY (movie_id) REFERENCES movie (id),
    FOREIGN KEY (rating_platform_id) REFERENCES rating_platform (id)
);
