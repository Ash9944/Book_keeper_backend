package com.backend.bookKeeper.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bson.Document;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@RequiredArgsConstructor
public class User {

    private String userId;
    private String password;
    private String name;
    private String email;
    private long phoneNumber;
    private Date since;
    private List<Document> customUsers;

    public User(String userId, String password, String name, String email, long phoneNumber) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.since = new Date();
        this.customUsers = new ArrayList<>();
    }

    public HashMap<String, Object> convertToMap() {
        return new HashMap<String, Object>() {
            {
                put("userId", userId);
                put("password", password);
                put("name", name);
                put("email", email);
                put("phoneNumber", phoneNumber);  
                put("since", since);  
                put("customUsers", customUsers);           
            }
        };
    }
}
