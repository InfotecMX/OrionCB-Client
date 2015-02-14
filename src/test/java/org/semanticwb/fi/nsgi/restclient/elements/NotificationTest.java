package org.semanticwb.fi.nsgi.restclient.elements;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class NotificationTest {

    @Test
    public void notificationTest() throws Exception {
        List<EntityQuery> entities = new ArrayList<>();
        entities.add(new EntityQuery("Car", false, "Audi01"));
        List<String> attributes = new ArrayList<>();
        attributes.add("FuelTank");
        attributes.add("EngineTemperature");

        Notification not = new Notification(entities, attributes, "http://200.38.191.69:1028/accumulate", "PT1H");
        not.addNotificationCondition(NotificationType.ONTIMEINTERVAL, "PT5M");
        assertEquals("PT1H", not.getDuration());
        assertEquals("http://200.38.191.69:1028/accumulate", not.getReference());
        assertEquals("Audi01", not.getEntitiesList().get(0).getId());
        assertTrue(not.getAttributesList().contains("EngineTemperature"));
        assertEquals("{\"type\":\"ONTIMEINTERVAL\",\"condValues\":[\"PT5M\"]}",
                not.getNotifications().get(0).toJsonObject().toString());
        assertEquals("{\"entities\":[{\"type\":\"Car\",\"isPattern\":false,\"id\":\"Audi01\"}]," +
                "\"attributes\":[\"FuelTank\",\"EngineTemperature\"]," +
                "\"reference\":\"http://200.38.191.69:1028/accumulate\"," +
                "\"duration\":\"PT1H\",\"notifyConditions\":[{\"type\":\"ONTIMEINTERVAL\"," +
                "\"condValues\":[\"PT5M\"]}]}",not.toJsonObject().toString());
    }
}