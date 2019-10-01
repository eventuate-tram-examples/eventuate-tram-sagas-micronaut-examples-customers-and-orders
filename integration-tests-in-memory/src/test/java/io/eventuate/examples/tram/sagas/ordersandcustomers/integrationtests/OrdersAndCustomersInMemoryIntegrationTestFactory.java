package io.eventuate.examples.tram.sagas.ordersandcustomers.integrationtests;

import io.eventuate.tram.messaging.common.ChannelMapping;
import io.eventuate.tram.messaging.common.DefaultChannelMapping;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.inject.Singleton;
import javax.sql.DataSource;

@Factory
public class OrdersAndCustomersInMemoryIntegrationTestFactory {

  @Singleton
  @Primary
  public DataSource dataSource() {
    EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
    return builder.setType(EmbeddedDatabaseType.H2).addScripts("eventuate-tram-embedded-schema.sql", "eventuate-tram-sagas-embedded.sql").build();
  }

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
