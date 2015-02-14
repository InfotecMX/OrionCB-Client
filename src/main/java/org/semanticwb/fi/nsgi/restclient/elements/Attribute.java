package org.semanticwb.fi.nsgi.restclient.elements;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Class Representing an attribute of a contextElement
 * This helper class allows an easy way to get the required Json to process an attribute
 * Created by serch on 2/10/15.
 */
public class Attribute {
    private final String name;
    private final String type;
    private String value;

    /**
     * Constructor from values
     * @param name Name of the attribute
     * @param type Type of the value
     * @param value Actual value
     */
    public Attribute(final String name, final String type, final String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    /**
     * Constructor from jSon
     * @param jsonObject jsonObject representing an Attribute with name, type and value values
     */
    public Attribute(final JsonObject jsonObject) {

        this.name = jsonObject.getString("name");
        this.type = jsonObject.getString("type");
        this.value = jsonObject.getString("value");
    }

    /**
     * Get the value of the attribute
     * @return value of attribute
     */
    public String getValue() {
        return value;
    }

    /**
     * Set a new value for this attribute
     * @param value
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * get the name of the attribute
     * @return name of the attribute
     */
    public String getName() {
        return name;
    }

    /**
     * get the type of the value
     * @return type of the value
     */
    public String getType() {
        return type;
    }

    /**
     * get the jsonObject representing this attribute
     * @return a jsonObject of this attribute
     */
    public final JsonObject toJsonObject() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("name", name);
        builder.add("type", type);
        builder.add("value", value);
        return builder.build();
    }
}
