package org.skyscreamer.nevado.jms.metadata;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoConnection;
import org.skyscreamer.nevado.jms.NevadoConnectionFactory;
import org.skyscreamer.nevado.jms.NevadoSession;

import javax.jms.*;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/30/12
 * Time: 8:43 AM
 */
public class JMSHeaderOverrideTest extends AbstractJMSTest {
    @Test
    public void testOverride() throws JMSException, IOException {
        NevadoConnectionFactory overriddenConnectionFactory = createConnectionFactory();
        overriddenConnectionFactory.setOverrideJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
        overriddenConnectionFactory.setOverrideJMSPriority(9);
        overriddenConnectionFactory.setOverrideJMSTTL(60000L);
        NevadoConnection conn = overriddenConnectionFactory.createConnection();
        conn.start();
        NevadoSession overriddenSession = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Message msg = overriddenSession.createMessage();

        Queue tempQueue = overriddenSession.createTemporaryQueue();
        overriddenSession.createProducer(tempQueue).send(msg);
        Message msgOut = overriddenSession.createConsumer(tempQueue).receive();
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();

        Assert.assertEquals(DeliveryMode.NON_PERSISTENT, msgOut.getJMSDeliveryMode());
        Assert.assertEquals(9, msgOut.getJMSPriority());
        Assert.assertEquals(System.currentTimeMillis() + 60000, msgOut.getJMSExpiration(), 1000);
        conn.close();
    }
}
