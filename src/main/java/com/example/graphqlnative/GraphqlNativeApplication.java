package com.example.graphqlnative;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.NativeDetector;
import org.springframework.core.io.ClassPathResource;
import org.springframework.graphql.boot.GraphQlSourceBuilderCustomizer;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.ResourceHint;
import org.springframework.nativex.hint.TypeAccess;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@TypeHint(
	types ={
		Customer.class,
		Boolean.class
	}, //
	access = {//
		TypeAccess.QUERY_DECLARED_CONSTRUCTORS, TypeAccess.QUERY_DECLARED_METHODS, TypeAccess.QUERY_PUBLIC_CONSTRUCTORS, TypeAccess.QUERY_PUBLIC_METHODS,
		TypeAccess.DECLARED_CLASSES, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.DECLARED_FIELDS, TypeAccess.DECLARED_FIELDS
	} //
)
@ResourceHint(patterns = {"graphql/schema.graphqls", "graphiql/index.html"})
@SpringBootApplication
public class GraphqlNativeApplication {

	public static void main(String[] args) {
		SpringApplication.run(GraphqlNativeApplication.class, args);
	}

	@Bean
	GraphQlSourceBuilderCustomizer graphQlSourceBuilderCustomizer() {
		return builder -> {
			if (NativeDetector.inNativeImage())
				builder.schemaResources(new ClassPathResource("graphql/schema.graphqls"));
		};
	}
}

@Controller
class CustomerGraphQlController {

	private final Map<Integer, Customer> db;

	CustomerGraphQlController() {
		var ctr = new AtomicInteger();
		this.db = List
			.of("Tammie", "Kimly", "Josh", "Peanut")
			.stream()
			.map(name -> new Customer(ctr.incrementAndGet(), name))
			.collect(Collectors.toMap(Customer::id, customer -> customer));

	}

	@QueryMapping
	Flux<Customer> customers() {
		return Flux.fromIterable(this.db.values());
	}
}

record Customer(Integer id, String name) {
}