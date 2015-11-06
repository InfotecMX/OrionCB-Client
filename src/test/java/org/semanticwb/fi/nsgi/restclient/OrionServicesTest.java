package org.semanticwb.fi.nsgi.restclient;

import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverRule;
import org.junit.Rule;
import org.junit.Test;
import org.semanticwb.fi.nsgi.restclient.elements.*;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import static com.github.restdriver.clientdriver.RestClientDriver.giveResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.onRequestTo;
import java.util.Optional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OrionServicesTest {

    private static String RESPONSE_UPDATE_CONTEXT_1 = "{\"contextResponses\":[{\"contextElement\":" +
            "{\"type\":\"Car\",\"isPattern\":\"false\",\"id\":\"Audi01\",\"attributes\":" +
            "[{\"name\":\"FuelTank\",\"type\":\"float\",\"value\":\"\"}," +
            "{\"name\":\"EngineTemperature\",\"type\":\"float\",\"value\":\"\"}]}," +
            "\"statusCode\":{\"code\":\"200\",\"reasonPhrase\":\"OK\"}}]}";
    private static String RESPONSE_UPDATE_CONTEXT_2 = "{\"contextResponses\":[{\"contextElement\":" +
            "{\"type\":\"Car\",\"isPattern\":\"false\",\"id\":\"Audi01\",\"attributes\":" +
            "[{\"name\":\"FuelTank\",\"type\":\"float\",\"value\":\"\"}," +
            "{\"name\":\"EngineTemperature\",\"type\":\"float\",\"value\":\"\"}]}," +
            "\"statusCode\":{\"code\":\"200\",\"reasonPhrase\":\"OK\"}}," +
            "{\"contextElement\":{\"type\":\"Car\",\"isPattern\":\"false\",\"id\":\"Audi02\"," +
            "\"attributes\":[{\"name\":\"FuelTank\",\"type\":\"float\",\"value\":\"\"}," +
            "{\"name\":\"EngineTemperature\",\"type\":\"float\",\"value\":\"\"}]}," +
            "\"statusCode\":{\"code\":\"200\",\"reasonPhrase\":\"OK\"}}]}";
    private static String BODY_APPEND_1 = "{\"contextElements\":[{\"type\":\"Car\",\"id\":\"Audi01\"," +
            "\"attributes\":[{\"name\":\"FuelTank\",\"type\":\"float\",\"value\":\"50.0\"}," +
            "{\"name\":\"EngineTemperature\",\"type\":\"float\",\"value\":\"90.0\"}]}]," +
            "\"updateAction\":\"APPEND\"}";
    private static String BODY_APPEND_2 = "{\"contextElements\":[{\"type\":\"Car\",\"id\":\"Audi01\"," +
            "\"attributes\":[{\"name\":\"FuelTank\",\"type\":\"float\",\"value\":\"50.0\"}," +
            "{\"name\":\"EngineTemperature\",\"type\":\"float\",\"value\":\"90.0\"}]}," +
            "{\"type\":\"Car\",\"id\":\"Audi02\"," +
            "\"attributes\":[{\"name\":\"FuelTank\",\"type\":\"float\",\"value\":\"20.0\"}," +
            "{\"name\":\"EngineTemperature\",\"type\":\"float\",\"value\":\"70.0\"}]}]," +
            "\"updateAction\":\"APPEND\"}";
    private static String BODY_QUERY_1 = "{\"entities\":[{\"type\":\"Car\",\"isPattern\":true,\"id\":\"Audi*\"}]," +
            "\"attributes\":[\"FuelTank\",\"EngineTemperature\"]}";
    private static String RESPONSE_QUERY_1 = "{\"contextResponses\":[" +
            "{\"contextElement\":{\"type\":\"Car\",\"isPattern\":\"false\",\"id\":\"Audi01\"," +
            "\"attributes\":[{\"name\":\"FuelTank\",\"type\":\"float\",\"value\":\"50.0\"}," +
            "{\"name\":\"EngineTemperature\",\"type\":\"float\",\"value\":\"90.0\"}]}," +
            "\"statusCode\":{\"code\":\"200\",\"reasonPhrase\":\"OK\"}}," +
            "{\"contextElement\":{\"type\":\"Car\",\"isPattern\":\"false\",\"id\":\"Audi02\"," +
            "\"attributes\":[{\"name\":\"FuelTank\",\"type\":\"float\",\"value\":\"20.0\"}," +
            "{\"name\":\"EngineTemperature\",\"type\":\"float\",\"value\":\"70.0\"}]}," +
            "\"statusCode\":{\"code\":\"200\",\"reasonPhrase\":\"OK\"}}]}";
    private static String BODY_QUERY_2 = "{\"entities\":[{\"type\":\"Car\",\"isPattern\":false,\"id\":\"Audi01\"}]}";
    private static String RESPONSE_QUERY_2 = "{\"contextResponses\":[{\"contextElement\":" +
            "{\"type\":\"Car\",\"isPattern\":\"false\",\"id\":\"Audi01\"," +
            "\"attributes\":[{\"name\":\"FuelTank\",\"type\":\"float\",\"value\":\"50.0\"}," +
            "{\"name\":\"EngineTemperature\",\"type\":\"float\",\"value\":\"90.0\"}]}," +
            "\"statusCode\":{\"code\":\"200\",\"reasonPhrase\":\"OK\"}}]}";
    private static String UPDATE_CONTEXT = "{\"contextElements\":[{\"type\":\"Car\",\"id\":\"Audi01\"," +
            "\"attributes\":[{\"name\":\"FuelTank\",\"type\":\"float\",\"value\":\"98.0\"}]}]," +
            "\"updateAction\":\"UPDATE\"}";
    private static String RESPONSE_UPDATE = "{\"contextResponses\":[{\"contextElement\":" +
            "{\"type\":\"Car\",\"isPattern\":\"false\",\"id\":\"Audi01\",\"attributes\":" +
            "[{\"name\":\"FuelTank\",\"type\":\"float\",\"value\":\"\"}]}," +
            "\"statusCode\":{\"code\":\"200\",\"reasonPhrase\":\"OK\"}}]}";
    private static String SUBSCRIBE_BODY = "{\"entities\":[{\"type\":\"Car\",\"isPattern\":false,\"id\":\"Audi01\"}]," +
            "\"attributes\":[\"FuelTank\",\"EngineTemperature\"]," +
            "\"reference\":\"http://200.38.191.69:1028/accumulate\",\"duration\":\"PT1H\",\"notifyConditions\":" +
            "[{\"type\":\"ONTIMEINTERVAL\",\"condValues\":[\"PT5M\"]}]}";
    private static String SUBSCRIBE_RESPONSE = "{\"subscribeResponse\":" +
            "{\"subscriptionId\":\"54dd428d989ddbe7c867cd57\",\"duration\":\"PT1H\"}}";
    private static String UPDATE_SUB_BODY = "{\"subscriptionId\":\"54dd428d989ddbe7c867cd57\"," +
            "\"notifyConditions\":[{\"type\":\"ONCHANGE\",\"condValues\":[\"EngineTemperature\"]}]}";
    private static String UPDATE_SUB_RESPONSE = "{\"subscribeResponse\":{\"subscriptionId\":\"54dd428d989ddbe7c867cd57\"}}";
    private static String UNSUBSCRIBE_BODY = "{\"subscriptionId\": \"54dd428d989ddbe7c867cd57\"}";
    private static String UNSUBSCRIBE_RESPONSE = "{\"subscriptionId\":\"54dd428d989ddbe7c867cd57\"," +
            "\"statusCode\":{\"code\":\"200\",\"reasonPhrase\":\"OK\"}}";
    private static String ALL_ENTITIES_RESPONSE = "{\"contextResponses\":[{\"contextElement\":" +
            "{\"type\":\"Room\",\"isPattern\":\"false\",\"id\":\"Room1\",\"attributes\":" +
            "[{\"name\":\"temperature\",\"type\":\"float\",\"value\":\"23\"}," +
            "{\"name\":\"pressure\",\"type\":\"integer\",\"value\":\"720\"}]}," +
            "\"statusCode\":{\"code\":\"200\",\"reasonPhrase\":\"OK\"}},{\"contextElement\":" +
            "{\"type\":\"Room\",\"isPattern\":\"false\",\"id\":\"Room2\",\"attributes\":" +
            "[{\"name\":\"temperature\",\"type\":\"float\",\"value\":\"21\"}," +
            "{\"name\":\"pressure\",\"type\":\"integer\",\"value\":\"711\"}]}," +
            "\"statusCode\":{\"code\":\"200\",\"reasonPhrase\":\"OK\"}},{\"contextElement\":" +
            "{\"type\":\"Room\",\"isPattern\":\"false\",\"id\":\"Room3\",\"attributes\":" +
            "[{\"name\":\"temperature\",\"type\":\"float\",\"value\":\"15\"}," +
            "{\"name\":\"pressure\",\"type\":\"integer\",\"value\":\"560\"}]}," +
            "\"statusCode\":{\"code\":\"200\",\"reasonPhrase\":\"OK\"}},{\"contextElement\":" +
            "{\"type\":\"Car\",\"isPattern\":\"false\",\"id\":\"Audi01\",\"attributes\":" +
            "[{\"name\":\"FuelTank\",\"type\":\"float\",\"value\":\"98.0\"}," +
            "{\"name\":\"EngineTemperature\",\"type\":\"float\",\"value\":\"90.0\"}]}," +
            "\"statusCode\":{\"code\":\"200\",\"reasonPhrase\":\"OK\"}},{\"contextElement\":" +
            "{\"type\":\"Car\",\"isPattern\":\"false\",\"id\":\"Audi02\",\"attributes\":" +
            "[{\"name\":\"FuelTank\",\"type\":\"float\",\"value\":\"20.0\"}," +
            "{\"name\":\"EngineTemperature\",\"type\":\"float\",\"value\":\"70.0\"}]}," +
            "\"statusCode\":{\"code\":\"200\",\"reasonPhrase\":\"OK\"}}]}";
    private static String ENTITY_RESPONSE = "{\"contextElement\":{\"type\":\"Car\"," +
            "\"isPattern\":\"false\",\"id\":\"Audi02\",\"attributes\":" +
            "[{\"name\":\"FuelTank\",\"type\":\"float\",\"value\":\"20.0\"}," +
            "{\"name\":\"EngineTemperature\",\"type\":\"float\",\"value\":\"70.0\"}]}," +
            "\"statusCode\":{\"code\":\"200\",\"reasonPhrase\":\"OK\"}}";
    private static String TYPE_RESPONSE="{\"contextResponses\":[{\"contextElement\":" +
            "{\"type\":\"Car\",\"isPattern\":\"false\",\"id\":\"Audi01\",\"attributes\":" +
            "[{\"name\":\"FuelTank\",\"type\":\"float\",\"value\":\"98.0\"}," +
            "{\"name\":\"EngineTemperature\",\"type\":\"float\",\"value\":\"90.0\"}]}," +
            "\"statusCode\":{\"code\":\"200\",\"reasonPhrase\":\"OK\"}},{\"contextElement\":" +
            "{\"type\":\"Car\",\"isPattern\":\"false\",\"id\":\"Audi02\",\"attributes\":" +
            "[{\"name\":\"FuelTank\",\"type\":\"float\",\"value\":\"20.0\"}," +
            "{\"name\":\"EngineTemperature\",\"type\":\"float\",\"value\":\"70.0\"}]}," +
            "\"statusCode\":{\"code\":\"200\",\"reasonPhrase\":\"OK\"}}]}";
    private static String TYPE_ATT_RESPONSE = "{\"contextResponses\":[{\"contextElement\":" +
            "{\"type\":\"Car\",\"isPattern\":\"false\",\"id\":\"Audi01\",\"attributes\":" +
            "[{\"name\":\"EngineTemperature\",\"type\":\"float\",\"value\":\"90.0\"}]}," +
            "\"statusCode\":{\"code\":\"200\",\"reasonPhrase\":\"OK\"}},{\"contextElement\":" +
            "{\"type\":\"Car\",\"isPattern\":\"false\",\"id\":\"Audi02\",\"attributes\":" +
            "[{\"name\":\"EngineTemperature\",\"type\":\"float\",\"value\":\"70.0\"}]}," +
            "\"statusCode\":{\"code\":\"200\",\"reasonPhrase\":\"OK\"}}]}";

    @Rule
    public ClientDriverRule driver = new ClientDriverRule(1026);

    @Test
    public void registerContextTest() throws Exception {
        driver.addExpectation(onRequestTo("/v1/updateContext").withMethod(ClientDriverRequest.Method.POST)
                        .withBody(BODY_APPEND_1, "application/json"),
                giveResponse(RESPONSE_UPDATE_CONTEXT_1, "application/json").withStatus(200));
        driver.addExpectation(onRequestTo("/v1/updateContext").withMethod(ClientDriverRequest.Method.POST)
                        .withBody(BODY_APPEND_2, "application/json"),
                giveResponse(RESPONSE_UPDATE_CONTEXT_2, "application/json").withStatus(200));
        //OrionServices os = new OrionServices();
        List<Attribute> list = new ArrayList<>();
        list.add(new Attribute("FuelTank", "float", "50.0"));
        list.add(new Attribute("EngineTemperature", "float", "90.0"));
        ContextElement ce = new ContextElement("Car", "Audi01", list);
        JsonObject response = OrionServices.registerContext(ce).
                orElseThrow(() -> new RuntimeException("No value returned"));
        assertNotNull(response);
        JsonObject cresp = response.getJsonArray("contextResponses").getJsonObject(0).getJsonObject("statusCode");
        assertEquals("200", cresp.getString("code"));
        assertEquals("OK", cresp.getString("reasonPhrase"));
        list = new ArrayList<>();
        list.add(new Attribute("FuelTank", "float", "20.0"));
        list.add(new Attribute("EngineTemperature", "float", "70.0"));
        ContextElement ce2 = new ContextElement("Car", "Audi02", list);
        Future<Optional<JsonObject>> future = OrionServices.registerContextAsync(ce, ce2);
        response = future.get().
                orElseThrow(() -> new RuntimeException("No value returned"));
        assertNotNull(response);
        cresp = response.getJsonArray("contextResponses").getJsonObject(0).getJsonObject("statusCode");
        assertEquals("200", cresp.getString("code"));
        assertEquals("OK", cresp.getString("reasonPhrase"));
        cresp = response.getJsonArray("contextResponses").getJsonObject(1).getJsonObject("contextElement");
        assertEquals("Audi02", cresp.getString("id"));
    }

    @Test
    public void queryContext() throws Exception {
        driver.addExpectation(onRequestTo("/v1/queryContext").withMethod(ClientDriverRequest.Method.POST)
                        .withBody(BODY_QUERY_1, "application/json"),
                giveResponse(RESPONSE_QUERY_1, "application/json").withStatus(200));
        driver.addExpectation(onRequestTo("/v1/queryContext").withMethod(ClientDriverRequest.Method.POST)
                        .withBody(BODY_QUERY_2, "application/json"),
                giveResponse(RESPONSE_QUERY_2, "application/json").withStatus(200));
        ContextQuery cq = new ContextQuery();
        cq.addEntity(new EntityQuery("Car", true, "Audi*"));
        cq.addAttibute("FuelTank");
        cq.addAttibute("EngineTemperature");
        Future<Optional<JsonObject>> future = OrionServices.queryContextAsync(cq);
        JsonObject resp = future.get().
                orElseThrow(() -> new RuntimeException("No value returned"));
        assertNotNull(resp);
        resp = resp.getJsonArray("contextResponses").getJsonObject(1).getJsonObject("contextElement");
        assertEquals("Audi02", resp.getString("id"));
        cq = new ContextQuery();
        cq.addEntity(new EntityQuery("Car", false, "Audi01"));
        resp = OrionServices.queryContext(cq).
                orElseThrow(() -> new RuntimeException("No value returned"));
        assertNotNull(resp);
        resp = resp.getJsonArray("contextResponses").getJsonObject(0).getJsonObject("contextElement")
                .getJsonArray("attributes").getJsonObject(1);
        assertEquals("90.0", resp.getString("value"));
        assertEquals("EngineTemperature", resp.getString("name"));
    }

    @Test
    public void updateContextTest() throws Exception {
        driver.addExpectation(onRequestTo("/v1/updateContext").withMethod(ClientDriverRequest.Method.POST)
                        .withBody(BODY_APPEND_1, "application/json"),
                giveResponse(RESPONSE_UPDATE_CONTEXT_1, "application/json").withStatus(200));
        driver.addExpectation(onRequestTo("/v1/updateContext").withMethod(ClientDriverRequest.Method.POST)
                        .withBody(UPDATE_CONTEXT, "application/json"),
                giveResponse(RESPONSE_UPDATE, "application/json").withStatus(200));
        List<Attribute> list = new ArrayList<>();
        list.add(new Attribute("FuelTank", "float", "50.0"));
        list.add(new Attribute("EngineTemperature", "float", "90.0"));
        ContextElement ce = new ContextElement("Car", "Audi01", list);
        JsonObject response = OrionServices.registerContext(ce).
                orElseThrow(() -> new RuntimeException("No value returned"));
        assertNotNull(response);
        ce = new ContextElement("Car", "Audi01");
        ce.add(new Attribute("FuelTank", "float", "98.0"));
        JsonObject resp = OrionServices.updateContext(ce).
                orElseThrow(() -> new RuntimeException("No value returned"));
        assertNotNull(resp);
        resp = resp.getJsonArray("contextResponses").getJsonObject(0).getJsonObject("statusCode");
        assertEquals("200", resp.getString("code"));
    }

    @Test
    public void subscribeToTest() throws Exception {
        driver.addExpectation(onRequestTo("/v1/subscribeContext").withMethod(ClientDriverRequest.Method.POST)
                        .withBody(SUBSCRIBE_BODY, "application/json"),
                giveResponse(SUBSCRIBE_RESPONSE, "application/json").withStatus(200));
        driver.addExpectation(onRequestTo("/v1/updateContextSubscription").withMethod(ClientDriverRequest.Method.POST)
                        .withBody(UPDATE_SUB_BODY, "application/json"),
                giveResponse(UPDATE_SUB_RESPONSE, "application/json").withStatus(200));
        driver.addExpectation(onRequestTo("/v1/unsubscribeContext").withMethod(ClientDriverRequest.Method.POST)
                        .withBody(UNSUBSCRIBE_BODY, "application/json"),
                giveResponse(UNSUBSCRIBE_RESPONSE, "application/json").withStatus(200));
        List<EntityQuery> entities = new ArrayList<>();
        entities.add(new EntityQuery("Car", false, "Audi01"));
        List<String> attributes = new ArrayList<>();
        attributes.add("FuelTank");
        attributes.add("EngineTemperature");
        Notification not = new Notification(entities, attributes, "http://200.38.191.69:1028/accumulate", "PT1H");
        not.addNotificationCondition(NotificationType.ONTIMEINTERVAL, "PT5M");
        JsonObject response = OrionServices.subscribeTo(not).
                orElseThrow(() -> new RuntimeException("No value returned"));
        assertNotNull(response);
        response = response.getJsonObject("subscribeResponse");
        assertEquals("PT1H", response.getString("duration"));
        String subId = response.getString("subscriptionId");
        assertEquals("54dd428d989ddbe7c867cd57", subId);
        response = OrionServices.updateSubscription(subId, 
                new NotificationCondition(NotificationType.ONCHANGE, 
                        "EngineTemperature")).
                orElseThrow(() -> new RuntimeException("No value returned"));
        response = response.getJsonObject("subscribeResponse");
        subId = response.getString("subscriptionId");
        assertEquals("54dd428d989ddbe7c867cd57", subId);
        response = OrionServices.unsubscribeTo(subId).
                orElseThrow(() -> new RuntimeException("No value returned"));
        subId = response.getString("subscriptionId");
        assertEquals("54dd428d989ddbe7c867cd57", subId);
        assertEquals("200", response.getJsonObject("statusCode").getString("code"));
    }

    @Test
    public void testToService(){
        driver.addExpectation(onRequestTo("/v1/contextEntities").withMethod(ClientDriverRequest.Method.GET),
                giveResponse(ALL_ENTITIES_RESPONSE, "application/json").withStatus(200));
        driver.addExpectation(onRequestTo("/v1/contextEntities/Audi02").withMethod(ClientDriverRequest.Method.GET),
                giveResponse(ENTITY_RESPONSE, "application/json").withStatus(200));
        driver.addExpectation(onRequestTo("/v1/contextEntityTypes/Car").withMethod(ClientDriverRequest.Method.GET),
                giveResponse(TYPE_RESPONSE, "application/json").withStatus(200));
        driver.addExpectation(onRequestTo("/v1/contextEntityTypes/Car/attributes/EngineTemperature").withMethod(ClientDriverRequest.Method.GET),
                giveResponse(TYPE_ATT_RESPONSE, "application/json").withStatus(200));
        JsonObject jsonObject = OrionServices.getAllEntities().
                orElseThrow(() -> new RuntimeException("No value returned"));
        JsonArray array = jsonObject.getJsonArray("contextResponses");
        assertNotNull(array);
        assertEquals(5, array.size());
        jsonObject = OrionServices.getEntity("Audi02").
                orElseThrow(() -> new RuntimeException("No value returned")).
                getJsonObject("contextElement");
        assertEquals("Car", jsonObject.getString("type"));
        jsonObject = OrionServices.getAttributeFromType("Car", "EngineTemperature").
                orElseThrow(() -> new RuntimeException("No value returned")).
                getJsonArray("contextResponses").getJsonObject(1);
        assertEquals("Audi02",jsonObject.getJsonObject("contextElement").getString("id"));
        jsonObject = OrionServices.getType("Car").
                orElseThrow(() -> new RuntimeException("No value returned"));
        array = jsonObject.getJsonArray("contextResponses");
        assertEquals("Audi01", array.getJsonObject(0).getJsonObject("contextElement").getString("id"));
    }

}

/* Example Subscribed response

POST http://200.38.191.69:1028/accumulate
Content-Length: 602
User-Agent: orion/0.18.1 libcurl/7.19.7
Host: 200.38.191.69:1028
Accept: application/xml, application/json
Content-Type: application/json

{
  "subscriptionId" : "54dd428d989ddbe7c867cd57",
  "originator" : "localhost",
  "contextResponses" : [
    {
      "contextElement" : {
        "type" : "Car",
        "isPattern" : "false",
        "id" : "Audi01",
        "attributes" : [
          {
            "name" : "FuelTank",
            "type" : "float",
            "value" : "98.0"
          },
          {
            "name" : "EngineTemperature",
            "type" : "float",
            "value" : "90.0"
          }
        ]
      },
      "statusCode" : {
        "code" : "200",
        "reasonPhrase" : "OK"
      }
    }
  ]
}

 */