package com.backend.bookKeeper.Services;

import java.util.HashMap;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.bookKeeper.Model.Dao;
import com.backend.bookKeeper.Model.User;
import com.mongodb.client.model.Filters;

import jakarta.annotation.PostConstruct;

@Service
public class LoginServices {

    @Autowired
    private Dao dao;

    public Boolean login(String userId, String password) {
        userId = userId.toLowerCase();

        Bson filter = Filters.and(
                Filters.eq("userId", userId),
                Filters.eq("password", password));
        long count = dao.count(filter, "users");

        return count != 0;
    }

    public Boolean signUp(User userDetails) {
        HashMap<String, Object> formattedDetails = userDetails.convertToMap();

        String userId = (String) formattedDetails.get("userId");
        formattedDetails.put("userId", userId.toLowerCase());

        if (dao.count(new Document("userId", userId.toLowerCase()), "users") != 0) {
            return false;
        }

        Document document = new Document(formattedDetails);

        return dao.createOne(document, "users");
    }
}
