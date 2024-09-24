package com.backend.bookKeeper.Services;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.backend.bookKeeper.Model.MongoConnect;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;

@Component
public class CounterServices {
    @Autowired
    private MongoConnect mongoConnect;

    public long getNextSequenceValue(String counterId) {
        // Find the document with the counter ID and increment the sequence value by 1
        MongoCollection<Document> collection = this.mongoConnect.getDatabase().getCollection("counters");
        Document counter = collection.find(eq("_id", counterId)).first();
        if (counter == null) {
            initialize(counterId , 0);
        }

        Document updatedCounter = collection.findOneAndUpdate(
                eq("_id", counterId), // Find the document with the given counter ID
                inc("sequence_value", 1), // Increment the sequence_value by 1
                new com.mongodb.client.model.FindOneAndUpdateOptions()
                        .returnDocument(com.mongodb.client.model.ReturnDocument.AFTER) // Return the updated document
        );

        // Return the updated counter value
        if (updatedCounter != null) {
            return updatedCounter.getLong("sequence_value");
        } else {
            throw new RuntimeException("Counter not found for ID: " + counterId);
        }
    }

    // Method to initialize a new counter (if not exists)
    public void initialize(String counterId, long initialValue) {

        MongoCollection<Document> collection = this.mongoConnect.getDatabase().getCollection("counters");
        Document counter = collection.find(eq("_id", counterId)).first();
        if (counter == null) {
            Document newCounter = new Document("_id", counterId)
                    .append("sequence_value", initialValue);
            collection.insertOne(newCounter);
            System.out.println("Initialized counter with ID: " + counterId + " and value: " + initialValue);
        } else {
            System.out.println("Counter already exists with ID: " + counterId);
        }
    }
}
