package com.rohitbaranwal.projectreactor.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class MovieReactiveServiceTest {

  private MovieInfoService movieInfoService = new MovieInfoService();

  private ReviewService reviewService = new ReviewService();

  MovieReactiveService movieReactiveService = new MovieReactiveService(movieInfoService, reviewService);

  @Test
  void getAllMovies() {
    var movieFlux = movieReactiveService.getAllMovies();

    StepVerifier.create(movieFlux)
        .assertNext(movie -> {
          //name of movie
          assertEquals("Batman Begins", movie.getMovieInfo().getName());
          //reviewList
          assertEquals(2, movie.getReviewList().size());
        })
        .assertNext(movie -> {
          //name of movie
          assertEquals("The Dark Knight", movie.getMovieInfo().getName());
          //reviewList
          assertEquals(2, movie.getReviewList().size());
        })
        .assertNext(movie -> {
          //name of movie
          assertEquals("Dark Knight Rises", movie.getMovieInfo().getName());
          //reviewList
          assertEquals(2, movie.getReviewList().size());
        })
        .verifyComplete();
  }

  @Test
  public void getMovieById() {
    Long movieId = 3L;
    var movieMono = movieReactiveService.getMovieById(movieId);

    StepVerifier.create(movieMono)
        .assertNext(movie -> {
          //name of movie
          assertEquals("Batman Begins", movie.getMovieInfo().getName());
          assertEquals(movieId, movie.getMovieInfo().getMovieInfoId());
          //reviewList
          assertEquals(2, movie.getReviewList().size());
        })
        .verifyComplete();
  }
}