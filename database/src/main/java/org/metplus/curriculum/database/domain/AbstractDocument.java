package org.metplus.curriculum.database.domain;


import java.math.BigInteger;

import com.mongodb.DBObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;

public abstract class AbstractDocument {
    @Id private BigInteger documentId;

    public void setId(BigInteger id) {
        this.documentId = id;
    }

    public BigInteger getId() {
        return documentId;
    }

}
