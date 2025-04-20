package com.rohitbaranwal.projectreactor.first;

import com.rohitbaranwal.projectreactor.exception.ReactorException;
import java.util.List;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

public class FluxAndMonoGeneratorServiceTest {

  FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();


  @Test
  public void testNamesFlux() {

    var namesFlux = fluxAndMonoGeneratorService.namesFlux();

    StepVerifier.create(namesFlux)
        //.expectNext("alex", "ben", "chloe")
        .expectNextCount(3) //no of elements being returned can also be tested
        .verifyComplete();
  }

  @Test
  public void testNamesFlux_map() {

    var namesFlux = fluxAndMonoGeneratorService.namesFlux_map();

    StepVerifier.create(namesFlux)
        .expectNext("ALEX", "BEN", "CHLOE")
        .verifyComplete();
  }

  @Test
  public void testNamesFlux_immutability() {

    var namesFlux = fluxAndMonoGeneratorService.namesFlux_immutability();

    StepVerifier.create(namesFlux)
        .expectNext("alex", "ben", "chloe")
        .verifyComplete();
  }

  @Test
  void namesFlux_MapThenFilterThanMap() {

    var namesFlux = fluxAndMonoGeneratorService.namesFlux_MapThenfilterThenMap(3);

    StepVerifier.create(namesFlux)
        .expectNext("4-ALEX", "5-CHLOE")
        .verifyComplete();
  }

  @Test
  void namesFlux_MapThenfilterThenMap_doCallBackMethods() {
    //Testing doOnNext
    var namesFlux = fluxAndMonoGeneratorService.namesFlux_MapThenfilterThenMap_doCallBackMethods(3);

    StepVerifier.create(namesFlux)
        .expectNext("4-ALEX", "5-CHLOE")
        .verifyComplete();
  }

  @Test
  void namesMono_map_filter() {

    var namesFlux = fluxAndMonoGeneratorService.namesMono_map_filter(3);

    StepVerifier.create(namesFlux)
        .expectNext("ALEX")
        .verifyComplete();
  }

  @Test
  void namesFlux_mapThenfilter_thenFlatMap() {

    var namesFlux = fluxAndMonoGeneratorService.namesFlux_mapThenfilter_thenFlatMap(3);

    StepVerifier.create(namesFlux)
        .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
        .verifyComplete();

  }

  @Test
  void namesFlux_mapThenfilter_thenFlatMap_async() {
    var namesFlux = fluxAndMonoGeneratorService.namesFlux_mapThenfilter_thenFlatMap_async(3);

    StepVerifier.create(namesFlux)
        //This below line will fail for method since we are waiting for execution for each letter to some random delay .
        // but wait until whole execution is completed so below will fail but next below to it will pass
        //.expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
        .expectNextCount(9)
        .verifyComplete();

  }

  @Test
  void namesFlux_mapThenfilter_thenConcatMap() {

    var namesFlux = fluxAndMonoGeneratorService.namesFlux_mapThenfilter_thenConcatMap(3);

    StepVerifier.create(namesFlux)
        //This below will pass now as which failed with flatmap async because ordering is preserved with concatMap
        .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
        .verifyComplete();
  }

  @Test
  void namesMono_map_filter_thenFlatMap() {
    var namesFlux = fluxAndMonoGeneratorService.namesMono_map_filter_thenFlatMap(3);

    StepVerifier.create(namesFlux)
        .expectNext(List.of("A", "L", "E", "X"))
        .verifyComplete();
  }

  @Test
  void namesMono_map_filter_thenFlatMapMany() {
    var namesFlux = fluxAndMonoGeneratorService.namesMono_map_filter_thenFlatMapMany(3);

    StepVerifier.create(namesFlux)
        .expectNext("A", "L", "E", "X")
        .verifyComplete();
  }

  @Test
  void namesFlux_transform() {
    var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform(3);

    StepVerifier.create(namesFlux)
        .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
        .verifyComplete();
  }

  @Test
  void namesFlux_transform_defaultIfEmpty() {

    int strLen = 6;

    var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform_defaultIfEmpty(strLen);

    StepVerifier.create(namesFlux)
        .expectNext("default")
        .verifyComplete();
  }

  @Test
  void namesFlux_transform_switchIfEmpty() {

    int strLen = 6;

    var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform_switchIfEmpty(strLen);

    StepVerifier.create(namesFlux)
        .expectNext("D", "E", "F", "A", "U", "L", "T")
        .verifyComplete();
  }

  @Test
  void explore_concat() {
    var flux = fluxAndMonoGeneratorService.explore_concat();

    StepVerifier.create(flux)
        .expectNext("A", "B", "C", "D", "E", "F")
        .verifyComplete();
  }

  @Test
  void explore_concatwith() {
    var flux = fluxAndMonoGeneratorService.explore_concatwith();

    StepVerifier.create(flux)
        .expectNext("A", "B", "C", "D", "E", "F")
        .verifyComplete();
  }

  @Test
  void explore_concatwith_mono() {
    var flux = fluxAndMonoGeneratorService.explore_concatwith_mono();

    StepVerifier.create(flux)
        .expectNext("A", "B")
        .verifyComplete();

  }


