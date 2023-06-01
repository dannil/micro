package com.github.dannil.demo.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@EnableKafka
@Configuration
public class KafkaConfiguration {

    public static final String PERSONS_TOPIC = "persons";

    @Bean
    public NewTopic persons() {
        return TopicBuilder.name(PERSONS_TOPIC).partitions(5).replicas(1).build();
    }

}
