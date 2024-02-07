package com.testcontainers.adapters.listeners;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;

import java.util.Map;

public class Utf8StringAvroDeserializer implements org.apache.kafka.common.serialization.Deserializer<GenericRecord> {
    private final KafkaAvroDeserializer avroDeserializer = new KafkaAvroDeserializer();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        avroDeserializer.configure(configs, isKey);
    }

    @Override
    public GenericRecord deserialize(String topic, byte[] data) {
        GenericRecord record = (GenericRecord) avroDeserializer.deserialize(topic, data);
        converterUtf8ParaStringRecursivamente(record);
        return record;
    }

    @Override
    public void close() {
        avroDeserializer.close();
    }

    private void converterUtf8ParaStringRecursivamente(GenericRecord registro) {
        for (org.apache.avro.Schema.Field campo : registro.getSchema().getFields()) {
            Object valorCampo = registro.get(campo.name());
            if (valorCampo instanceof Utf8) {
                registro.put(campo.name(), valorCampo.toString());
            } else if (valorCampo instanceof GenericRecord) {
                converterUtf8ParaStringRecursivamente((GenericRecord) valorCampo);
            }
        }
    }
}
