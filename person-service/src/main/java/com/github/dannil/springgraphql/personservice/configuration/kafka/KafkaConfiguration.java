package com.github.dannil.springgraphql.personservice.configuration.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@EnableKafka
@Configuration
public class KafkaConfiguration {

    @Bean
    public NewTopic personAdded() {
        return TopicBuilder.name(PersonTopic.ADDED).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic personDeleted() {
        return TopicBuilder.name(PersonTopic.DELETED).partitions(3).replicas(1).build();
    }

}
