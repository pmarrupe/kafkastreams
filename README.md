# Kafka-Streams:

1. The main class listens on the topic: `TweetHashTags1` and writes the output to `googlehashtags`.

2. The incoming data is a list of hash tags with hashtag-count.

ex: `2 #Google`  where 2 is the count of the hashtag #google.

3. The key is `2` and value is `2 #Google`

4. If the count is great than 1 and hashtag is of google only then it produces the data to the topic: `googlehashtags`.

5. To consume on the `googlehashtags` topic run this command:

`bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic googlehashtags`

(p.s assuming you have kafka and zookeeper running locally).

6. Before running this make sure to run the spark-kafka-connector to get the data onto the TweetHashTags1.

You can find it here: 

https://github.com/pmarrupe/kafkaconnector

This writes to two topics: 

- `tweet.data` topic will have all the tweet json.
- `TwitterHashTags1` topic will have all the hashtag counts.

