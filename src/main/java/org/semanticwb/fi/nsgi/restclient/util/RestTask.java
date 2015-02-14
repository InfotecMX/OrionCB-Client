package org.semanticwb.fi.nsgi.restclient.util;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * Callable Task to asynchronously ask for a REST resource
 * The REST server should be asking for an application/json payload
 * Created by serch on 2/6/15.
 */
public class RestTask implements Callable<JsonObject> {
    private final String url;
    private final String data;
    private final String type;

    /**
     * Callable Task Constructor
     *
     * @param url  URL of the Json REST Service
     * @param data Payload in JSON form
     * @param type HTTP Verb to call: GET POST PUT DELETE
     */
    public RestTask(final String url, final String data, final String type) {
        this.url = url;
        this.data = data;
        this.type = type;
    }

    @Override
    /**
     *  Method doing the actual work, to be called by the executing thread or ExecutorService
     */
    public final JsonObject call() throws Exception {
        try {
            URL uri = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
            connection.setRequestMethod(type);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            if (null != data) {
                connection.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(data);
                out.flush();
                out.close();
                //System.out.println("data: "+data);
            } else {
                connection.setDoOutput(false);
            }
            connection.connect();
            JsonReader rdr = Json.createReader(connection.getInputStream());
            return rdr.readObject();
        } catch (IOException ioe) {
            return null;
        }
    }
}
