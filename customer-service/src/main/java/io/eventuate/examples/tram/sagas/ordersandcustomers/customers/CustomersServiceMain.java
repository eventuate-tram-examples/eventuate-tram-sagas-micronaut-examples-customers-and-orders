package io.eventuate.examples.tram.sagas.ordersandcustomers.customers;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;

@OpenAPIDefinition
public class CustomersServiceMain {
  public static void main(String[] args) {
    Micronaut.run(CustomersServiceMain.class);
  }
}
