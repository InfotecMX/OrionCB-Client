package org.semanticwb.fi.endpoint;

import org.junit.Assert;
import org.junit.Test;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NotificationEndPointTest {
    private static String DATA="{\n" +
            "  \"subscriptionId\" : \"54dd428d989ddbe7c867cd57\",\n" +
            "  \"originator\" : \"localhost\",\n" +
            "  \"contextResponses\" : [\n" +
            "    {\n" +
            "      \"contextElement\" : {\n" +
            "        \"type\" : \"Car\",\n" +
            "        \"isPattern\" : \"false\",\n" +
            "        \"id\" : \"Audi01\",\n" +
            "        \"attributes\" : [\n" +
            "          {\n" +
            "            \"name\" : \"FuelTank\",\n" +
            "            \"type\" : \"float\",\n" +
            "            \"value\" : \"98.0\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\" : \"EngineTemperature\",\n" +
            "            \"type\" : \"float\",\n" +
            "            \"value\" : \"90.0\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      \"statusCode\" : {\n" +
            "        \"code\" : \"200\",\n" +
            "        \"reasonPhrase\" : \"OK\"\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    private static String name = null;
    static {
        Path path= Paths.get(NotificationEndPointTest.class.getResource(".").getPath());
        for (int i =0; i<path.getNameCount(); i++){
            if (path.getName(i).toString().equals("target")){
                name = path.getName(i-1).toString();
                break;
            }
        }
    }
            //.getParent().getParent().getFileName().toString();

    @EJB
    private NotificationEndPoint endpoint;

    @Test
    public void testGetIntro() throws Exception {
        EJBContainer.createEJBContainer(EndPoint.getOpts()).getContext().bind("inject", this);
        Assert.assertNotNull(endpoint);
        final String expected = "{\"Message\":\"EndPonit to receive JSON Notifications from OrionCB\"}";
        final String actual = get("http://127.0.0.1:8080/"+name+"/api/endpoint");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPost() throws Exception {
        EJBContainer container = EJBContainer.createEJBContainer(EndPoint.getOpts());
        container.getContext().bind("inject", this);
        Assert.assertNotNull(endpoint);
        final String expected = "{\"Status\":\"received\"}";
        final String actual = post("http://127.0.0.1:8080/"+name+"/api/endpoint", DATA);
        Assert.assertEquals(expected, actual);

    }

    private static String get(final String uri) throws IOException {
        final URL url = new URL(uri);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[] buffer = new byte[1024];
        int length;

        try (final InputStream is = url.openStream()) {
            while((length = is.read(buffer)) != -1){
                out.write(buffer, 0, length);
            }
            out.flush();
        }
        return new String(out.toByteArray());
    }

    private static String post(final String uri, final String payload) throws IOException {
        final URL url = new URL(uri);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[] buffer = new byte[1024];
        int length;

        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        try (final DataOutputStream data = new DataOutputStream(connection.getOutputStream())) {
            data.writeBytes(payload);
            data.flush();
        }
        connection.connect();
        try (final InputStream is = connection.getInputStream()) {
            while((length = is.read(buffer)) != -1){
                out.write(buffer, 0, length);
            }
            out.flush();
        }
        return new String(out.toByteArray());
    }
}