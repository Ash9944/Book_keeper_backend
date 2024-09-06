package com.backend.bookKeeper.Controllers;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.backend.bookKeeper.Model.User;
import com.backend.bookKeeper.Services.LoginServices;

@RestController
public class LoginController {

    @Autowired
    private LoginServices services;

    @PostMapping("/sign/up")
    public Boolean signUp(@RequestBody User userDetails) {
        return services.signUp(userDetails);
    }

    @PostMapping("/login")
    public Boolean login(@RequestBody LinkedHashMap<String, String> loginDetails) {
        if(!loginDetails.containsKey("userId") || !loginDetails.containsKey("password")) {
            return false;
        }

        return services.login(loginDetails.get("userId") , loginDetails.get("password"));
    }
}
