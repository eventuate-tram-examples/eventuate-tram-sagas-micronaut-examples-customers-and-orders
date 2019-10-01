package io.eventuate.examples.tram.sagas.ordersandcustomers.integrationtests;

import io.micronaut.test.annotation.MicronautTest;

@MicronautTest(transactional = false)
public class OrdersAndCustomersInMemoryIntegrationTest extends AbstractOrdersAndCustomersIntegrationTest {
}
