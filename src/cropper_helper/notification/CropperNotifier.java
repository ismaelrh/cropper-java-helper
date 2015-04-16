package cropper_helper.notification;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vividsolutions.jts.geom.*;
import cropper_helper.cropper.Feature;
import cropper_helper.cropper.Subscription;
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

    public static void notifyUsers(Feature newFeature) {
        GeometryFactory gf = new GeometryFactory();
        Polygon reference = new Polygon(gf.createLinearRing(newFeature.getGeometry().getCoordinates().toArray(new Coordinate[0])), null, gf);
        for (Subscription subscr : DatabaseHelper.getSubscriptions()) {
            Polygon p = new Polygon(gf.createLinearRing(subscr.getGeometry().getCoordinates().toArray(new Coordinate[0])), null, gf);
            if (!reference.disjoint(p)) {
                String notificationTitle = "Cropper notification";
                String notificationDescription = "Event type: " + ((Feature.FeatureProperties) newFeature.getProperties()).getCategory() + " in your crop.\n";
                Notifier notif = new EmailNotifier(((Subscription.SubscriptionProperties) subscr.getProperties()).getEmail());
                notif.sendNotification(notificationTitle, notificationDescription);
            }
        }
    }
}
