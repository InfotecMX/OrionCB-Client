package org.semanticwb.fi.endpoint;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by serch on 2/26/15.
 */

@Path("/api")
@Singleton
@Lock(LockType.READ)
public class NotificationEndPoint {

    static Logger log = Logger.getLogger(NotificationEndPoint.class.getName());

    @GET
    @Path("/endpoint")
    @Produces(MediaType.APPLICATION_JSON)
    public final String getIntro() {
        log.log(Level.INFO, "en getIntro");
        return "{\"Message\":\"EndPonit to receive JSON Notifications from OrionCB\"}";
    }

    @POST
    @Path("/endpoint")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public final String post(JsonObject obj){
        log.log(Level.INFO,"Got an "+obj.getClass().getCanonicalName());
        log.log(Level.INFO,obj.toString());
        return "{\"Status\":\"received\"}";
    }


}
