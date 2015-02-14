package org.semanticwb.fi.nsgi.restclient.elements;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Created by serch on 2/11/15.
 */
public class EntityQuery {
    private final String type;
    private final boolean isPattern;
    private final String id;

    public EntityQuery(final String type, final boolean isPattern, final String id) {
        this.type = type;
        this.isPattern = isPattern;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public boolean isPattern() {
        return isPattern;
    }

    public String getId() {
        return id;
    }

    public final JsonObject toJsonObject() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("type", type);
        builder.add("isPattern", isPattern);
        builder.add("id", id);
        return builder.build();
    }
}
