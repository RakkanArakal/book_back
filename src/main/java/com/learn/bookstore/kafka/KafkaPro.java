package com.learn.bookstore.kafka;

import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

import java.util.Properties;
public class KafkaPro {
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.setProperty("transactional.id", "my-transactional-id");
        KafkaProperties.Producer<String, String> producer = null;
        try {
            producer = new KafkaProducer<String, String>(props, new StringSerializer(), new StringSerializer());
            producer.initTransactions();
            producer.beginTransaction();
            for (int i = 0; i < 100; i++)
                producer.send(new ProducerRecord<>("test", Integer.toString(i), "Message " + Integer.toString(i)));
            producer.commitTransaction();
        } catch (ProducerFencedException e) {
            producer.close();
        } catch (OutOfOrderSequenceException e) {
            producer.close();
        } catch (AuthorizationException e) {
            producer.close();
        } catch (KafkaException e) {
            producer.abortTransaction(); }
        producer.close(); } }///