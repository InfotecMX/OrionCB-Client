package org.semanticwb.fi.nsgi.restclient.elements;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class NotificationCondition {
    private final NotificationType type;
    private final String[] list;

    public NotificationCondition(final NotificationType type, final String... values) {
        this.type = type;
        this.list = values;
    }

    public final JsonObject toJsonObject() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonArrayBuilder array = Json.createArrayBuilder();
        builder.add("type", type.name());
        for (String cond : list) {
            array.add(cond);
        }
        builder.add("condValues", array);
        return builder.build();
    }
}
