package com.rohitbaranwal.projectreactor.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class Movie {

  private MovieInfo movieInfo;

  private List<Review> reviewList;

  private Revenue revenue;

  public Movie(MovieInfo movieInfo, List<Review> reviewList) {
    this.movieInfo = movieInfo;
    this.reviewList = reviewList;
  }

}
