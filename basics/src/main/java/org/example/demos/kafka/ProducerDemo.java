package org.example.demos.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemo {
     private static final Logger logger = LoggerFactory.getLogger(ProducerDemo.class.getSimpleName());

     public static void main(String[] args) {
         logger.info("I'm a Kafka Producer!");

         // Create producer properties
         Properties producerConfig = new Properties();

         producerConfig.setProperty("bootstrap.servers", "127.0.0.1:9092");
         producerConfig.setProperty("key.serializer", StringSerializer.class.getName());
         producerConfig.setProperty("value.serializer", StringSerializer.class.getName());

         // Create the producer
         KafkaProducer<String, String> producer = new KafkaProducer<>(producerConfig);

         // Create a producer record
         ProducerRecord<String, String> producerRecord = new ProducerRecord<>("demo_topic", "hello world!");

         // Send data
         producer.send(producerRecord);

         // Flush the producer
         producer.flush();

         // Close the producer (also runs flush() under the hood)
         producer.close();
     }
}
