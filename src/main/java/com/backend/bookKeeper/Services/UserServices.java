package com.backend.bookKeeper.Services;

import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.bookKeeper.Model.Dao;
import com.backend.bookKeeper.Model.FriendBook;
import com.backend.bookKeeper.enums.FriendBookStatus;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

@Service
public class UserServices {
    @Autowired
    private Dao dao;

    public List<Document> getAllusers(String userId) {
        ObjectId userObjectId = new ObjectId(userId);
        Bson friendsQuery = Filters.and(Filters.eq("requesterId", userObjectId));
        List<ObjectId> friendsOfUser = dao.distinctObjectId("friendId", friendsQuery, "friendsBook");

        Bson requestQuery = Filters.and(Filters.eq("friendId", userObjectId));
        List<ObjectId> requestsOfUser = dao.distinctObjectId("requesterId", requestQuery, "friendsBook");

        friendsOfUser.addAll(requestsOfUser);

        Bson filter = Filters.and(
                Filters.ne("_id", userObjectId),
                Filters.nin("_id", friendsOfUser));
        Bson projection = Filters.empty();
        List<Document> response = dao.findByQuery(filter, projection, "users");
        int responseLength = response.size();

        for (int i = 0; i < responseLength; i++) {
            Document item = response.get(i);
            Object userObject = item.get("_id");
            item.put("_id", userObject.toString());
        }

        return response;
    }

    public Document getUserDetails(String userId) {
        Bson filter = Filters.and(
                Filters.eq("userId", userId));
        Bson projection = Filters.empty();
        List<Document> response = dao.findByQuery(filter, projection, "users");
        int responseLength = response.size();
        if (responseLength == 0) {
            return new Document("Failed", "No User Found");
        }

        Document user = response.get(0);
        Object userObjectId = user.get("_id");
        user.put("_id", userObjectId.toString());

        return user;
    }

    public Boolean addFriend(String userId, String friendId) {
        ObjectId friendObjectId = new ObjectId(friendId);
        ObjectId userObjectId = new ObjectId(userId);
        Bson findOneProjection = Filters.empty();

        Document friend = dao.findOne(friendObjectId, findOneProjection, "users");
        Document user = dao.findOne(userObjectId, findOneProjection, "users");

        Date currentDate = new Date();
        FriendBook friendBook = new FriendBook(
                friend.getObjectId("_id"),
                user.getObjectId("_id"),
                FriendBookStatus.PENDING,
                currentDate);

        Document friendBookDocument = friendBook.convertToMongDocument();
        dao.createOne(friendBookDocument, "friendsBook");
        return true;
    }

    public Boolean acceptFriend(String userId, String friendId) {
        ObjectId friendObjectId = new ObjectId(friendId);
        ObjectId userObjectId = new ObjectId(userId);
        Bson findOneProjection = Filters.empty();

        Document friend = dao.findOne(friendObjectId, findOneProjection, "users");
        Document user = dao.findOne(userObjectId, findOneProjection, "users");

        Document filter = new Document("friendId", userObjectId).append("requesterId", friendObjectId);
        Document update = new Document("status", FriendBookStatus.ACCEPTED);
        dao.updateOneByQuery(filter, "SET", update, "friendsBook");

        Date currentDate = new Date();
        FriendBook friendBook = new FriendBook(
                friend.getObjectId("_id"),
                user.getObjectId("_id"),
                FriendBookStatus.ACCEPTED,
                currentDate);
        Document friendBookDocument = friendBook.convertToMongDocument();
        dao.createOne(friendBookDocument, "friendsBook");

        return true;
    }

    public Boolean rejectFriend(String userId, String friendId) {
        ObjectId friendObjectId = new ObjectId(friendId);
        ObjectId userObjectId = new ObjectId(userId);
        Bson findOneProjection = Filters.empty();

        Document friend = dao.findOne(friendObjectId, findOneProjection, "users");
        Document user = dao.findOne(userObjectId, findOneProjection, "users");

        int isFriend = friend.size();
        int isUser = user.size();
        if (isFriend == 0 || isUser == 0) {
            return false;
        }

        Document userFilter = new Document("friendId", userObjectId).append("requestedId", friendObjectId);
        Document friendFilter = new Document("friendId", friendObjectId).append("requestedId", userObjectId);
        Document update = new Document("status", FriendBookStatus.REJECTED);

        dao.updateOneByQuery(friendFilter, "SET", update, "friendsBook");
        dao.updateOneByQuery(userFilter, "SET", update, "friendsBook");
        return true;
    }

