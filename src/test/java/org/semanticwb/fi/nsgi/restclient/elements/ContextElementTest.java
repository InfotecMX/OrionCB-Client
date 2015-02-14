package org.semanticwb.fi.nsgi.restclient.elements;

import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class ContextElementTest {

    private final String ENTITY  ="{\"type\":\"Room\",\"id\":\"Room1\"," +
            "\"attributes\":[{\"name\":\"temperature\",\"type\":\"float\"," +
            "\"value\":\"18\"},{\"name\":\"pressure\",\"type\":\"integer\"," +
            "\"value\":\"585\"}]}";

    @Test
    public void contextElement(){
        Attribute pressure = new Attribute("pressure", "integer", "610");
        Attribute temperature  = new Attribute("temperature","float","20.0");
        ContextElement ce = new ContextElement("Room","Room3");
        ce.add(temperature);
        ce.add(pressure);
        assertEquals("{\"type\":\"Room\",\"id\":\"Room3\",\"attributes\"" +
                ":[{\"name\":\"temperature\",\"type\":\"float\",\"value\":" +
                "\"20.0\"},{\"name\":\"pressure\",\"type\":\"integer\"," +
                "\"value\":\"610\"}]}", ce.toJsonObject().toString());
        assertEquals(pressure, ce.getAttribute(1));
        assertEquals("Room3", ce.getId());
        assertEquals("Room", ce.getType());
        assertTrue(ce.remove(pressure));
        assertEquals(temperature, ce.getAttribute(0));
        ce.remove(0);
        assertEquals("{\"type\":\"Room\",\"id\":\"Room3\"}",
                ce.toJsonObject().toString());
        JsonReader rdr = Json.createReader(new ByteArrayInputStream(ENTITY.getBytes()));
        JsonObject jObj = rdr.readObject();
        rdr.close();
        ce = new ContextElement(jObj);
        assertEquals(ENTITY, ce.toJsonObject().toString());
        List<Attribute> lista = new ArrayList<>();
        lista.add(temperature);
        lista.add(pressure);
        ce = new ContextElement("Room", "Room4", lista);
        Iterator iter = ce.getAttributeIterator();
        assertTrue(iter.hasNext());
        assertEquals(temperature, iter.next());
        assertTrue(iter.hasNext());
        assertEquals(pressure, iter.next());
    }
}