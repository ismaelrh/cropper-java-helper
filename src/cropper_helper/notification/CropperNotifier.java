package cropper_helper.notification;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import cropper_helper.database.DatabaseHelper;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dbarelop on 14/4/15.
 */
public class CropperNotifier {
    private static final Logger logger = Logger.getLogger(CropperNotifier.class.getName());

    public static void notifyUsers(JsonObject newEvent) {
        Polygon ref = parsePolygon(newEvent.getAsJsonObject("geometry").getAsJsonArray("coordinates"));
        for (JsonObject subscr : DatabaseHelper.getSubscriptions()) {
            Polygon p = parsePolygon(subscr.getAsJsonObject("geometry").getAsJsonArray("coordinates"));
            if (!ref.disjoint(p)) {
                sendNotification(subscr);
            }
        }
    }

    public static Polygon parsePolygon(JsonArray array) {
        List<Coordinate> coordinates = new ArrayList<>();
        for (JsonElement coord : array.get(0).getAsJsonArray()) {
            Coordinate c = new Coordinate(coord.getAsJsonArray().get(0).getAsDouble(), coord.getAsJsonArray().get(1).getAsDouble());
            coordinates.add(c);
        }
        GeometryFactory gf = new GeometryFactory();
        LinearRing lr = gf.createLinearRing(coordinates.toArray(new Coordinate[0]));
        return new Polygon(lr, null, gf);
    }

    private static void sendNotification(JsonObject subscription) {
        String email = subscription.getAsJsonObject("properties").get("email").toString();
        String category = subscription.getAsJsonObject("properties").get("category").toString();
        String event = subscription.getAsJsonObject("properties").get("preset").toString();
        event = event.substring(event.lastIndexOf('/') + 1);
        final String SUBJECT = "Cropper notification";
        String message = "Type: " + category + "\n" + event + " in your crop.\n";
        try {
            sendEmail(email, SUBJECT, message);
        } catch (MessagingException e) {
            logger.log(Level.SEVERE, e.toString(), e);
        }
    }

    private static void sendEmail(String dest, String subj, String mess) throws MessagingException {
        final String USERNAME = "croppersa@gmail.com";
        final String PASSWORD = "YOUR_PASSWORD";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        Message m = new MimeMessage(session);
        m.setFrom(new InternetAddress(USERNAME));
        m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(dest));
        m.setSubject(subj);
        m.setText(mess);
        Transport.send(m);
        System.out.println("Notification sent succesfully to " + dest);
    }
}
