package com.rohitbaranwal.projectreactor.service;

import static com.rohitbaranwal.projectreactor.util.CommonUtil.delay;

import java.util.List;

public class FluxAndMonoSchedulersService {

  static List<String> namesList = List.of("alex", "ben", "chloe");
  static List<String> namesList1 = List.of("adam", "jill", "jack");

  private String upperCase(String name) {
    delay(1000);
    return name.toUpperCase();
  }

}
