package com.backend.bookKeeper.Services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.bookKeeper.Model.Dao;
import com.backend.bookKeeper.Model.Transactions;
import com.backend.bookKeeper.enums.TransactionTypes;
import static com.mongodb.client.model.Accumulators.first;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

@Service
public class TransactionServices {
        @Autowired
        private Dao dao;
        @Autowired
        private CounterServices counter;

        @SuppressWarnings("unchecked")
        public Boolean addTransaction(String userId, LinkedHashMap<String, Object> trxDetails) {
                ObjectId userObjectId = new ObjectId(userId);
                Bson findOneProjection = fields(include("name"));
                Document user = dao.findOne(userObjectId, findOneProjection, "users");
                user.put("_id", userObjectId.toString());

                long idSequence = counter.getNextSequenceValue("transactionId");
                LinkedHashMap<String, Object> type = (LinkedHashMap<String, Object>) trxDetails.get("type");
                LinkedHashMap<String, Object> to = (LinkedHashMap<String, Object>) trxDetails.get("user");

                String isoDateString = (String) trxDetails.get("date");
                Instant instant = Instant.parse(isoDateString);
                Date date = Date.from(instant);

                String enumKey = (String) type.get("label");
                TransactionTypes enumType = TransactionTypes.valueOf(enumKey.toUpperCase());

                Transactions transaction = new Transactions(
                                idSequence,
                                enumType,
                                Long.parseLong((String) trxDetails.get("amount")),
                                date,
                                (String) trxDetails.get("description"),
                                user,
                                new Document((LinkedHashMap<String, Object>) to.get("value")));

                dao.createOne(transaction.convertToMongDocument(), "transactions");
                return true;
        }

        public List<Document> getTransactions(String userId) {
                ObjectId userObjectId = new ObjectId(userId);
                Bson findOneProjection = Filters.empty();
                dao.findOne(userObjectId, findOneProjection, "users");

                List<Bson> myDataPipeline = Arrays.asList(
                                // $match stage
                                match(eq("from._id", userId)),

                                // $group stage
                                group("$to._id",
                                                sum("amount", "$amount"),
                                                first("name", "$to.name"),
                                                sum("CREDIT",
                                                                new Document("$cond",
                                                                                new Document("if", new Document("$eq",
                                                                                                Arrays.asList("$type",
                                                                                                                "CREDIT")))
                                                                                                .append("then", "$amount")
                                                                                                .append("else", 0L))),
                                                sum("DEBIT",
                                                                new Document("$cond",
                                                                                new Document("if", new Document("$eq",
                                                                                                Arrays.asList("$type",
                                                                                                                "DEBIT")))
                                                                                                .append("then", "$amount")
                                                                                                .append("else", 0L)))));

                List<Bson> otherDataPipeline = Arrays.asList(
                                // $match stage
                                match(eq("to._id", userId)),

                                // $group stage
                                group("$from._id",
                                                sum("amount", "$amount"),
                                                first("name", "$from.name"),
                                                sum("DEBIT",
                                                                new Document("$cond",
                                                                                new Document("if", new Document("$eq",
                                                                                                Arrays.asList("$type",
                                                                                                                "CREDIT")))
                                                                                                .append("then", "$amount")
                                                                                                .append("else", 0L))),
                                                sum("CREDIT",
                                                                new Document("$cond",
                                                                                new Document("if", new Document("$eq",
                                                                                                Arrays.asList("$type",
                                                                                                                "DEBIT")))
                                                                                                .append("then", "$amount")
                                                                                                .append("else", 0L)))));

                List<Document> myDataResponse = dao.aggregate(myDataPipeline, "transactions");
                List<Document> otherDataResponse = dao.aggregate(otherDataPipeline, "transactions");
                List<Integer> indexesToRemove = new ArrayList<>();

                int otherDataRespSize = otherDataResponse.size();
                int myDataRespSize = myDataResponse.size();
                for (int i = 0; i < myDataRespSize; i++) {
                        Document myData = myDataResponse.get(i);

                        for (int j = 0; j < otherDataRespSize; j++) {
                                Document otherData = otherDataResponse.get(j);

                                String otherDataId = otherData.getString("_id");
                                String myDataId = myData.getString("_id");

                                if (!otherDataId.equals(myDataId)) {
                                        continue;
                                }

                                long myDataCredit = myData.getLong("CREDIT");
                                long myDataDebit = myData.getLong("DEBIT");
                                long otherCredit = otherData.getLong("CREDIT");
                                long otherDebit = otherData.getLong("DEBIT");

                                myData.put("CREDIT", myDataCredit + otherCredit);
                                myData.put("DEBIT", myDataDebit + otherDebit);

                                indexesToRemove.add(j);
                        }
                }

                Collections.sort(indexesToRemove);
                for (int i = indexesToRemove.size() - 1; i >= 0; i--) {
                        otherDataResponse.remove(indexesToRemove.get(i).intValue());
                }

                myDataResponse.addAll(otherDataResponse);
                return myDataResponse;
        }

        public List<Document> getProfileTransactions(String userId, String friendId) {
                ObjectId userObjectId = new ObjectId(userId);
                Bson findOneProjection = Filters.empty();
                dao.findOne(userObjectId, findOneProjection, "users");

                Bson trxFilter = Filters.or(
                                Filters.and(
                                                Filters.eq("from._id", userId),
                                                Filters.eq("to._id", friendId)
                                            ),
                                Filters.and(
                                                Filters.eq("to._id", userId),
                                                Filters.eq("from._id", friendId)
                                           )

                );
                Bson trxProjection = Filters.empty();

                List<Document> trxResponse = dao.findByQuery(trxFilter, trxProjection, "transactions");
                return trxResponse;
        }

        public Boolean deleteTransaction(String transactionId) {
                Bson deleteFilter = Filters.eq("id", Long.valueOf(transactionId));
                return dao.deleteOneByQuery(deleteFilter, "transactions");
        }

}
