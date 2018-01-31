import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Predicate;

public class KStreams {

    public static void main(final String[] args) throws Exception {
        final String bootstrapServers = args.length > 0 ? args[0] : "localhost:9092";
        final Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "test1");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        streamsConfiguration.put(StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG, "org.apache.kafka.streams.processor.WallclockTimestampExtractor");

        streamsConfiguration.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);

        streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);

        final Serializer<String> jsonSerializer = new StringSerializer();
        // load a simple json deserializer
        final Deserializer<String> jsonDeserializer = new StringDeserializer();

        final Serde<String> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

        // Setup a string Serde for the key portion of the messages
        final Serde<String> stringSerde = Serdes.String();

        final KStreamBuilder builder = new KStreamBuilder();

        KStream<String, String> source = builder.stream(stringSerde, jsonSerde, "TweetHashTags1");

        Predicate<String, String> isFirstHost = (k, v) ->
                v.contains("#Google") && Long.valueOf(k)>1;

        KStream<String, String> firstHost = source.filter(isFirstHost);


        firstHost.to(stringSerde, jsonSerde, "googlehashtags");
        final KafkaStreams streams = new KafkaStreams(builder, streamsConfiguration);
        streams.cleanUp();
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

}