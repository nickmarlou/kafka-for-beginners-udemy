package org.example.demos.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithKeys {
     private static final Logger logger = LoggerFactory.getLogger(ProducerDemoWithKeys.class.getSimpleName());

     public static void main(String[] args) {
         logger.info("I'm a Kafka Producer with callbacks!");

         // Create producer properties
         Properties producerConfig = new Properties();

         producerConfig.setProperty("bootstrap.servers", "127.0.0.1:9092");
         producerConfig.setProperty("key.serializer", StringSerializer.class.getName());
         producerConfig.setProperty("value.serializer", StringSerializer.class.getName());

         // Create the producer
         KafkaProducer<String, String> producer = new KafkaProducer<>(producerConfig);

         for (int j=0; j<3; j++) {
             for (int i=0; i<10; i++) {
                 String topic = "demo_topic";
                 String key = "id_" + i;
                 String value = "hello world " + i;

                 // Create a producer record
                 ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, value);

                 // Send data
                 producer.send(producerRecord, new Callback() {
                     @Override
                     public void onCompletion(RecordMetadata metadata, Exception e) {
                         if (e == null) {
                             // success
                             logger.info("Key: " + key + ". Partition: " + metadata.partition() + ".");
                         } else {
                             logger.error("Error while producing" + e);
                         }
                     }
                 });
             }
         }

         // Flush the producer
         producer.flush();

         // Close the producer (also runs flush() under the hood)
         producer.close();
     }
}
