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
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dbarelop on 14/4/15.
 */
public class CropperNotifier {
    private static final Logger logger = Logger.getLogger(CropperNotifier.class.getName());
    //List of notified zones, used to not repeat notifications.
    private static List<Long> alreadyNotified = Collections.synchronizedList(new ArrayList<Long>());
    public static void notifyUsers(Feature newFeature) {
        try {
            GeometryFactory gf = new GeometryFactory();
            Polygon reference = new Polygon(gf.createLinearRing(newFeature.getGeometry().getCoordinates().toArray(new Coordinate[0])), null, gf);
            System.out.println("Checking collisions....");
            for (Subscription subscr : DatabaseHelper.getSubscriptions()) {
                System.out.println("Checking one collision");
                Polygon p = new Polygon(gf.createLinearRing(subscr.getGeometry().getCoordinates().toArray(new Coordinate[0])), null, gf);
                if (!reference.disjoint(p)) {

                    if(!alreadyNotified.contains(newFeature.get_id())){
                        String notificationTitle = "Cropper alert";
                        //Category
                        String category = ((Feature.FeatureProperties) newFeature.getProperties()).getCategory();
                        int slash = category.lastIndexOf("-");
                        String cat = category.substring(slash +1,category.length());

                        //Preset
                        String subcategory = ((Feature.FeatureProperties) newFeature.getProperties()).getPreset();
                        int subslash = subcategory.lastIndexOf("/");
                        String sub = subcategory.substring(subslash + 1,subcategory.length());

                        String notificationDescription = "There is a " + cat + " alert in your crop. Type: " + sub  +".\n";
                        Notifier notif = new EmailNotifier(((Subscription.SubscriptionProperties) subscr.getProperties()).getEmail());
                        notif.sendNotification(notificationTitle, notificationDescription);
                        alreadyNotified.add(newFeature.get_id());
                    }
                    else{
                        System.out.println("Zone already notified");
                    }

                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
