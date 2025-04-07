package com.foo;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.transactions.MorphiaSession;
import org.bson.UuidRepresentation;
import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.MongoDBContainer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.mongodb.MongoClientSettings.builder;

public class ReproducerTest {

    private MongoDBContainer mongoDBContainer;
    private String connectionString;

    private Datastore datastore;

    @Test
    public void reproduce() {
        MorphiaSession session = datastore.startSession();
        session.startTransaction();

        session.save(new MyEntity());
        List<MyEntity> companies1 = session.find(MyEntity.class).iterator().toList();
        System.out.println(companies1.size());
        List<MyEntity> companies2 = session.aggregate(MyEntity.class).execute(MyEntity.class).toList();
        System.out.println(companies2.size());
        session.commitTransaction();
        List<MyEntity> companies3 = session.find(MyEntity.class).iterator().toList();
        System.out.println(companies3.size());
        List<MyEntity> companies4 = session.aggregate(MyEntity.class).execute(MyEntity.class).toList();
        System.out.println(companies4.size());
    }

    @NotNull
    public String databaseName() {
        return "morphia_repro";
    }

    @NotNull
    public String dockerImageName() {
        return "mongo:7";
    }

    @BeforeClass
    private void setup() {
        mongoDBContainer = new MongoDBContainer(dockerImageName());
        mongoDBContainer.start();
        connectionString = mongoDBContainer.getReplicaSetUrl(databaseName());

        MongoClient mongoClient = MongoClients.create(builder()
                                                  .uuidRepresentation(UuidRepresentation.STANDARD)
                                                  .applyConnectionString(new ConnectionString(connectionString))
                                                  .build());

        datastore = Morphia.createDatastore(mongoClient, databaseName());
    }
}
