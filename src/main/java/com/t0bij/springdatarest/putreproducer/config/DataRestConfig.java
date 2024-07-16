package com.t0bij.springdatarest.putreproducer.config;

import com.t0bij.springdatarest.putreproducer.model.Movie;
import com.t0bij.springdatarest.putreproducer.model.Rating;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class DataRestConfig implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(
            RepositoryRestConfiguration config, CorsRegistry cors) {
        config.exposeIdsFor(Movie.class);
        config.exposeIdsFor(Rating.class);
    }

}