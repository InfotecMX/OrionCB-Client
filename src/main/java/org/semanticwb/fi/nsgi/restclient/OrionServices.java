package org.semanticwb.fi.nsgi.restclient;

import org.semanticwb.fi.nsgi.restclient.elements.ContextElement;
import org.semanticwb.fi.nsgi.restclient.elements.ContextQuery;
import org.semanticwb.fi.nsgi.restclient.elements.Notification;
import org.semanticwb.fi.nsgi.restclient.elements.NotificationCondition;
import org.semanticwb.fi.nsgi.restclient.util.RestTask;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * API services for an Orion Context-Broker
 * Created by serch on 2/11/15.
 */
public class OrionServices {
    private final static ExecutorService executor;
    private final static String endpoint;

    private OrionServices() { //avoid someone to instantiate this class
    }

    static {
        try (InputStream is = OrionServices.class.getClassLoader().getResourceAsStream("services.properties")) {
            Properties prop = new Properties();
            prop.load(is);
            endpoint = prop.getProperty("endpoint", "http://localhost:1026");
        } catch (IOException ioe) {
            throw new IllegalStateException("Unable to read services.properties file.", ioe);
        }
        int threads = Runtime.getRuntime().availableProcessors() / 2;
        executor = Executors.newFixedThreadPool(threads > 0 ? threads : 1);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                executor.shutdown();
            }
        });
    }

    /**
     * Register asynchronously a new entity to a ContextBroker as shown in
     * http://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/Publish/Subscribe_Broker_-_Orion_Context_Broker_-_User_and_Programmers_Guide#Entity_Creation
     *
     * @param entities entities to create
     * @return Future of a JsonObject of the response from the server
     */
    public final static Future<JsonObject> registerContextAsync(final ContextElement... entities) {
        if (entities.length > 0) {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            JsonArrayBuilder array = Json.createArrayBuilder();
            for (ContextElement ce : entities) {
                array.add(ce.toJsonObject());
            }
            builder.add("contextElements", array);
            builder.add("updateAction", "APPEND");
            RestTask task = new RestTask(endpoint + "/v1/updateContext",
                    builder.build().toString(), "POST");
            return executor.submit(task);
        }
        return null;
    }

    /**
     * Register a new entity to a ContextBroker as shown in
     * http://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/Publish/Subscribe_Broker_-_Orion_Context_Broker_-_User_and_Programmers_Guide#Entity_Creation
     *
     * @param entities entities to create
     * @return JsonObject of the response from the server
     */
    public final static JsonObject registerContext(final ContextElement... entities) {
        if (entities.length > 0) {
            try {
                Future<JsonObject> future = registerContextAsync(entities);
                if (null != future)
                    return future.get();
                else
                    return null;
            } catch (InterruptedException | ExecutionException iex) {
                throw new RuntimeException("registering to " + endpoint, iex);
            }
        }
        return null;
    }

    /**
     * Query asynchronously the ContextBroker values of entities as shown in
     * http://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/Publish/Subscribe_Broker_-_Orion_Context_Broker_-_User_and_Programmers_Guide#Query_Context_operation
     *
     * @param query format of the query to apply
     * @return a Future of a JsonObject with the result of the query
     */
    public final static Future<JsonObject> queryContextAsync(final ContextQuery query) {
        RestTask task = new RestTask(endpoint + "/v1/queryContext",
                query.toJsonObject().toString(), "POST");
        return executor.submit(task);
    }

    /**
     * Query the ContextBroker values of entities as shown in
     * http://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/Publish/Subscribe_Broker_-_Orion_Context_Broker_-_User_and_Programmers_Guide#Query_Context_operation
     *
     * @param query format of the query to apply
     * @return a JsonObject with the result of the query
     */
    public final static JsonObject queryContext(final ContextQuery query) {
        try {
            Future<JsonObject> future = queryContextAsync(query);
            if (null != future)
                return future.get();
            else
                return null;
        } catch (InterruptedException | ExecutionException iex) {
            throw new RuntimeException("Consulting from " + endpoint);
        }
    }

    /**
     * Update asynchronously entities attributes as shown in
     * http://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/Publish/Subscribe_Broker_-_Orion_Context_Broker_-_User_and_Programmers_Guide#Update_context_elements
     *
     * @param entities entities to update
     * @return Future of JsonObject of the response from the server
     */
    public final static Future<JsonObject> updateContextAsync(final ContextElement... entities) {
        if (entities.length > 0) {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            JsonArrayBuilder array = Json.createArrayBuilder();
            for (ContextElement ce : entities) {
                array.add(ce.toJsonObject());
            }
            builder.add("contextElements", array);
            builder.add("updateAction", "UPDATE");
            RestTask task = new RestTask(endpoint + "/v1/updateContext",
                    builder.build().toString(), "POST");
            return executor.submit(task);
        }
        return null;
    }

    /**
     * Update entities attributes as shown in
     * http://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/Publish/Subscribe_Broker_-_Orion_Context_Broker_-_User_and_Programmers_Guide#Update_context_elements
     *
     * @param entities entities to update
     * @return JsonObject of the response from the server
     */
    public final static JsonObject updateContext(final ContextElement... entities) {
        if (entities.length > 0) {
            try {
                Future<JsonObject> future = updateContextAsync(entities);
                if (null != future)
                    return future.get();
                else
                    return null;
            } catch (InterruptedException | ExecutionException iex) {
                throw new RuntimeException("updating to " + endpoint, iex);
            }
        }
        return null;
    }

    /**
     * Subscribe asynchronously a callback to receive notifications on entities values as shown in
     * http://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/Publish/Subscribe_Broker_-_Orion_Context_Broker_-_User_and_Programmers_Guide#Context_subscriptions
     *
     * @param notification Notification query
     * @return Future of a Json with the response from the server
     */
    public final static Future<JsonObject> subscribeToAsync(final Notification notification) {
        RestTask task = new RestTask(endpoint + "/v1/subscribeContext",
                notification.toJsonObject().toString(), "POST");
        return executor.submit(task);
    }

    /**
     * Subscribe a callback to receive notifications on entities values as shown in
     * http://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/Publish/Subscribe_Broker_-_Orion_Context_Broker_-_User_and_Programmers_Guide#Context_subscriptions
     *
     * @param notification Notification query
     * @return Json with the response from the server
     */
    public final static JsonObject subscribeTo(final Notification notification) {
        try {
            Future<JsonObject> future = subscribeToAsync(notification);
            if (null != future)
                return future.get();
            else
                return null;
        } catch (InterruptedException | ExecutionException iex) {
            throw new RuntimeException("subscribing to " + endpoint, iex);
        }
    }

    /**
     * Unsubscribe asynchronously a callback to receive notifications on entities values as shown in
     * http://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/Publish/Subscribe_Broker_-_Orion_Context_Broker_-_User_and_Programmers_Guide#Context_subscriptions
     *
     * @param subscriptionId Subscription ID to change
     * @return Futurte of a JsonObject with the response from the server
     */
    public final static Future<JsonObject> unsubscribeToAsync(final String subscriptionId) {
        RestTask task = new RestTask(endpoint + "/v1/unsubscribeContext",
                "{\"subscriptionId\": \"" + subscriptionId + "\"}", "POST");
        return executor.submit(task);
    }

    /**
     * Unsubscribe a callback to receive notifications on entities values as shown in
     * http://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/Publish/Subscribe_Broker_-_Orion_Context_Broker_-_User_and_Programmers_Guide#Context_subscriptions
     *
     * @param subscriptionId Subscription ID to change
     * @return JsonObject with the response from the server
     */
    public final static JsonObject unsubscribeTo(final String subscriptionId) {
        try {
            Future<JsonObject> future = unsubscribeToAsync(subscriptionId);
            if (null != future)
                return future.get();
            else
                return null;
        } catch (InterruptedException | ExecutionException iex) {
            throw new RuntimeException("unsubscribe to" + endpoint, iex);
        }
    }


    /**
     * Update asynchronously a subscription of a callback to receive notifications on entities values as shown in
     * http://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/Publish/Subscribe_Broker_-_Orion_Context_Broker_-_User_and_Programmers_Guide#Context_subscriptions
     *
     * @param subscriptionId Subscription ID to change
     * @param notifications Notification queries
     * @return Future of a Json with the response from the server
     */
    public final static Future<JsonObject> updateSubscriptionAsync(final String subscriptionId, final NotificationCondition... notifications){
        if (notifications.length>0) {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            JsonArrayBuilder array = Json.createArrayBuilder();
            for(NotificationCondition nc : notifications){
                array.add(nc.toJsonObject());
            }
            builder.add("subscriptionId",subscriptionId);
            builder.add("notifyConditions",array);
            RestTask task = new RestTask(endpoint + "/v1/updateContextSubscription", builder.build().toString(), "POST");
            return executor.submit(task);
        }
        return null;
    }

    /**
     * Update a subscription of a callback to receive notifications on entities values as shown in
     * http://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/Publish/Subscribe_Broker_-_Orion_Context_Broker_-_User_and_Programmers_Guide#Context_subscriptions
     *
     * @param subscriptionId Subscription ID to change
     * @param notifications Notification queries
     * @return Json with the response from the server
     */
    public final static JsonObject updateSubscription(final String subscriptionId, final NotificationCondition... notifications) {
        if (notifications.length > 0) {
            try {
                Future<JsonObject> future = updateSubscriptionAsync(subscriptionId, notifications);
                if (null != future)
                    return future.get();
                else
                    return null;
            } catch (InterruptedException | ExecutionException iex ){
                throw new RuntimeException("updateSubscription to "+endpoint, iex);
            }
        }
        return null;
    }
}
