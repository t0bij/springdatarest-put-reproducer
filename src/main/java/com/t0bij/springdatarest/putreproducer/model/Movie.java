package com.t0bij.springdatarest.putreproducer.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "movie")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("movie-ratings")
    @MapKey(name = "ratingPlatformId")
    @ToString.Exclude
    private Map<Long, Rating> ratings = new LinkedHashMap<>();

    public Collection<Rating> getRatings() {
        return ratings.values();
    }

    public void setRatings(Collection<Rating> ratings) {
        this.ratings.clear();
        for (var rating : ratings) {
            addRating(rating);
        }
    }

    public void addRating(Rating rating) {
        if (!ratings.containsValue(rating)) {
            ratings.put(rating.getRatingPlatformId(), rating);
            rating.setMovie(this);
        }
    }

    public void removeRating(Rating rating) {
        if (ratings.containsValue(rating)) {
            ratings.remove(rating.getRatingPlatformId(), rating);
            rating.setMovie(null);
        }
    }

}