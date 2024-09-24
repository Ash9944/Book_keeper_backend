package com.backend.bookKeeper.Model;

import java.util.Date;

import org.bson.Document;

import com.backend.bookKeeper.enums.TransactionTypes;

public class Transactions {
    private final long id;
    private final TransactionTypes type;
    private final long amount;
    private final Date date;
    private final String description;
    private final Document from;
    private final Document to;

    public Transactions(long id, TransactionTypes type, long amount, Date date, String description, Document from, Document to) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.from = from;
        this.to = to;
    }

    public Document convertToMongDocument() {
        return new Document()
            .append("id", this.id)
            .append("type", this.type)
            .append("amount", this.amount)
            .append("date", this.date)
            .append("description", this.description)
            .append("from", this.from)
            .append("to", this.to);
    }
    
}
