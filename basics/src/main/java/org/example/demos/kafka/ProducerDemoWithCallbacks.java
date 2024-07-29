package org.example.demos.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithCallbacks {
     private static final Logger logger = LoggerFactory.getLogger(ProducerDemoWithCallbacks.class.getSimpleName());

     public static void main(String[] args) {
         logger.info("I'm a Kafka Producer with callbacks!");

         // Create producer properties
         Properties producerConfig = new Properties();

         producerConfig.setProperty("bootstrap.servers", "127.0.0.1:9092");
         producerConfig.setProperty("key.serializer", StringSerializer.class.getName());
         producerConfig.setProperty("value.serializer", StringSerializer.class.getName());
         // producerConfig.setProperty("batch.size", "400");

         // Create the producer
         KafkaProducer<String, String> producer = new KafkaProducer<>(producerConfig);

         for (int j=0; j<10; j++) {
             for (int i=0; i<30; i++) {
                 // Create a producer record
                 ProducerRecord<String, String> producerRecord = new ProducerRecord<>("demo_topic", "hello world " + i);

                 // Send data
                 producer.send(producerRecord, new Callback() {
                     @Override
                     public void onCompletion(RecordMetadata metadata, Exception e) {
                         if (e == null) {
                             // success
                             logger.info("Produced successfully! \n" +
                                     "Topic: " + metadata.topic() + "\n" +
                                     "Partition: " + metadata.partition() + "\n" +
                                     "Offset: " + metadata.offset() + "\n" +
                                     "Timestamp: " + metadata.timestamp());
                         } else {
                             logger.error("Error while producing" + e);
                         }
                     }
                 });
             }

             try {
                 Thread.sleep(500);
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
         }

         // Flush the producer
         producer.flush();

         // Close the producer (also runs flush() under the hood)
         producer.close();
     }
}
