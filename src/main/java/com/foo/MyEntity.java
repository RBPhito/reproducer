package com.foo;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

@Entity
public class MyEntity {

    @Id
    private ObjectId id;

    private Boolean boolField;
}
