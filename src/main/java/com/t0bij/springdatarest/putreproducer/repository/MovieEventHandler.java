package com.t0bij.springdatarest.putreproducer.repository;

import com.t0bij.springdatarest.putreproducer.model.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@RepositoryEventHandler
@Component
public class MovieEventHandler {

    @Autowired
    private MovieRepository movieRepository;

    @HandleBeforeSave
    public void handleSave(Movie movie) {
//        System.out.println("Before save: " + movie);
    }

}