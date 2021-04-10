package io.eventuate.examples.tram.sagas.ordersandcustomers.endtoendtests;

import io.micronaut.context.annotation.Factory;
import org.springframework.web.client.RestTemplate;

import javax.inject.Singleton;

@Factory
public class CustomersAndOrdersE2ETestFactory {

  @Singleton
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
