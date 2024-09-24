package com.backend.bookKeeper.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Indexes.descending;

@Component
public class Dao {

    @Autowired
    private MongoConnect mongoConnect;

    final static Map<String, String> operationIndex = Map.of(
            "SET", "$set",
            "PUSH", "$push",
            "PULL", "$pull",
            "UNSET", "$unset",
            "ADDTOSET", "$addToSet"
    );

    public List<Document> findAll(String collectionName) {
        try {
            List<Document> documentList = new ArrayList<>();

            MongoCollection<Document> collection = this.mongoConnect.getDatabase().getCollection(collectionName);

            FindIterable<Document> result = collection.find();
            result.into(documentList);
            return documentList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Document findOne(ObjectId id , Bson projection , String collectionName) {
        try {
            MongoCollection<Document> collection = this.mongoConnect.getDatabase().getCollection(collectionName);

            MongoCursor<Document> result = collection.find(eq("_id", id)).projection(projection).iterator();

            if (!result.hasNext()) {
                throw new RuntimeException("No data found");
            }

            return result.next();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

    public List<Document> findByQuery(Bson filter, Bson projection , String collectionName) {
        try {
            List<Document> documentList = new ArrayList<>();

            MongoCollection<Document> collection = this.mongoConnect.getDatabase().getCollection(collectionName);

            FindIterable<Document> result = collection.find(filter).projection(projection).sort(descending("date"));

            result.into(documentList);
            return documentList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Document> distinct(String field, Bson filter , String collectionName) {
        try {
            List<Document> documentList = new ArrayList<>();

            MongoCollection<Document> collection = this.mongoConnect.getDatabase().getCollection(collectionName);

            MongoIterable<Document> result = collection.distinct(field, Document.class).filter(filter);

            result.into(documentList);
            return documentList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<ObjectId> distinctObjectId(String field, Bson filter , String collectionName) {
        try {
            List<ObjectId> documentList = new ArrayList<>();

            MongoCollection<Document> collection = this.mongoConnect.getDatabase().getCollection(collectionName);

            MongoIterable<ObjectId> result = collection.distinct(field, ObjectId.class).filter(filter);

            result.into(documentList);
            return documentList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean createOne(Document document , String collectionName) {
        try {
            MongoCollection<Document> collection = this.mongoConnect.getDatabase().getCollection(collectionName);

            collection.insertOne(document);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean createMany(List<Document> documents , String collectionName) {
        try {
            MongoCollection<Document> collection = this.mongoConnect.getDatabase().getCollection(collectionName);

            collection.insertMany(documents);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean updateById(ObjectId id, String operator, Document document , String collectionName) {
        try {
            MongoCollection<Document> collection = this.mongoConnect.getDatabase().getCollection(collectionName);

            Document filter = new Document("_id", id);
            Document update = new Document(operationIndex.get(operator), document);
            collection.updateOne(filter, update);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean updateOneByQuery(Document filter, String operator, Document document , String collectionName) {
        try {
            MongoCollection<Document> collection = this.mongoConnect.getDatabase().getCollection(collectionName);

            Document update = new Document(operationIndex.get(operator), document);
            collection.updateOne(filter, update);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean updateMany(Document filter, String operator, Document document,String collectionName) {
        try {
            MongoCollection<Document> collection = this.mongoConnect.getDatabase().getCollection(collectionName);
            Document update = new Document(operationIndex.get(operator), document);
            collection.updateMany(filter, update);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean deleteById(ObjectId id ,String collectionName) {
        try {
            MongoCollection<Document> collection = this.mongoConnect.getDatabase().getCollection(collectionName);
            Document filter = new Document("_id", id);
            collection.deleteOne(filter);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean deleteOneByQuery(Bson filter , String collectionName) {
        try {
            MongoCollection<Document> collection = this.mongoConnect.getDatabase().getCollection(collectionName);
            collection.deleteOne(filter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean deleteMany(Bson filter,String collectionName) {
        try {
            MongoCollection<Document> collection = this.mongoConnect.getDatabase().getCollection(collectionName);
            collection.deleteMany(filter);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Document> aggregate(List<Bson> pipeline,String collectionName) {
        try {
            MongoCollection<Document> collection = this.mongoConnect.getDatabase().getCollection(collectionName);
            AggregateIterable<Document> result = collection.aggregate(pipeline);

            List<Document> resultList = result.into(new ArrayList<>());
            return resultList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long count(Bson filter,String collectionName) {
        try {
            MongoCollection<Document> collection = this.mongoConnect.getDatabase().getCollection(collectionName);
            long count = collection.countDocuments(filter);
            return count;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
