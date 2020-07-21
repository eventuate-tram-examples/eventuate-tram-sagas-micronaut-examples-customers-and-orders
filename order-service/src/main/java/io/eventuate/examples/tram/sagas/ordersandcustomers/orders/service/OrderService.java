package io.eventuate.examples.tram.sagas.ordersandcustomers.orders.service;

import io.eventuate.examples.tram.sagas.ordersandcustomers.commondomain.Money;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.messaging.commands.ReserveCreditCommand;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.Order;
import io.eventuate.examples.tram.sagas.ordersandcustomers.messaging.createorder.CreateOrderSagaData;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.events.publisher.ResultWithEvents;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

public class OrderService {

  @PersistenceContext
  private EntityManager entityManager;

  public void createOrder(CreateOrderSagaData data) {
    ResultWithEvents<Order> oe = Order.createOrder(data.getOrderDetails());
    Order order = oe.result;
    entityManager.persist(order);
    data.setOrderId(order.getId());
  }

  public CommandWithDestination reserveCredit(long orderId, Long customerId, Money orderTotal) {
    return send(new ReserveCreditCommand(customerId, orderId, orderTotal))
            .to("customerService")
            .build();
  }

  public void approveOrder(long orderId) {
    entityManager.find(Order.class, orderId).noteCreditReserved();
  }

  public void rejectOrder(long orderId) {
    entityManager.find(Order.class, orderId).noteCreditReservationFailed();
  }

}