    public List<Document> getFriends(String userId) {
        ObjectId userObjectId = new ObjectId(userId);
        Bson findOneProjection = fields(include("customUsers.name", "customUsers._id" , "customUsers.isCustomUser") , excludeId());
        Document user = dao.findOne(userObjectId, findOneProjection, "users");

        // Bson filter = new Document("requestedId", userObjectId).append("status",
        // FriendBookStatus.ACCEPTED);
        Bson friendsIdQuery = Filters.and(
                Filters.eq("requesterId", userObjectId),
                Filters.eq("status", FriendBookStatus.ACCEPTED));

        List<ObjectId> friendsId = dao.distinctObjectId("friendId", friendsIdQuery, "friendsBook");

        Bson userFilter = Filters.and(
                Filters.ne("_id", userObjectId),
                Filters.in("_id", friendsId));
        Bson projection = fields(include("name"));
        List<Document> response = dao.findByQuery(userFilter, projection, "users");
        var customUsers = user.getList("customUsers", Document.class);
        if (customUsers != null) {
            response.addAll(customUsers);
        }

        int responseLength = response.size();

        for (int i = 0; i < responseLength; i++) {
            Document item = response.get(i);
            Object userObject = item.get("_id");
            item.put("_id", userObject.toString());
        }

        return response;
    }

    public List<Document> getRequests(String userId) {
        ObjectId userObjectId = new ObjectId(userId);
        Bson findOneProjection = Filters.empty();
        dao.findOne(userObjectId, findOneProjection, "users");
        // Bson filter = new Document("requestedId", userObjectId).append("status",
        // FriendBookStatus.ACCEPTED);
        Bson filter = Filters.and(
                Filters.eq("friendId", userObjectId),
                Filters.eq("status", FriendBookStatus.PENDING));
        List<ObjectId> requestersId = dao.distinctObjectId("requesterId", filter, "friendsBook");

        Bson userFilter = Filters.in("_id", requestersId);
        Bson projection = Filters.empty();

        List<Document> response = dao.findByQuery(userFilter, projection, "users");
        int responseLength = response.size();

        for (int i = 0; i < responseLength; i++) {
            Document item = response.get(i);
            Object userObject = item.get("_id");
            item.put("_id", userObject.toString());
        }

        return response;
    }

    public Boolean removeFriend(String userId, String friendId) {
        ObjectId friendObjectId = new ObjectId(friendId);
        ObjectId userObjectId = new ObjectId(userId);
        Bson findOneProjection = Filters.empty();

        dao.findOne(friendObjectId, findOneProjection, "users");
        dao.findOne(userObjectId, findOneProjection, "users");

        Bson filter = Filters.and(
                Filters.or(Filters.eq("requesterId", userObjectId), Filters.eq("friendId", userObjectId)),
                Filters.or(Filters.eq("requesterId", friendObjectId), Filters.eq("friendId", friendObjectId)));

        return dao.deleteMany(filter, "friendsBook");
    }

    public Boolean addCustomUser(String userId, String name, String phoneNumber) {
        ObjectId userObjectId = new ObjectId(userId);
        Bson findOneProjection = Filters.empty();
        Document user = dao.findOne(userObjectId, findOneProjection, "users");
        var customUsers = user.getList("customUsers", Document.class);

        if (customUsers != null) {
            int responseLength = customUsers.size();

            for (int i = 0; i < responseLength; i++) {
                Document item = customUsers.get(i);
                String userName = item.getString("name");
                if (userName.toUpperCase().equals(name.toUpperCase())) {
                    return false;
                }
            }
        }

        Document customUser = new Document("name", name.toUpperCase())
                .append("phoneNumber", phoneNumber)
                .append("_id", new ObjectId())
                .append("isCustomUser", true);

        Document updateFilter = new Document("_id", userObjectId);
        Document updateObject = new Document("customUsers", customUser);

        dao.updateOneByQuery(updateFilter, "PUSH", updateObject, "users");
        return true;
    }

    public Boolean removeCustomUser(String userId, String friendId) {
        ObjectId userObjectId = new ObjectId(userId);
        ObjectId friendObjectId = new ObjectId(friendId);

        Document filter = new Document("_id", userObjectId);

        // Define the pull update to remove the object where 'name' is 'gaming'
        Document pullObject = new Document("_id", friendObjectId);
        Document update = new Document("customUsers", pullObject);
        dao.updateOneByQuery(filter, "PULL", update, "users");

        return true;
    }
}
