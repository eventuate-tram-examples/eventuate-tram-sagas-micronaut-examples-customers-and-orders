package io.eventuate.examples.tram.sagas.ordersandcustomers.messaging.createorder;

import io.eventuate.examples.tram.sagas.ordersandcustomers.messaging.common.OrderDetails;

public class CreateOrderSagaData  {

  private Long orderId;

  private OrderDetails orderDetails;

  public CreateOrderSagaData() {
  }

  public CreateOrderSagaData(OrderDetails orderDetails) {
    this.orderDetails = orderDetails;
  }

  public Long getOrderId() {
    return orderId;
  }

  public OrderDetails getOrderDetails() {
    return orderDetails;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }
}
