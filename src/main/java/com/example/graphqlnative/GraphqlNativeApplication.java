package com.example.graphqlnative;

import graphql.GraphQL;
import graphql.analysis.QueryVisitorFieldArgumentEnvironment;
import graphql.analysis.QueryVisitorFieldArgumentInputValue;
import graphql.execution.Execution;
import graphql.execution.nextgen.result.RootExecutionResultNode;
import graphql.language.*;
import graphql.parser.ParserOptions;
import graphql.schema.*;
import graphql.schema.validation.SchemaValidationErrorCollector;
import graphql.util.NodeAdapter;
import graphql.util.NodeZipper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.NativeDetector;
import org.springframework.core.io.ClassPathResource;
import org.springframework.graphql.boot.GraphQlSourceBuilderCustomizer;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.nativex.hint.ResourceHint;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.springframework.nativex.hint.TypeAccess.*;

@TypeHint(
	typeNames = {
		"graphql.analysis.QueryTraversalContext",
		"graphql.schema.idl.SchemaParseOrder",
	},
	types = {

		Argument.class,
		ArrayValue.class,
		Boolean.class,
		BooleanValue.class,
		Customer.class,
		DataFetchingEnvironment.class,
		Directive.class,
		DirectiveDefinition.class,
		DirectiveLocation.class,
		Document.class,
		EnumTypeDefinition.class,
		EnumTypeExtensionDefinition.class,
		EnumValue.class,
		EnumValueDefinition.class,
		Execution.class,
		Field.class,
		FieldDefinition.class,
		FloatValue.class,
		FragmentDefinition.class,
		FragmentSpread.class,
		GraphQL.class,
		GraphQLArgument.class,
		GraphQLCodeRegistry.Builder.class,
		GraphQLDirective.class,
		GraphQLEnumType.class,
		GraphQLEnumValueDefinition.class,
		GraphQLFieldDefinition.class,
		GraphQLInputObjectField.class,
		GraphQLInputObjectType.class,
		GraphQLInterfaceType.class,
		GraphQLList.class,
		GraphQLNamedType.class,
		GraphQLNonNull.class,
		GraphQLObjectType.class,
		GraphQLOutputType.class,
		GraphQLScalarType.class,
		GraphQLSchema.class,
		GraphQLSchemaElement.class,
		GraphQLUnionType.class,
		ImplementingTypeDefinition.class,
		InlineFragment.class,
		InputObjectTypeDefinition.class,
		InputObjectTypeExtensionDefinition.class,
		InputValueDefinition.class,
		IntValue.class,
		InterfaceTypeDefinition.class,
		InterfaceTypeExtensionDefinition.class,
		List.class,
		ListType.class,
		NodeAdapter.class,
		NodeZipper.class,
		NonNullType.class,
		NullValue.class,
		ObjectField.class,
		ObjectTypeDefinition.class,
		ObjectTypeExtensionDefinition.class,
		ObjectValue.class,
		OperationDefinition.class,
		OperationTypeDefinition.class,
		ParserOptions.class,
		QueryVisitorFieldArgumentEnvironment.class,
		QueryVisitorFieldArgumentInputValue.class,
		RootExecutionResultNode.class,
		ScalarTypeDefinition.class,
		ScalarTypeExtensionDefinition.class,
		SchemaDefinition.class,
		SchemaExtensionDefinition.class,
		SchemaValidationErrorCollector.class,
		SelectionSet.class,
		StringValue.class,
		TypeDefinition.class,
		TypeName.class,
		UnionTypeDefinition.class,
		UnionTypeExtensionDefinition.class,
		VariableDefinition.class,
		VariableReference.class,

		// new
	}, //
	access = {//
		PUBLIC_CLASSES, PUBLIC_CONSTRUCTORS, PUBLIC_FIELDS, PUBLIC_METHODS,
		DECLARED_CLASSES, DECLARED_CONSTRUCTORS, DECLARED_FIELDS, DECLARED_METHODS
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
			// this part isn't great, but:
			// right now the PatternResourceResolver used to 'find' the schema files on the classpath fails because
			// in the graalvm native image world there is NO classpath to speak of. So this code manually
			// adds a Resource, knowing that the default resolution logic fail. But, it only does so in
			// a GraalVM native image context
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