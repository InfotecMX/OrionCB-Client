package org.semanticwb.fi.nsgi.restclient.elements;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by serch on 2/12/15.
 */
public class Notification {
    private final List<EntityQuery> entitiesList;
    private final List<String> attributesList;
    private final String reference;
    private final String duration;
    private final List<NotificationCondition> notifications;

    public Notification(final List<EntityQuery> entitiesList,
                        final List<String> attributesList,
                        final String reference,
                        final String duration) {
        this.entitiesList = entitiesList;
        this.attributesList = attributesList;
        this.reference = reference;
        this.duration = duration;
        this.notifications = new ArrayList<>();
    }

    public List<EntityQuery> getEntitiesList() {
        return entitiesList;
    }

    public List<String> getAttributesList() {
        return attributesList;
    }

    public String getReference() {
        return reference;
    }

    public String getDuration() {
        return duration;
    }

    public List<NotificationCondition> getNotifications() {
        return notifications;
    }

    public final void addNotificationCondition(final NotificationType nt, final String condition) {
        NotificationCondition nc = new NotificationCondition(nt, condition);
        notifications.add(nc);
    }

    public final JsonObject toJsonObject() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonArrayBuilder array = Json.createArrayBuilder();
        for (EntityQuery eq : entitiesList) {
            array.add(eq.toJsonObject());
        }
        builder.add("entities", array);
        array = Json.createArrayBuilder();
        for (String att : attributesList) {
            array.add(att);
        }
        builder.add("attributes", array);
        builder.add("reference", reference);
        builder.add("duration", duration);
        array = Json.createArrayBuilder();
        for (NotificationCondition nc : notifications) {
            array.add(nc.toJsonObject());
        }
        builder.add("notifyConditions", array);
        return builder.build();
    }
}

