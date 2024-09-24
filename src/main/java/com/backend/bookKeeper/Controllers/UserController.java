package com.backend.bookKeeper.Controllers;

import java.util.LinkedHashMap;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.backend.bookKeeper.Services.UserServices;

@RestController
public class UserController {
    
    @Autowired
    private UserServices services;

    @PostMapping("/add/friend")
    public Boolean addFriend(@RequestBody LinkedHashMap<String, String> friendshipDetails) {
        if(!friendshipDetails.containsKey("userId") || !friendshipDetails.containsKey("friendId")) {
            return false;
        }
        return services.addFriend(friendshipDetails.get("userId") , friendshipDetails.get("friendId"));
    }

    @PostMapping("/add/custom/friend/{userId}")
    public Boolean addCustomUser(@RequestBody LinkedHashMap<String, String> userDetails , @PathVariable String userId) {
        if(!userDetails.containsKey("name") || !userDetails.containsKey("phoneNumber")) {
            return false;
        }
        return services.addCustomUser(userId ,userDetails.get("name") , userDetails.get("phoneNumber"));
    }

    @DeleteMapping("/remove/custom/friend/{userId}/{friendId}")
    public Boolean removeCustomUser(@PathVariable String userId , @PathVariable String friendId) {
        return services.removeCustomUser(userId ,friendId);
    }

    @PostMapping("/accept/friend")
    public Boolean acceptFriend(@RequestBody LinkedHashMap<String, String> details) {
        if(!details.containsKey("userId") || !details.containsKey("friendId")) {
            return false;
        }
        return services.acceptFriend(details.get("userId") , details.get("friendId"));
    }

    @GetMapping("/friends/{userId}")
    public List<Document> getFriends(@PathVariable String userId) {
        return services.getFriends(userId);
    }

    @GetMapping("/requests/{userId}")
    public List<Document> getRequests(@PathVariable String userId) {
        return services.getRequests(userId);
    }

    @DeleteMapping("/remove/friend")
    public Boolean removeFriend(@RequestBody LinkedHashMap<String, String> details) {
        if(!details.containsKey("userId") || !details.containsKey("friendId")) {
            return false;
        }
        return services.removeFriend(details.get("userId") , details.get("friendId"));
        // return services.removeFriend(userId , friendId);
    }

    @GetMapping("/user/{userId}")
    public Document getUserDetails(@PathVariable String userId){
        return services.getUserDetails(userId);
    }

    @GetMapping("/users/{userId}")
    public List<Document> getUsers(@PathVariable String userId){
        return services.getAllusers(userId);
    }

    // @PostMapping("/add/friend/{userId}/{friendId}")
    // public Boolean addFriend(@PathVariable String userId , String friendId) {
    //     return services.addFriend(userId , friendId);
    // }
    
}
