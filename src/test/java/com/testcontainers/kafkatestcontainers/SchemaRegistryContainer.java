package com.testcontainers.kafkatestcontainers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import static com.testcontainers.kafkatestcontainers.KafkaTestcontainersInitializer.CONFLUENT_PLATFORM_VERSION;


public class SchemaRegistryContainer extends GenericContainer<SchemaRegistryContainer> {


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
