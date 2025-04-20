package com.rohitbaranwal.projectreactor.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;

import com.rohitbaranwal.projectreactor.exception.MovieException;
import com.rohitbaranwal.projectreactor.exception.NetworkException;
import com.rohitbaranwal.projectreactor.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class) //from JUNIT5
public class MovieReactiveServiceMockTest {

  @InjectMocks
  MovieReactiveService movieReactiveService;

  @Mock
  MovieInfoService movieInfoService;

  @Mock
  ReviewService reviewService;

  @Test
  void getAllMovies_1() {

    Mockito.when(movieInfoService.retrieveMoviesFlux())
        .thenCallRealMethod();

    Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
        .thenCallRealMethod();

    var moviesFlux = movieReactiveService.getAllMovies_1();

    StepVerifier.create(moviesFlux)
        .expectNextCount(3)
        .verifyComplete();
  }

  @Test
  void getAllMovies_1_exception() {

    var errorMessage = "Exception occured in Review Service";

    Mockito.when(movieInfoService.retrieveMoviesFlux())
        .thenCallRealMethod();

    Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
        .thenThrow(new RuntimeException(errorMessage));

    var moviesFlux = movieReactiveService.getAllMovies_1();

    StepVerifier.create(moviesFlux)
        .expectError(MovieException.class)
        .verify();
  }

  @Test
  void getAllMovies_1_retry_exception() {

    var errorMessage = "Exception occured in Review Service";

    Mockito.when(movieInfoService.retrieveMoviesFlux())
        .thenCallRealMethod();

    Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
        .thenThrow(new RuntimeException(errorMessage));

    var moviesFlux = movieReactiveService.getAllMovies_1_retry();

    StepVerifier.create(moviesFlux)
        .expectError(MovieException.class)
        .verify();

    Mockito.verify(reviewService, Mockito.times(4)).retrieveReviewsFlux(isA(Long.class));
  }

  @Test
  void getAllMovies_1_retryWhen_exception() {

    var errorMessage = "Exception occured in Review Service";

    Mockito.when(movieInfoService.retrieveMoviesFlux())
        .thenCallRealMethod();

    Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
        .thenThrow(new RuntimeException(errorMessage));

    var moviesFlux = movieReactiveService.getAllMovies_1_retryWhen();

    StepVerifier.create(moviesFlux)
        .expectError(MovieException.class)
        .verify();

    Mockito.verify(reviewService, Mockito.times(4)).retrieveReviewsFlux(isA(Long.class));
  }

  @Test
  void getAllMovies_1_retryWhen_1_networkexception() {

    var errorMessage = "Exception occured in Review Service";

    Mockito.when(movieInfoService.retrieveMoviesFlux())
        .thenCallRealMethod();

    Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
        .thenThrow(new NetworkException(errorMessage));

    var moviesFlux = movieReactiveService.getAllMovies_1_retryWhen_1();

    StepVerifier.create(moviesFlux)
        .expectError(MovieException.class)
        .verify();

    Mockito.verify(reviewService, Mockito.times(4)).retrieveReviewsFlux(isA(Long.class));
  }

  @Test
  void getAllMovies_1_retryWhen_1_serviceexception() {

    var errorMessage = "Exception occured in Review Service";

    Mockito.when(movieInfoService.retrieveMoviesFlux())
        .thenCallRealMethod();

    Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
        .thenThrow(new RuntimeException(errorMessage));

    var moviesFlux = movieReactiveService.getAllMovies_1_retryWhen_1();

    StepVerifier.create(moviesFlux)
        .expectError(ServiceException.class)
        .verify();

    Mockito.verify(reviewService, Mockito.times(1)).retrieveReviewsFlux(isA(Long.class));
  }

  @Test
  void getAllMovies_1_repeat() {

    Mockito.when(movieInfoService.retrieveMoviesFlux())
        .thenCallRealMethod();

    Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
        .thenCallRealMethod();

    var moviesFlux = movieReactiveService.getAllMovies_1_repeat();

    StepVerifier.create(moviesFlux)
        .expectNextCount(6)
        .thenCancel() //since repeat will keep on sending data indefinetely , thenCancel is used to check first few then cancel subscription.this is done to verify behaviour of how repeat() works
        .verify();

    Mockito.verify(reviewService, Mockito.times(6)).retrieveReviewsFlux(isA(Long.class));
  }

  @Test
  void getAllMovies_1_repeat_n() {

    Mockito.when(movieInfoService.retrieveMoviesFlux())
        .thenCallRealMethod();

    Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
        .thenCallRealMethod();

    var numberOfTimes = 2L;

    var moviesFlux = movieReactiveService.getAllMovies_1_repeat_n(numberOfTimes);

    StepVerifier.create(moviesFlux)
        .expectNextCount(9)// first then repeat 2 times( 3 + 2*3)
        .verifyComplete();

    Mockito.verify(reviewService, Mockito.times(9)).retrieveReviewsFlux(isA(Long.class));
  }
}
