package com.rohitbaranwal.projectreactor.first;

import com.rohitbaranwal.projectreactor.exception.ReactorException;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class FluxAndMonoGeneratorService {

  public Flux<String> namesFlux() {
    return Flux.fromIterable(List.of("alex", "ben", "chloe")).log(); //db call or service point call
  }

  public Flux<String> namesFlux_map() {
    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
        .map(String::toUpperCase)
        .log(); //db call or service point call
  }

  public Flux<String> namesFlux_immutability() {
    var namesFlux =  Flux.fromIterable(List.of("alex", "ben", "chloe"));
    namesFlux.map(String::toUpperCase);
    //Reactive Stream are immutable and cant be updated,so if we test for upper case in output test fails for it
    //Reactive streams can only be performed on logical change as part of chaining
    return namesFlux;
  }

  public Flux<String> namesFlux_MapThenfilterThenMap(int strLen) {
    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
        .map(String::toUpperCase)
        .filter(s -> s.length() > strLen)
        .map(s -> s.length() + "-" + s)
        .log();
  }

  public static void main(String[] args) {
    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    /* namesFlux is a data publisher which is then subscribed
      - To understand what happened behind added log as chaining in line number 10
      - first request is subscribed to publisher (subscription object is returned back)
      - then request is sent for unbound data available f(i.e., it's asking the Publisher to emit all available items without any limit.)
      - then events are recieved 1 by 1 using onNext() function
      - when all events are sent (each data in list here refers as event , then onComplete() signal is sent back
     */
    fluxAndMonoGeneratorService.namesFlux()
        .subscribe(name ->
            System.out.println("Name is : " + name)
        );

    fluxAndMonoGeneratorService.namesMono()
        .subscribe(name ->
            System.out.println("Mono Name is: " + name)
        );

  }

  public Flux<String> namesFlux_mapThenfilter_thenFlatMap(int strLen) {
    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
        .map(String::toUpperCase)
        .filter(s -> s.length() > strLen)
        //ALEX, CHLOE -> A, L,E,X,C,H,L,O,E
        .flatMap(s -> convertStringToChar(s))
        .log();
  }

  public Flux<String> namesFlux_mapThenfilter_thenFlatMap_async(int strLen) {
    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
        .map(String::toUpperCase)
        .filter(s -> s.length() > strLen)
        //ALEX, CHLOE -> A, L,E,X,C,H,L,O,E
        .flatMap(s -> convertStringToChar_withDelay(s))
        .log();
  }

  public Flux<String> namesFlux_mapThenfilter_thenConcatMap(int strLen) {
    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
        .map(String::toUpperCase)
        .filter(s -> s.length() > strLen)
        //ALEX, CHLOE -> A, L,E,X,C,H,L,O,E
        .concatMap(s -> convertStringToChar_withDelay(s))
        .log();
  }

  public Flux<String> namesFlux_transform(int strLen) {

    Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
        .filter(s -> s.length() > strLen);

    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
        .transform(filterMap)
        //ALEX, CHLOE -> A, L,E,X,C,H,L,O,E
        .concatMap(s -> convertStringToChar_withDelay(s))
        .log();
  }

  public Flux<String> namesFlux_transform_defaultIfEmpty(int strLen) {

    Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
        .filter(s -> s.length() > strLen);

    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
        .transform(filterMap)
        .concatMap(s -> convertStringToChar_withDelay(s))
        //No value will be present if we have strlen in incoming greater than 5
        //we can use default value
        .defaultIfEmpty("default")
        .log();
  }

  public Flux<String> namesFlux_transform_switchIfEmpty(int strLen) {

    Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
        .filter(s -> s.length() > strLen)
        .concatMap(s -> convertStringToChar_withDelay(s));

    Flux<String> defaultFlux = Flux.just("default").transform(filterMap); //D, E, F, A, U, L, T

    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
        .transform(filterMap)
        .switchIfEmpty(defaultFlux) //RETURN defaultflux if no output post transform
        .log();
  }

  public Flux<String> explore_concat() {

    Flux<String> abcFlux = Flux.just("A", "B", "C"); //think data coming from service A

    Flux<String> defFlux = Flux.just("D", "E", "F"); //THINK DATA COMING FROM SERVICE B

    //concat //this works like first complete fluxABC and then fluxDEF
    //using static method of Flux class
    return Flux.concat(abcFlux, defFlux).log();
  }

  public Flux<String> explore_concatwith() {

    Flux<String> abcFlux = Flux.just("A", "B", "C"); //think data coming from service A

    Flux<String> defFlux = Flux.just("D", "E", "F"); //THINK DATA COMING FROM SERVICE B

    //concatWith //this works like first complete fluxABC and then fluxDEF
    //using instance method of abcFlux
    return abcFlux.concatWith(defFlux).log();
  }

  //concat subscribes to the Publishers in sequence
  public Flux<String> explore_concatwith_mono() {

    Mono<String> aMono = Mono.just("A"); //single data coming from service A

    Mono<String> bMono = Mono.just("B"); //single DATA COMING FROM SERVICE B

    //concatWith //this works like first complete aMono and then bMono
    //using instance method of aMono
    return aMono.concatWith(bMono).log(); //returns flux of two values post concat
  }

  public Flux<String> explore_merge() {

    Flux<String> abcFlux = Flux.just("A", "B", "C")
        .delayElements(Duration.ofMillis(100)); //think data coming from service A

    Flux<String> defFlux = Flux.just("D", "E", "F")
        .delayElements(Duration.ofMillis(150)); //THINK DATA COMING FROM SERVICE B
    //ordering is not present with merge or mergeWith any result could be return depending on what is available
    return Flux.merge(abcFlux, defFlux).log();
  }

  public Flux<String> explore_mergewith() {

    Flux<String> abcFlux = Flux.just("A", "B", "C")
        .delayElements(Duration.ofMillis(100)); //think data coming from service A

    Flux<String> defFlux = Flux.just("D", "E", "F")
        .delayElements(Duration.ofMillis(90)); //THINK DATA COMING FROM SERVICE B

    //ordering is not present with merge or mergeWith any result could be return depending on what is available
    return abcFlux.mergeWith(defFlux).log();
  }

  public Flux<String> explore_mergewith_mono() {

    Mono<String> aMono = Mono.just("A"); //single data coming from service A

    Mono<String> bMono = Mono.just("B"); //single DATA COMING FROM SERVICE B

    return aMono.mergeWith(bMono).log(); //returns flux of two values post concat
  }

  public Flux<String> explore_mergeSequential() {

    Flux<String> abcFlux = Flux.just("A", "B", "C")
        .delayElements(Duration.ofMillis(100)); //think data coming from service A

    Flux<String> defFlux = Flux.just("D", "E", "F")
        .delayElements(Duration.ofMillis(150)); //THINK DATA COMING FROM SERVICE B
    //ordering is preserved first abcFlux then defFlux
    return Flux.mergeSequential(abcFlux, defFlux).log();
  }

  public Flux<String> explore_zip() {

    var abcFlux = Flux.just("A", "B", "C");
    var defFlux = Flux.just("D", "E", "F");

    //This will return AD , BE , CF
    return Flux.zip(abcFlux, defFlux, (first ,second) -> first + second);
  }

  public Flux<String> explore_zip_1() {
    //four publishers
    var abcFlux = Flux.just("A", "B", "C");
    var defFlux = Flux.just("D", "E", "F");
    var flux3 = Flux.just("1", "2", "3");
    var flux4 = Flux.just("4", "5", "6");

    //This will return AD14 , BE25 , CF36
    //basically when we use zip it forms Tuple as result
    //thing like Tuple4<String, String, Integer, Integer> then we use map on tuple to comibine all 4 result
    return Flux.zip(abcFlux, defFlux, flux3, flux4).map((t1) -> t1.getT1() + t1.getT2() + t1.getT3() + t1.getT4()).log();
  }

  public Flux<String> explore_zip_with() {

    var abcFlux = Flux.just("A", "B", "C");
    var defFlux = Flux.just("D", "E", "F");

    //This will return AD , BE , CF
    return abcFlux.zipWith(defFlux, (first, second) -> first + second).log();
  }

  public Mono<String> explore_zipwith_mono() {

    Mono<String> aMono = Mono.just("A"); //single data coming from service A

    Mono<String> bMono = Mono.just("B"); //single DATA COMING FROM SERVICE B

    return aMono.zipWith(bMono).map(t2 -> t2.getT1() + t2.getT2()).log();
  }

  public Flux<String> namesFlux_MapThenfilterThenMap_doCallBackMethods(int strLen) {
    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
        .map(String::toUpperCase)
        .filter(s -> s.length() > strLen)
        .map(s -> s.length() + "-" + s)
        //used for metric, auditing, logging
        //accepts consumer has no output
        .doOnNext(name -> {
          //this are also called side effect operators since they dont change original data source
          //example updating name to lower case
          //but on test we will still see upper caps
          System.out.println("Name is: " + name.toLowerCase());
        })
        .doOnSubscribe(s -> {
          //This will be printed once since StepVerifier subscribes it once at first then elements are emitted
          System.out.println("Subscription is: " + s);
        })
        .doOnComplete(() -> {
          //this executes runnable , no INPUTS executes on complete
          System.out.println("Inside the complete callback");
        })
        //this is invoked at end irrespective reactive sequence completed success or failed
        //accepts consumer with type of SIGNALTYPE
        .doFinally(signalType -> {
          System.out.println("Inside finally: "
              + signalType);//this says last event that got emitted out of reactive stream
        })
        .log();//this becomes obsulute after use of doOnNext()
  }

  public Flux<String> exception_flux() {

    return Flux.just("A", "B", "C")
        .concatWith(Flux.error(new RuntimeException("Unexpected Error Occured")))
        .concatWith(Flux.just("D"))
        .log();
  }

  public Flux<String> explore_onErrorReturn() {

    return Flux.just("A", "B", "C")
        .concatWith(Flux.error(new IllegalStateException("Unexpected Error Occured")))
        .onErrorReturn("D") //HANDLE EXCEPTION AND RETURN DEFAULT VALUE WITH INITIAL VALUES
        .log();
  }

  public Flux<String> explore_onErrorResume(Exception e) {

    var recoveryFlux = Flux.just("D", "E", "F");

    return Flux.just("A", "B", "C")
        .concatWith(Flux.error(e))
        .onErrorResume(ex -> {
          log.error("Exception is : " + ex);
          if (ex instanceof IllegalStateException) {
            return recoveryFlux; // ON FAILURE HANDLE WITH A PUBLISHER (FLUX )
          } else {
            return Flux.error(ex);
          }
        })
        .log();
  }

  public Flux<String> explore_onErrorContinue() {

    return Flux.just("A", "B", "C")
        .map(name -> {
          if (name.equals("B")) {
            throw new IllegalStateException("Exception Occured");
          }
          return name;
        })
        .concatWith(Flux.just("D")) //This will too get emitted even after excpetion is thrown
        .onErrorContinue((ex, name) -> {
          log.error("Exception is : ", ex);
          log.error("Element caused exception: {}", name);
        }) //REJECT ELEMENTS WHICH CAUSED EXCPETION AND THEN CONTINUE
        .log();
  }

  public Flux<String> explore_onErrorMap() {

    return Flux.just("A", "B", "C")
        .map(name -> {
          if (name.equals("B")) {
            throw new IllegalStateException("Exception Occured");
          }
          return name;
        })
        .concatWith(Flux.just("D"))
        .onErrorMap((ex) -> {
          log.error("Exception is : ", ex);
          return new ReactorException(ex, ex.getMessage());
        })
        .log();
  }

  public Flux<String> explore_doOnError() {

    return Flux.just("A", "B", "C")
        .concatWith(Flux.error(new IllegalStateException("Unexpected Error Occured")))
        .concatWith(Flux.just("D")) //Workflow didnt reached here since exception was recieved
        .doOnError(ex -> {
          log.error("Exception occured: ", ex);
        })
        .log();
  }

  public Mono<Object> explore_mono_onErrorReturn() {

    return Mono.just("A")
        .map(value -> {
          throw new RuntimeException("Exception Occured");
        })
        .onErrorReturn("abc")
        .log();
  }

  public Flux<String> convertStringToChar(String name) {
    var charArray = name.split("");
    return Flux.fromArray(charArray);
  }

  public Flux<String> convertStringToChar_withDelay(String name) {
    var charArray = name.split("");
    var delay = new Random().nextInt(1000);
    return Flux.fromArray(charArray)
        .delayElements(Duration.ofMillis(delay));
  }

  public Mono<String> namesMono() {
    return Mono.just("alex");
  }

  public Mono<String> namesMono_map_filter(int strLen) {
    return Mono.just("alex")
        .map(String::toUpperCase)
        .filter(s -> s.length() > strLen);
  }

  public Mono<List<String>> namesMono_map_filter_thenFlatMap(int strLen) {
    return Mono.just("alex")
        .map(String::toUpperCase)
        .filter(s -> s.length() > strLen)
        .flatMap(this::convertStringToCharMono)
        .log(); //List.of("A", "L", "E", "X");
  }

  public Flux<String> namesMono_map_filter_thenFlatMapMany(int strLen) {
    return Mono.just("alex")
        .map(String::toUpperCase)
        .filter(s -> s.length() > strLen)
        .flatMapMany(this::convertStringToChar);
    //convert mono to flux
  }

  private Mono<List<String>> convertStringToCharMono(String s) {
    var charArray = s.split("");
    return Mono.just(List.of(charArray));
  }

  public Mono<String> exception_mono_onErrorContinue(String input) {

    return Mono.just(input)
        .map(value -> {
          if (value.equals("abc")) {
            throw new RuntimeException("Exception Occured");
          } else {
            return value;
          }
        })
        .onErrorContinue((ex, value) -> {
          log.info("Exception Occured", ex);
          log.info("value is: {}", value);
        })
        .log();
  }

}
