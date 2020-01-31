package io.eventuate.examples.tram.sagas.ordersandcustomers.integrationtests;

import io.eventuate.examples.tram.sagas.ordersandcustomers.commondomain.Money;
import io.eventuate.examples.tram.sagas.ordersandcustomers.commondomain.OrderState;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.domain.Customer;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.service.CustomerService;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.Order;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.common.OrderDetails;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionTemplate;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public abstract class AbstractOrdersAndCustomersIntegrationTest {

  @Inject
  private CustomerService customerService;

  @Inject
  private OrderService orderService;

  @PersistenceContext
  private EntityManager entityManager;

  @Inject
  private TransactionTemplate transactionTemplate;

  @Test
  public void shouldApproveOrder() throws InterruptedException {
    Money creditLimit = new Money("15.00");
    Customer customer = customerService.createCustomer("Fred", creditLimit);
    Order order = orderService.createOrder(new OrderDetails(customer.getId(), new Money("12.34")));

    assertOrderState(order.getId(), OrderState.APPROVED);
  }

  @Test
  public void shouldRejectOrder() throws InterruptedException {
    Money creditLimit = new Money("15.00");
    Customer customer = customerService.createCustomer("Fred", creditLimit);
    Order order = orderService.createOrder(new OrderDetails(customer.getId(), new Money("123.40")));

    assertOrderState(order.getId(), OrderState.REJECTED);
  }

  private void assertOrderState(Long id, OrderState expectedState) throws InterruptedException {
    Order order = null;
    for (int i = 0; i < 30; i++) {
      order = transactionTemplate
              .execute(s -> Optional.ofNullable(entityManager.find(Order.class, id)))
              .orElseThrow(() -> new IllegalArgumentException(String.format("Order with id %s is not found", id)));
      if (order.getState() == expectedState)
        break;
      TimeUnit.MILLISECONDS.sleep(400);
    }

    Assertions.assertEquals(expectedState, order.getState());
  }
}
