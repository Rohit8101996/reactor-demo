package com.rohitbaranwal.projectreactor.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Review {

  private Long reviewId;

  private Long movieInfoId;

  private String comment;

  private Double rating;
}