  @Test
  void explore_merge() {
    var flux = fluxAndMonoGeneratorService.explore_merge();

    StepVerifier.create(flux)
        .expectNext("A", "D", "B", "E", "C", "F")
        .verifyComplete();
  }

  @Test
  void explore_mergewith() {
    var flux = fluxAndMonoGeneratorService.explore_mergewith();

    StepVerifier.create(flux)
        //since we have fixed delay and result is there in flux so we see some ordering but in actual case it will be of any order depending on what comes from flux any flux first which is merged
        .expectNext("D", "A", "E", "B", "F", "C")
        .verifyComplete();
  }

  @Test
  void explore_mergewith_mono() {
    var flux = fluxAndMonoGeneratorService.explore_mergewith_mono();

    StepVerifier.create(flux)
        .expectNext("A", "B")
        .verifyComplete();
  }

  @Test
  void explore_mergeSequential() {
    var flux = fluxAndMonoGeneratorService.explore_mergeSequential();

    StepVerifier.create(flux)
        .expectNext("A", "B", "C", "D", "E", "F")
        .verifyComplete();
  }

  @Test
  void explore_zip() {
    var flux = fluxAndMonoGeneratorService.explore_zip();

    StepVerifier.create(flux)
        .expectNext("AD","BE", "CF")
        .verifyComplete();
  }

  @Test
  void explore_zip_1() {
    var flux = fluxAndMonoGeneratorService.explore_zip_1();

    StepVerifier.create(flux)
        .expectNext("AD14","BE25", "CF36")
        .verifyComplete();
  }

  @Test
  void explore_zip_with() {
    var flux = fluxAndMonoGeneratorService.explore_zip_with();

    StepVerifier.create(flux)
        .expectNext("AD","BE", "CF")
        .verifyComplete();
  }

  @Test
  void explore_zipwith_mono() {
    var flux = fluxAndMonoGeneratorService.explore_zipwith_mono();

    StepVerifier.create(flux)
        .expectNext("AB")
        .verifyComplete();
  }

  @Test
  public void exception_flux() {
    var value = fluxAndMonoGeneratorService.exception_flux();

    StepVerifier.create(value)
        .expectNext("A", "B", "C")
        .expectError(RuntimeException.class)
        .verify();
    //This means subscription is cancelled between
    //subscriber and publisher if any error is recieved and also stepverifier we could assert anything post error is encountered
  }

  @Test
  public void exception_flux_1() {
    var value = fluxAndMonoGeneratorService.exception_flux();

    StepVerifier.create(value)
        .expectNext("A", "B", "C")
        .expectError()//asserting without exception also works
        .verify();
  }

  @Test
  public void exception_flux_2() {
    var value = fluxAndMonoGeneratorService.exception_flux();

    StepVerifier.create(value)
        .expectNext("A", "B", "C")
        .expectErrorMessage("Unexpected Error Occured")//asserting with exception message
        .verify();
  }

  @Test
  public void explore_onErrorReturn() {
    var value = fluxAndMonoGeneratorService.explore_onErrorReturn();

    StepVerifier.create(value)
        .expectNext("A", "B", "C", "D")
        .verifyComplete();
  }

  @Test
  public void explore_onErrorResume() {

    var ex = new IllegalStateException("Not a valid State");

    var value = fluxAndMonoGeneratorService.explore_onErrorResume(ex);

    StepVerifier.create(value)
        .expectNext("A", "B", "C", "D", "E", "F")
        .verifyComplete();
  }

  @Test
  public void explore_onErrorResume_1() {

    var ex = new RuntimeException("some exception");

    var value = fluxAndMonoGeneratorService.explore_onErrorResume(ex);

    StepVerifier.create(value)
        .expectNext("A", "B", "C")
        .expectErrorMessage("some exception")
        .verify();
  }

  @Test
  public void explore_onErrorContinue() {

    var value = fluxAndMonoGeneratorService.explore_onErrorContinue();

    StepVerifier.create(value)
        .expectNext("A", "C", "D")
        .verifyComplete();
  }

  @Test
  public void explore_onErrorMap() {

    var value = fluxAndMonoGeneratorService.explore_onErrorMap();

    StepVerifier.create(value)
        .expectNext("A")
        .expectError(ReactorException.class)
        .verify();
  }

  @Test
  public void explore_doOnError() {

    var value = fluxAndMonoGeneratorService.explore_doOnError();

    StepVerifier.create(value)
        .expectNext("A", "B", "C")
        .expectError(IllegalStateException.class)
        .verify();
  }

  @Test
  public void explore_mono_onErrorReturn() {

    var value = fluxAndMonoGeneratorService.explore_mono_onErrorReturn();

    StepVerifier.create(value)
        .expectNext("abc")
        .verifyComplete();
  }

  @Test
  public void exception_mono_onErrorContinue_1() {

    String inputString = "reactor";

    var value = fluxAndMonoGeneratorService.exception_mono_onErrorContinue(inputString);

    StepVerifier.create(value)
        .expectNext(inputString)
        .verifyComplete();
  }

  @Test
  public void exception_mono_onErrorContinue_2() {

    String inputString = "abc";

    var value = fluxAndMonoGeneratorService.exception_mono_onErrorContinue(inputString);

    StepVerifier.create(value)
        .verifyComplete();
  }

}
