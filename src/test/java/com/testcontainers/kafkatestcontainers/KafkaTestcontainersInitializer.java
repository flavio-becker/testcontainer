package com.testcontainers.kafkatestcontainers;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

public class KafkaTestcontainersInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static final Network NETWORK = Network.newNetwork();
    public static final String CONFLUENT_PLATFORM_VERSION = "7.4.1";
    public static final DockerImageName KAFKA_IMAGE =
            DockerImageName.parse("confluentinc/cp-kafka").withTag(CONFLUENT_PLATFORM_VERSION);

    public static final KafkaContainer KAFKA = new KafkaContainer(KAFKA_IMAGE)
            .withNetwork(NETWORK)
            .withKraft()
            .withEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", "1")
            .withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", "1");

    public static final SchemaRegistryContainer SCHEMA_REGISTRY =
            new SchemaRegistryContainer(CONFLUENT_PLATFORM_VERSION)
                    .withStartupTimeout(Duration.ofMinutes(2));


    public static class SchemaRegistryContainer extends GenericContainer<SchemaRegistryContainer> {
        public static final String SCHEMA_REGISTRY_IMAGE = "confluentinc/cp-schema-registry";
        public static final int SCHEMA_REGISTRY_PORT = 8081;

        public SchemaRegistryContainer() {
            this(CONFLUENT_PLATFORM_VERSION);
        }

        public SchemaRegistryContainer(String version) {
            super(DockerImageName.parse(SCHEMA_REGISTRY_IMAGE).withTag(CONFLUENT_PLATFORM_VERSION));

            waitingFor(Wait.forHttp("/subjects").forStatusCode(200));
            withExposedPorts(SCHEMA_REGISTRY_PORT);
        }

        public SchemaRegistryContainer withKafka(KafkaContainer kafka) {
            return withKafka(kafka.getNetwork(), kafka.getNetworkAliases().get(0) + ":9092");
        }

        public SchemaRegistryContainer withKafka(Network network, String bootstrapServers) {
            withNetwork(network);
            withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry");
            withEnv(
                    "SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS",
                    "PLAINTEXT://" + bootstrapServers);
            return self();
        }

        public String getSchemaUrl() {
            return String.format("http://%s:%d", getHost(), getMappedPort(SCHEMA_REGISTRY_PORT));
        }
    }

    static {
        Startables.deepStart(KAFKA).join();

        SCHEMA_REGISTRY.withKafka(KAFKA).start();
        // Should be set after container is started
        SCHEMA_REGISTRY.withEnv("SCHEMA_REGISTRY_LISTENERS", SCHEMA_REGISTRY.getSchemaUrl());

    }

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        TestPropertyValues.of(

                //KAFKACONFIGURATION PROPERTIES
                "spring.kafka.bootstrap-servers=" + KAFKA.getBootstrapServers(),
                "spring.kafka.bootstrap-servers=" + KAFKA.getBootstrapServers(),
                "spring.kafka.consumer.bootstrap-servers=" + KAFKA.getBootstrapServers(),
                "spring.kafka.producer.bootstrap-servers=" + KAFKA.getBootstrapServers(),
                "spring.kafka.producer.properties.schema.registry.url=" + SCHEMA_REGISTRY.getSchemaUrl(),
                "spring.kafka.consumer.properties.schema.registry.url=" + SCHEMA_REGISTRY.getSchemaUrl(),
                "spring.kafka.properties.schema.registry.url=" + SCHEMA_REGISTRY.getSchemaUrl()

        ).applyTo(ctx.getEnvironment());
    }
}
