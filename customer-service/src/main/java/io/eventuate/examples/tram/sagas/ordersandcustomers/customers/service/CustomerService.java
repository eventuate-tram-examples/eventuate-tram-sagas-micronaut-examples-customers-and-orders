package io.eventuate.examples.tram.sagas.ordersandcustomers.customers.service;

import io.eventuate.examples.tram.sagas.ordersandcustomers.commondomain.Money;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.domain.Customer;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.domain.CustomerCreditLimitExceededException;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.messaging.replies.CustomerCreditReservationFailed;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.messaging.replies.CustomerCreditReserved;
import io.eventuate.tram.messaging.common.Message;
import io.micronaut.transaction.annotation.TransactionalAdvice;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

public class CustomerService {

  @PersistenceContext
  private EntityManager entityManager;

  @TransactionalAdvice
  public Customer createCustomer(String name, Money creditLimit) {
    Customer customer  = new Customer(name, creditLimit);
    entityManager.persist(customer);
    return customer;
  }

  public Message reserveCredit(long customerId, Long orderId, Money orderTotal) {
    Optional<Customer> customer = Optional.ofNullable(entityManager.find(Customer.class, customerId));
    try {
      customer
              .orElseThrow(() -> new IllegalArgumentException(String.format("customer with id %s is not found", customerId)))
              .reserveCredit(orderId, orderTotal);
      return withSuccess(new CustomerCreditReserved());
    } catch (CustomerCreditLimitExceededException e) {
      return withFailure(new CustomerCreditReservationFailed());
    }
  }
}
