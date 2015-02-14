package org.semanticwb.fi.nsgi.restclient.elements;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by serch on 2/11/15.
 */
public class ContextQuery {
    private final List<EntityQuery> entitiesList;
    private final List<String> attributesList;

    public ContextQuery(final List<EntityQuery> entities, final List<String> attributesList) {
        this.entitiesList = entities;
        this.attributesList = attributesList;
    }

    public ContextQuery() {
        this.entitiesList = new ArrayList<>();
        this.attributesList = new ArrayList<>();
    }

    public void addEntity(final EntityQuery eq) {
        entitiesList.add(eq);
    }

    public EntityQuery removeEntity(final int idx) {
        return entitiesList.remove(idx);
    }

    public boolean removeEntity(final EntityQuery eq) {
        return entitiesList.remove(eq);
    }

    public void addAttibute(final String attributeName) {
        attributesList.add(attributeName);
    }

    public String removeAttribute(final int idx) {
        return attributesList.remove(idx);
    }

    public boolean removeAttribute(final String attributeName) {
        return attributesList.remove(attributeName);
    }

    public final JsonObject toJsonObject() {
        JsonObjectBuilder jsonObject = Json.createObjectBuilder();
        if (!entitiesList.isEmpty()) {
            JsonArrayBuilder jsonArray = Json.createArrayBuilder();
            for (EntityQuery eq : entitiesList) {
                jsonArray.add(eq.toJsonObject());
            }
            jsonObject.add("entities", jsonArray);
            if (!attributesList.isEmpty()) {
                jsonArray = Json.createArrayBuilder();
                for (String att : attributesList) {
                    jsonArray.add(att);
                }
                jsonObject.add("attributes", jsonArray);
            }
        }
        return jsonObject.build();
    }
}
