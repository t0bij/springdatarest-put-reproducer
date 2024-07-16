package com.t0bij.springdatarest.putreproducer.repository;

import com.t0bij.springdatarest.putreproducer.model.Movie;
import com.t0bij.springdatarest.putreproducer.model.Rating;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MoviePutAspect {

    @Autowired
    private MovieRepository movieRepository;

    @Pointcut("execution(* org.springframework.data.rest.webmvc.RepositoryEntityController.putItemResource(..))")
    public void putItemResource() {
    }

    @Around("putItemResource()")
    public Object aroundPutItemResource(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();

        if (args[1] instanceof PersistentEntityResource payload) {
            Object content = payload.getContent();
            if (content instanceof Movie updatedMovie) {
                for (Rating rating : updatedMovie.getRatings()) {
                    rating.setMovieId(updatedMovie.getId());
                    rating.setMovie(updatedMovie);
                }
            }
        }

        return pjp.proceed(args);
    }
}