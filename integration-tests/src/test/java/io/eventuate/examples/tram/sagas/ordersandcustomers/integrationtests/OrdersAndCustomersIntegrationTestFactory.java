package io.eventuate.examples.tram.sagas.ordersandcustomers.integrationtests;

import io.eventuate.tram.messaging.common.ChannelMapping;
import io.eventuate.tram.messaging.common.DefaultChannelMapping;
import io.micronaut.context.annotation.Factory;

import javax.inject.Singleton;

@Factory
public class OrdersAndCustomersIntegrationTestFactory {
  @Singleton
  public ChannelMapping channelMapping(TramCommandsAndEventsIntegrationData data) {
    return DefaultChannelMapping.builder()
            .with("CustomerAggregate", data.getAggregateDestination())
            .with("customerService", data.getCommandChannel())
            .build();
  }


  @Singleton
  public TramCommandsAndEventsIntegrationData tramCommandsAndEventsIntegrationData() {
    return new TramCommandsAndEventsIntegrationData();
  }
}
