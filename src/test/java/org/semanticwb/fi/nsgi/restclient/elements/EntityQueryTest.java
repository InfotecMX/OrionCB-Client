package org.semanticwb.fi.nsgi.restclient.elements;

import org.junit.Test;

import static org.junit.Assert.*;

public class EntityQueryTest {

    @Test
    public void testToJsonObject() throws Exception {
        EntityQuery eq = new EntityQuery("Room", true, "Room*");
        assertEquals("Room", eq.getType());
        assertTrue(eq.isPattern());
        assertEquals("Room*",eq.getId());
        assertEquals("{\"type\":\"Room\",\"isPattern\":true,\"id\":\"Room*\"}",eq.toJsonObject().toString());
    }
}