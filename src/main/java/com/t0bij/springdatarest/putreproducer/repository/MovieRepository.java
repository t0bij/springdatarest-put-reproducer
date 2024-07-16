package com.t0bij.springdatarest.putreproducer.repository;

import com.t0bij.springdatarest.putreproducer.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface MovieRepository extends JpaRepository<Movie, Long> {
}