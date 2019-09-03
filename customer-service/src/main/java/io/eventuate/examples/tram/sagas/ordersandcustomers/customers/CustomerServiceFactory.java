package io.eventuate.examples.tram.sagas.ordersandcustomers.customers;

import io.eventuate.tram.messaging.common.ChannelMapping;
import io.eventuate.tram.messaging.common.DefaultChannelMapping;
import io.micronaut.context.annotation.Factory;

import javax.inject.Singleton;

@Factory
public class CustomerServiceFactory {
  @Singleton
  public ChannelMapping channelMapping() {
    return DefaultChannelMapping.builder().build();
  }
}
