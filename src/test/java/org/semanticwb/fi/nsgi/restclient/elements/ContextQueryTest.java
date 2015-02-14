package org.semanticwb.fi.nsgi.restclient.elements;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ContextQueryTest {

    @Test
    public void testToJsonObject() throws Exception {
        ContextQuery cq = new ContextQuery();
        EntityQuery eq1 = new EntityQuery("Car", false, "Audi01");
        EntityQuery eq2 = new EntityQuery("Room", true, "Room*");

        cq.addAttibute("temperature");
        cq.addEntity(eq1);
        assertEquals("{\"entities\":[{\"type\":\"Car\",\"isPattern\":false," +
                "\"id\":\"Audi01\"}],\"attributes\":[\"temperature\"]}", cq.toJsonObject().toString());
        cq.addEntity(eq2);
        cq.removeEntity(0);
        assertEquals("{\"entities\":[{\"type\":\"Room\",\"isPattern\":true," +
                "\"id\":\"Room*\"}],\"attributes\":[\"temperature\"]}", cq.toJsonObject().toString());
        cq.removeAttribute(0);
        assertEquals("{\"entities\":[{\"type\":\"Room\",\"isPattern\":true," +
                "\"id\":\"Room*\"}]}", cq.toJsonObject().toString());

        List<EntityQuery> listEntities = new ArrayList<>();
        listEntities.add(eq1);
        listEntities.add(eq2);
        List<String> atts = new ArrayList<>();
        atts.add("pressure");
        atts.add("speed");
        cq = new ContextQuery(listEntities, atts);
        assertTrue(cq.removeAttribute("speed"));
        assertEquals("{\"entities\":[{\"type\":\"Car\",\"isPattern\":false," +
                "\"id\":\"Audi01\"},{\"type\":\"Room\",\"isPattern\":true," +
                "\"id\":\"Room*\"}],\"attributes\":[\"pressure\"]}", cq.toJsonObject().toString());
        cq.removeEntity(eq2);
        assertEquals("{\"entities\":[{\"type\":\"Car\",\"isPattern\":false," +
                "\"id\":\"Audi01\"}],\"attributes\":[\"pressure\"]}",
                cq.toJsonObject().toString());

    }
}