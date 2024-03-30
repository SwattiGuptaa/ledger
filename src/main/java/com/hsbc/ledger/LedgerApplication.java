package com.hsbc.ledger;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Collections;

import static com.hsbc.ledger.util.Constants.*;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(description = "Open API documentation for Ledger Service"))
public class LedgerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LedgerApplication.class, args);
	}

	@Bean
	public SimpleMessageConverter converter() {
		SimpleMessageConverter converter = new SimpleMessageConverter();
		converter.setAllowedListPatterns(Collections.singletonList("com.hsbc.ledger.*"));
		return converter;
	}

	@Bean
	public Declarables topicBindings() {
		Queue queue = new Queue(WALLET_POSTING_QUEUE, false);
		Queue queryQueue = new Queue(POSTING_QUERY_QUEUE, false);

		TopicExchange topicExchange = new TopicExchange("posting.exchange");

		return new Declarables(
				queue,
				queryQueue,
				topicExchange,
				BindingBuilder
						.bind(queue)
						.to(topicExchange).with(WALLET_POSTING_ROUTING_KEY),
				BindingBuilder
						.bind(queryQueue)
						.to(topicExchange).with(POSTING_QUERY_ROUTING_KEY));
	}

	@Bean
	public Declarables fanoutBindings() {
		// queues and exchange related to client1
		Queue fanoutQueue1 = new Queue("client1.queue1", false);
		Queue fanoutQueue2 = new Queue("client1.queue2", false);
		FanoutExchange fanoutExchange = new FanoutExchange("client1.fanout.exchange");

		return new Declarables(
				fanoutQueue1,
				fanoutQueue2,
				fanoutExchange,
				BindingBuilder.bind(fanoutQueue1).to(fanoutExchange),
				BindingBuilder.bind(fanoutQueue2).to(fanoutExchange));
	}

}
