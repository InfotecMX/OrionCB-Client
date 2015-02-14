package org.semanticwb.fi.nsgi.restclient.elements;

import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;

public class AttributeTest {

    @Test
    public void testAttribute(){
        Attribute att = new Attribute("temperature","float", "21.5");
        assertEquals("21.5", att.toJsonObject().getString("value"));
        JsonReader rdr = Json.createReader(
                new ByteArrayInputStream(("{\"name\": \"temperature\"," +
                "\"type\": \"float\",\"value\": \"23\"}").getBytes()));
        JsonObject jsonObject = rdr.readObject();
        rdr.close();
        att = new Attribute(jsonObject);
        assertEquals("23", att.getValue());
        assertEquals("temperature",att.getName());
        assertEquals("float", att.getType());
        att.setValue("21.5");
        assertEquals("21.5",att.toJsonObject().getString("value"));
    }

}