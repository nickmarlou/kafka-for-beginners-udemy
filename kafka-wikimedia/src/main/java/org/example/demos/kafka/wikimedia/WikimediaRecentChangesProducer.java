package org.example.demos.kafka.wikimedia;

import com.launchdarkly.eventsource.ConnectStrategy;
import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.StreamException;
import com.launchdarkly.eventsource.background.BackgroundEventHandler;
import com.launchdarkly.eventsource.background.BackgroundEventSource;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class WikimediaRecentChangesProducer {
    private static final Logger logger = LoggerFactory.getLogger(WikimediaRecentChangesProducer.class.getSimpleName());

    public static void main(String[] args) throws InterruptedException, StreamException {
        logger.info("Starting Wikimedia producer...");

        KafkaProducer<String, String> producer = getProducer();

        // Create an event handler
        String topic = "kafka_wikimedia__recent_changes";

        BackgroundEventHandler eventHandler = new WikimediaChangeHandler(kafkaProducer, topic);

        String sourceUrl = "https://stream.wikimedia.org/v2/stream/recentchange";
        BackgroundEventSource bes = new BackgroundEventSource.Builder(
                eventHandler,
                new EventSource.Builder(URI.create(sourceUrl))
        )
                .threadPriority(Thread.MAX_PRIORITY)
                .build();

        bes.start();

        //
        TimeUnit.MINUTES.sleep(5);
    }

    private static KafkaProducer<String, String> getProducer() {
        String bootstrapServers = "127.0.0.1:9092";

        Properties producerConfig = new Properties();
        producerConfig.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerConfig.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerConfig.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return new KafkaProducer<String, String>(producerConfig);
    }
}
