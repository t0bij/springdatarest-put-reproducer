package com.t0bij.springdatarest.putreproducer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingId implements Serializable {

    private Long movieId;
    private Long ratingPlatformId;

}