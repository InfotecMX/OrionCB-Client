package org.semanticwb.fi.nsgi.restclient.elements;

import javax.json.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by serch on 2/10/15.
 */
public class ContextElement {
    private final String type;
    private final String id;
    private final List<Attribute> attrs;

    public ContextElement(final String type, final String id, final List<Attribute> attrs) {
        this.type = type;
        this.id = id;
        this.attrs = attrs;
    }

    public ContextElement(final String type, final String id) {
        this.type = type;
        this.id = id;
        this.attrs = new ArrayList<>();
    }

    public ContextElement(final JsonObject jsonObject) {
        this.type = jsonObject.getString("type");
        this.id = jsonObject.getString("id");
        this.attrs = new ArrayList<>();
        JsonArray jArray = jsonObject.getJsonArray("attributes");
        if (null != jArray) {
            for (JsonValue jVal : jArray) {
                if (jVal.getValueType().equals(JsonValue.ValueType.OBJECT)) {
                    JsonObject jObj = (JsonObject) jVal;
                    attrs.add(new Attribute(jObj));
                }
            }
        }
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public Attribute getAttribute(final int idx) {
        return attrs.get(idx);
    }

    public Iterator<Attribute> getAttributeIterator() {
        return attrs.iterator();
    }

    public void add(final Attribute attribute) {
        attrs.add(attribute);
    }

    public Attribute remove(final int idx) {
        return attrs.remove(idx);
    }

    public boolean remove(final Attribute attribute) {
        return attrs.remove(attribute);
    }

    public final JsonObject toJsonObject() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("type", type);
        builder.add("id", id);
        if (!attrs.isEmpty()) {
            JsonArrayBuilder jsonAttributes = Json.createArrayBuilder();
            for (Attribute att : attrs) {
                jsonAttributes.add(att.toJsonObject());
            }
            builder.add("attributes", jsonAttributes);
        }
        return builder.build();
    }
}
