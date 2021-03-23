package io.eventuate.examples.tram.sagas.ordersandcustomers.orders.service;

import io.eventuate.examples.tram.sagas.ordersandcustomers.messaging.createorder.CreateOrderSagaData;
import io.eventuate.examples.tram.sagas.ordersandcustomers.messaging.common.OrderDetails;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.Order;
import io.eventuate.tram.sagas.orchestration.SagaManager;
import io.micronaut.spring.tx.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


public class OrderSagaService {

  @Inject
  private SagaManager<CreateOrderSagaData> createOrderSagaManager;

  @PersistenceContext
  private EntityManager entityManager;

  @Transactional
  public Order createOrder(OrderDetails orderDetails) {
    CreateOrderSagaData data = new CreateOrderSagaData(orderDetails);
    createOrderSagaManager.create(data);
    return entityManager.find(Order.class, data.getOrderId());
  }
}
