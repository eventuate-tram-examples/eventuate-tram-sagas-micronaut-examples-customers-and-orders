package io.eventuate.examples.tram.sagas.ordersandcustomers.orders.web;

import io.eventuate.examples.tram.sagas.ordersandcustomers.messaging.common.OrderDetails;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.Order;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.service.OrderSagaService;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.webapi.CreateOrderRequest;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.webapi.CreateOrderResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.webapi.GetOrderResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.spring.tx.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Controller
public class OrderController {

  @Inject
  private OrderSagaService orderSagaService;

  @PersistenceContext
  private EntityManager entityManager;

  @Post(value = "/orders")
  public CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest) {
    Order order = orderSagaService.createOrder(new OrderDetails(createOrderRequest.getCustomerId(), createOrderRequest.getOrderTotal()));
    return new CreateOrderResponse(order.getId());
  }

  @Get("/orders/{orderId}")
  @Transactional
  public HttpResponse<GetOrderResponse> getOrder(Long orderId) {
    return Optional.ofNullable(entityManager.find(Order.class, orderId))
            .map(order -> HttpResponse.ok(new GetOrderResponse(order.getId(), order.getState())))
            .orElseGet(HttpResponse::notFound);
  }
}
