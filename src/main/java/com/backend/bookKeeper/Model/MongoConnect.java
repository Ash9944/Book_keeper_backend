package com.backend.bookKeeper.Model;

import org.springframework.stereotype.Component;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

@Component
public class MongoConnect {
    final String uri = "mongodb+srv://ash99449:9944902340@cluster0.gjs7tw8.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
    final String dataBaseName = "book_keeper";
    private MongoDatabase database;

    public MongoConnect() {
        try {
            // Create a new client and connect to the server
            MongoClient mongoClient = MongoClients.create(uri);
            this.database = mongoClient.getDatabase(this.dataBaseName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    

    public MongoDatabase getDatabase() {
        return this.database;
    }
}
