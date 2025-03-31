package com.rohitbaranwal.projectreactor.service;

import com.rohitbaranwal.projectreactor.domain.Movie;
import com.rohitbaranwal.projectreactor.domain.MovieInfo;
import com.rohitbaranwal.projectreactor.domain.Review;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MovieReactiveService {

  private MovieInfoService movieInfoService;

  private ReviewService reviewService;

  public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService) {
    this.movieInfoService = movieInfoService;
    this.reviewService = reviewService;
  }

  public Flux<Movie> getAllMovies() {
    Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveMoviesFlux();

    return movieInfoFlux.flatMap(movieInfo -> {
      Flux<Review> reviewFlux = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId());
      //I want single list of review for Movie Object
      Mono<List<Review>> monoReviewList = reviewFlux.collectList();
      return monoReviewList.map(reviewList -> new Movie(movieInfo, reviewList));
    }).log();
  }

  public Mono<Movie> getMovieById(long movieId) {
    Mono<MovieInfo> monoMovie = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);

//    either use this
//    return monoMovie.flatMap(movieInfo -> {
//      Flux<Review> reviewFlux = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId());
//      //I want single list of review for Movie Object
//      Mono<List<Review>> monoReviewList = reviewFlux.collectList();
//      //either use below or
//      return monoReviewList.map(reviewList -> new Movie(movieInfo, reviewList));
//    }).log();

    //since its mono
      Flux<Review> reviewFlux = reviewService.retrieveReviewsFlux(movieId);
      //I want single list of review for Movie Object
      Mono<List<Review>> monoReviewList = reviewFlux.collectList();
      return monoMovie.zipWith(monoReviewList, (movieInfo, reviewList) -> new Movie(movieInfo, reviewList)).log();

  }
}
