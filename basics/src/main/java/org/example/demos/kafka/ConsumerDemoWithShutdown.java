package org.example.demos.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerDemoWithShutdown {
     private static final Logger logger = LoggerFactory.getLogger(ConsumerDemoWithShutdown.class.getSimpleName());

     static String groupId = "demo_consumer";
     static String topic = "demo_topic";

     public static void main(String[] args) {
         logger.info("I'm a Kafka Consumer!");

         // Create consumer properties
         Properties consumerConfig = new Properties();

         consumerConfig.setProperty("bootstrap.servers", "127.0.0.1:9092");
         consumerConfig.setProperty("key.deserializer", StringDeserializer.class.getName());
         consumerConfig.setProperty("value.deserializer", StringDeserializer.class.getName());
         consumerConfig.setProperty("group.id", groupId);
         consumerConfig.setProperty("auto.offset.reset", "earliest");

         // Create a consumer
         KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerConfig);

         // Setup graceful shutdown
         final Thread mainThread = Thread.currentThread();
         Runtime.getRuntime().addShutdownHook(new Thread() {
             public void run() {
                 logger.info("Shutdown detected, let's exit");
                 consumer.wakeup();

                 // join the main thread to allow the execution of the code in the main thread
                 try {
                     mainThread.join();
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }
             }
         });

         try {
             // Subscribe to a topic
             consumer.subscribe(Arrays.asList(topic));
             // Poll for data
             while (true) {
                 ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

                 for (ConsumerRecord<String, String> record: records) {
                     logger.info("Key: " + record.key() + ". Value: " + record.value() + ".");
                     logger.info("Partition: " + record.partition() + ". Offset: " + record.offset() + ".");
                 }
             }
         } catch (WakeupException e) {
             logger.info("Consumer is starting to shut down.");
         } catch (Exception e) {
             logger.error("Unexpected error: " + e);
         } finally {
             consumer.close();
             logger.info("The consumer is now gracefully shut down.");
         }
     }
}
