package com.t0bij.springdatarest.putreproducer.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rating")
@IdClass(RatingId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Rating {

    @Id
    @Column(name = "movie_id", nullable = false)
    @EqualsAndHashCode.Include
    @JsonIgnore
    private Long movieId;

    @Id
    @Column(name = "rating_platform_id", nullable = false)
    @EqualsAndHashCode.Include
    private Long ratingPlatformId;

    @Column(name = "score", nullable = false)
    private int score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", insertable = false, updatable = false)
    @JsonBackReference("movie-ratings")
    @ToString.Exclude
    private Movie movie;

}