package com.rohitbaranwal.projectreactor.service;

import com.rohitbaranwal.projectreactor.domain.Movie;
import com.rohitbaranwal.projectreactor.domain.MovieInfo;
import com.rohitbaranwal.projectreactor.domain.Review;
import com.rohitbaranwal.projectreactor.exception.MovieException;
import com.rohitbaranwal.projectreactor.exception.NetworkException;
import com.rohitbaranwal.projectreactor.exception.ServiceException;
import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

@Slf4j
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

  private static RetryBackoffSpec getRetryBackOff() {
    return Retry.backoff(3, Duration.ofMillis(500L))
        .filter(e -> e instanceof MovieException) //retry will happen when MovieException is thrown
        .onRetryExhaustedThrow(
            (retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure()));
  }

  //Perform Error Handling
  public Flux<Movie> getAllMovies_1() {

    Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveMoviesFlux();
    return movieInfoFlux.flatMap(movieInfo -> {
          // Allow the reviewService call to fail silently
          Flux<Review> reviewFlux =
              reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId());

          // Collect reviews into a single list
          Mono<List<Review>> monoReviewList = reviewFlux.collectList();
          return monoReviewList
              .map(reviewList -> new Movie(movieInfo, reviewList));
        })
        .onErrorMap((ex) -> {
          log.error("Exception is: ", ex);
//          When to use return  : The exception is just being passed as a value or transformed and
//          returned in the context of the reactive pipeline (like with ). -> EXAMPLE IN FluxandMonoGeneratorService onErrorMap method
//          When to use throw : The exception is raised immediately and interrupts the
//          current thread of execution, effectively signaling an error and preventing further execution of the block.
          throw new MovieException(ex.getMessage());
        })
        .log();
  }

  public Flux<Movie> getAllMovies_1_retry() {

    Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveMoviesFlux();
    return movieInfoFlux.flatMap(movieInfo -> {
          // Allow the reviewService call to fail silently
          Flux<Review> reviewFlux =
              reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId());

          // Collect reviews into a single list
          Mono<List<Review>> monoReviewList = reviewFlux.collectList();
          return monoReviewList
              .map(reviewList -> new Movie(movieInfo, reviewList));
        })
        .onErrorMap((ex) -> {
          log.error("Exception is: ", ex);
          throw new MovieException(ex.getMessage());
        })
        .retry(
            3) //this means it will try first time if it fails it will retry for 3 more times at max to get correct results
        .log();
  }

  public Flux<Movie> getAllMovies_1_retryWhen() {

    //Retry retryWhen = Retry.backoff(3, Duration.ofMillis(500L)); //DEFAULT IT THROWS RETRYEXHAUSTEDEXCEPTION CAUSING TEST TO FAIL

    //TO FIX THIS
    var retryWhen = Retry.backoff(3, Duration.ofMillis(500L)).onRetryExhaustedThrow(
        (retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure()));

    Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveMoviesFlux();
    return movieInfoFlux.flatMap(movieInfo -> {
          // Allow the reviewService call to fail silently
          Flux<Review> reviewFlux =
              reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId());

          // Collect reviews into a single list
          Mono<List<Review>> monoReviewList = reviewFlux.collectList();
          return monoReviewList
              .map(reviewList -> new Movie(movieInfo, reviewList));
        })
        .onErrorMap((ex) -> {
          log.error("Exception is: ", ex);
          throw new MovieException(ex.getMessage());
        })
        .retryWhen(retryWhen)
        .log();
  }

  public Flux<Movie> getAllMovies_1_retryWhen_1() {

    Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveMoviesFlux();
    return movieInfoFlux.flatMap(movieInfo -> {
          // Allow the reviewService call to fail silently
          Flux<Review> reviewFlux =
              reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId());

          // Collect reviews into a single list
          Mono<List<Review>> monoReviewList = reviewFlux.collectList();
          return monoReviewList
              .map(reviewList -> new Movie(movieInfo, reviewList));
        })
        .onErrorMap((ex) -> {
          log.error("Exception is: ", ex);
          if (ex instanceof NetworkException) {
            throw new MovieException(ex.getMessage());
          } else {
            throw new ServiceException(ex.getMessage());
          }
        })
        .retryWhen(getRetryBackOff())
        .log();
  }

  public Flux<Movie> getAllMovies_1_repeat() {

    Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveMoviesFlux();
    return movieInfoFlux.flatMap(movieInfo -> {
          // Allow the reviewService call to fail silently
          Flux<Review> reviewFlux =
              reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId());

          // Collect reviews into a single list
          Mono<List<Review>> monoReviewList = reviewFlux.collectList();
          return monoReviewList
              .map(reviewList -> new Movie(movieInfo, reviewList));
        })
        .onErrorMap((ex) -> {
          log.error("Exception is: ", ex);
          if (ex instanceof NetworkException) {
            throw new MovieException(ex.getMessage());
          } else {
            throw new ServiceException(ex.getMessage());
          }
        })
        .retryWhen(getRetryBackOff())
        .repeat() //subscribes and then keeps on continuing
        .log();
  }

  public Flux<Movie> getAllMovies_1_repeat_n(Long n) {

    Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveMoviesFlux();
    return movieInfoFlux.flatMap(movieInfo -> {
          // Allow the reviewService call to fail silently
          Flux<Review> reviewFlux =
              reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId());

          // Collect reviews into a single list
          Mono<List<Review>> monoReviewList = reviewFlux.collectList();
          return monoReviewList
              .map(reviewList -> new Movie(movieInfo, reviewList));
        })
        .onErrorMap((ex) -> {
          log.error("Exception is: ", ex);
          if (ex instanceof NetworkException) {
            throw new MovieException(ex.getMessage());
          } else {
            throw new ServiceException(ex.getMessage());
          }
        })
        .retryWhen(getRetryBackOff())
        .repeat(n) //subscribes and then keeps on continuing till n times
        .log();
  }


  //Perform Error Handling
  public Flux<Movie> getAllMovies_2() {
    // Throw an error when the movieInfoService call fails
    Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveMoviesFlux()
        .onErrorMap(
            throwable -> new RuntimeException("Failed to retrieve movie information", throwable));

    return movieInfoFlux.flatMap(movieInfo -> {
      // Allow the reviewService call to fail silently
      Flux<Review> reviewFlux = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
          .onErrorResume(throwable -> Flux.empty());

      // Collect reviews into a single list
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
