package org.semanticwb.fi.nsgi.restclient.util;

import com.github.restdriver.clientdriver.ClientDriverRequest;
import org.junit.Rule;
import org.junit.Test;
import com.github.restdriver.clientdriver.ClientDriverRule;

import javax.json.JsonObject;

import static com.github.restdriver.clientdriver.RestClientDriver.giveResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.onRequestTo;
import java.util.function.Supplier;
import static org.junit.Assert.*;

public class RestTaskTest {
    private final static String QUERY_PATTERN = "{\n" +
            "    \"entities\": [\n" +
            "    {\n" +
            "        \"type\": \"Room\",\n" +
            "        \"isPattern\": \"true\",\n" +
            "        \"id\": \"Room*\"\n" +
            "    }\n" +
            "    ],\n" +
            "    \"attributes\" : [\n" +
            "        \"temperature\"\n" +
            "    ]\n" +
            "}";
    private final static String RESPONSE_PATTERN = "{\n" +
            "  \"contextResponses\" : [\n" +
            "    {\n" +
            "      \"contextElement\" : {\n" +
            "        \"type\" : \"Room\",\n" +
            "        \"isPattern\" : \"false\",\n" +
            "        \"id\" : \"Room1\",\n" +
            "        \"attributes\" : [\n" +
            "          {\n" +
            "            \"name\" : \"temperature\",\n" +
            "            \"type\" : \"float\",\n" +
            "            \"value\" : \"23\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      \"statusCode\" : {\n" +
            "        \"code\" : \"200\",\n" +
            "        \"reasonPhrase\" : \"OK\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"contextElement\" : {\n" +
            "        \"type\" : \"Room\",\n" +
            "        \"isPattern\" : \"false\",\n" +
            "        \"id\" : \"Room2\",\n" +
            "        \"attributes\" : [\n" +
            "          {\n" +
            "            \"name\" : \"temperature\",\n" +
            "            \"type\" : \"float\",\n" +
            "            \"value\" : \"25\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      \"statusCode\" : {\n" +
            "        \"code\" : \"200\",\n" +
            "        \"reasonPhrase\" : \"OK\"\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}\n";
    private final static String ERROR_BAD_URL  = "{\n" +
            "  \"orionError\" : {\n" +
            "    \"code\" : \"400\",\n" +
            "    \"reasonPhrase\" : \"Bad Request\",\n" +
            "    \"details\" : \"service not found\"\n" +
            "  }\n" +
            "}\n";
    private final static String VERSION = "{\n" +
            "  \"orion\" : {\n" +
            "  \"version\" : \"0.18.1\",\n" +
            "  \"uptime\" : \"6 d, 5 h, 45 m, 50 s\",\n" +
            "  \"git_hash\" : \"8ef92e857e0d1cb85c9b926e0db86bfe456c1eea\",\n" +
            "  \"compile_time\" : \"Wed Jan 21 12:31:24 CET 2015\",\n" +
            "  \"compiled_by\" : \"fermin\",\n" +
            "  \"compiled_in\" : \"centollo\"\n" +
            "}\n" +
            "}\n";

    @Rule
    public ClientDriverRule driver = new ClientDriverRule(1026);

    @Test
    public void testQuery() throws Exception {
        RestTask rt = new RestTask("http://localhost:1026/v1/queryContext",QUERY_PATTERN, "POST");
        driver.addExpectation(onRequestTo("/v1/queryContext").withMethod(ClientDriverRequest.Method.POST)
                .withBody(QUERY_PATTERN, "application/json"), giveResponse(RESPONSE_PATTERN,"application/json").withStatus(200));
        JsonObject json = rt.call().orElseThrow(() -> new RuntimeException("No value returned"));
        JsonObject jRoom = json.getJsonArray("contextResponses").getJsonObject(1).getJsonObject("contextElement")
                .getJsonArray("attributes").getJsonObject(0);
        assertEquals("25", jRoom.getString("value"));
    }

    @Test
    public void testBadRequest() throws Exception {
        RestTask rt = new RestTask("http://localhost:1026/v1/queryContexts",QUERY_PATTERN, "POST");
        driver.addExpectation(onRequestTo("/v1/queryContexts").withMethod(ClientDriverRequest.Method.POST)
                .withBody(QUERY_PATTERN, "application/json"), giveResponse(ERROR_BAD_URL,"application/json").withStatus(200));
        JsonObject json = rt.call().orElseThrow(() -> new RuntimeException("No value returned"));
        JsonObject jCode = json.getJsonObject("orionError");
        assertEquals("400", jCode.getString("code"));
    }

    @Test
    public void testGetVersion() throws Exception{
        RestTask rt = new RestTask("http://localhost:1026/version",null, "GET");
        driver.addExpectation(onRequestTo("/version").withMethod(ClientDriverRequest.Method.GET)
                .withBody("", "application/json"), giveResponse(VERSION,"application/json").withStatus(200));
        JsonObject json = rt.call().orElseThrow(() -> new RuntimeException("No value returned"));
        assertEquals("6 d, 5 h, 45 m, 50 s", json.getJsonObject("orion").getString("uptime"));
    }

    @Test
    public void testIOError() throws Exception{
        RestTask rt = new RestTask("http://foo.com.qq:1026/v1/queryContexts",QUERY_PATTERN, "POST");
        assertNull(rt.call());
    }

}