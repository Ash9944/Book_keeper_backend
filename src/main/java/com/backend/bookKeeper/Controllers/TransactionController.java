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

import com.backend.bookKeeper.Services.TransactionServices;

@RestController
public class TransactionController {

    @Autowired
    private TransactionServices services;

    @PostMapping("/add/transaction/{userId}")
    public Boolean addTransaction(@PathVariable String userId , @RequestBody LinkedHashMap<String, Object> trxDetails) {
        return services.addTransaction(userId , trxDetails);
    }

    @GetMapping("/transactions/{userId}")
    public List<Document> getTransactions(@PathVariable String userId) {
        return services.getTransactions(userId);
    }

    @GetMapping("/profile/transactions/{userId}/{friendId}")
    public List<Document> getProfileTransactions(@PathVariable String userId , @PathVariable String friendId) {
        return services.getProfileTransactions(userId , friendId);
    }

    @DeleteMapping("/transactions/{transactionId}")
    public Boolean deleteTransaction(@PathVariable String transactionId) {
        return services.deleteTransaction(transactionId);
    }
}
