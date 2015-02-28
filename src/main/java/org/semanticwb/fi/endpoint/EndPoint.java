package org.semanticwb.fi.endpoint;

import javax.ejb.embeddable.EJBContainer;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by serch on 2/26/15.
 */
public class EndPoint {

    public static void main(String[] args) throws Exception {

        EJBContainer.createEJBContainer(getOpts());

        // Log we're done
        Logger.getLogger(NotificationEndPoint.class.getName()).log(Level.INFO, "Microservice STARTED");

        // Prevent the VM from exiting
        new Semaphore(0).acquire();
    }

    public static Map<?, ?> getOpts() throws IOException {
        final Properties properties = new Properties();
        properties.load(EndPoint.class.getClassLoader().getResourceAsStream("services.properties"));
        properties.putAll(System.getProperties());

        return properties;
    }
}
