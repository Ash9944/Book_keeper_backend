package com.backend.bookKeeper.Model;

import java.util.HashMap;
import java.util.List;

import com.mongodb.lang.NonNull;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@RequiredArgsConstructor
public class User {

    @NonNull
    private String userId;
    @NonNull
    private String password;
    @NonNull
    private String name;
    private String email;
    private long phoneNumber;

    public HashMap<String, Object> convertToMap() {
        return new HashMap<String, Object>() {
            {
                put("userId", userId);
                put("password", password);
                put("name", name);
                put("email", email);
                put("phoneNumber", phoneNumber);            }
        };
    }
}
