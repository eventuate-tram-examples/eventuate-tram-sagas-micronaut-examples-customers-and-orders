package io.eventuate.examples.tram.sagas.ordersandcustomers.endtoendtests;

import io.eventuate.examples.tram.sagas.ordersandcustomers.commondomain.Money;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.webapi.CreateCustomerRequest;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.webapi.CreateCustomerResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.commondomain.OrderState;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.webapi.CreateOrderRequest;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.webapi.CreateOrderResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.webapi.GetOrderResponse;
import io.micronaut.context.annotation.Value;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@MicronautTest
public class CustomersAndOrdersE2ETest{

  @Value("${DOCKER_HOST_IP:localhost}")
  private String hostName;

  private String baseUrlOrders(String path) {
    return "http://"+hostName+":8081/" + path;
  }

  private String baseUrlCustomers(String path) {
    return "http://"+hostName+":8082/" + path;
  }

  @Inject
  RestTemplate restTemplate;

  @Test
  public void shouldApprove() throws Exception {
    CreateCustomerResponse createCustomerResponseResponse = restTemplate.postForObject(baseUrlCustomers("customers"),
            new CreateCustomerRequest("Fred", new Money("15.00")), CreateCustomerResponse.class);

    CreateOrderResponse createOrderResponse = restTemplate.postForObject(baseUrlOrders("orders"),
            new CreateOrderRequest(createCustomerResponseResponse.getCustomerId(), new Money("12.34")), CreateOrderResponse.class);

    assertOrderState(createOrderResponse.getOrderId(), OrderState.APPROVED);
  }

  @Test
  public void shouldReject() throws Exception {
    CreateCustomerResponse createCustomerResponseResponse = restTemplate.postForObject(baseUrlCustomers("customers"),
            new CreateCustomerRequest("Fred", new Money("15.00")), CreateCustomerResponse.class);

    CreateOrderResponse createOrderResponse = restTemplate.postForObject(baseUrlOrders("orders"),
            new CreateOrderRequest(createCustomerResponseResponse.getCustomerId(), new Money("123.40")), CreateOrderResponse.class);

    assertOrderState(createOrderResponse.getOrderId(), OrderState.REJECTED);
  }

  @Test
  public void testSwaggerUiUrls() throws IOException {
    testSwaggerUiUrl(8081, "swagger-ui/index.html");
    testSwaggerUiUrl(8082, "swagger-ui/index.html");
  }

  private void testSwaggerUiUrl(int port, String relativeUrl) throws IOException {
    assertUrlStatusIsOk(String.format("http://%s:%s/%s", hostName, port, relativeUrl));
  }

  private void assertUrlStatusIsOk(String url) throws IOException {
    HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();

    Assert.assertEquals(200, connection.getResponseCode());
  }

  private void assertOrderState(Long id, OrderState expectedState) throws InterruptedException {
    GetOrderResponse order = null;
    for (int i = 0; i < 30; i++) {
      ResponseEntity<GetOrderResponse> getOrderResponseEntity = restTemplate.getForEntity(baseUrlOrders("orders/" + id), GetOrderResponse.class);
      Assert.assertEquals(HttpStatus.OK, getOrderResponseEntity.getStatusCode());
      order = getOrderResponseEntity.getBody();
      if (order.getOrderState() == expectedState)
        break;
      TimeUnit.MILLISECONDS.sleep(400);
    }

    Assert.assertEquals(expectedState, order.getOrderState());
  }
}
