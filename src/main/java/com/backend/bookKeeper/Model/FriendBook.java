package com.backend.bookKeeper.Model;

import java.util.Date;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.backend.bookKeeper.enums.FriendBookStatus;

import lombok.Data;

@Data
public class FriendBook {
    private ObjectId friendId;
    private ObjectId requesterId;
    private FriendBookStatus status;
    private Date since;
    private Document friendDetails;


    public FriendBook(ObjectId friendId, ObjectId requesterId, FriendBookStatus status, Date since) {
        this.friendId = friendId;
        this.requesterId = requesterId;
        this.status = status;
        this.since = since;
    }

    public Document convertToMongDocument(){
        return new Document()
            .append("friendId", this.friendId)
            .append("requesterId", this.requesterId)
            .append("status", this.status)
            .append("since", this.since);
    }


}





