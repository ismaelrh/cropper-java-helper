package cropper_helper.notification;

import com.vividsolutions.jts.geom.*;
import cropper_helper.cropper.Feature;
import cropper_helper.cropper.Subscription;
import cropper_helper.database.DatabaseHelper;
import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Created by dbarelop on 14/4/15.
 */
public class CropperNotifier {
    private static final Logger logger = Logger.getLogger(CropperNotifier.class.getName());
    private static int MAX_THREADS = 8;
    private static ExecutorService executor = Executors.newSingleThreadExecutor();
    // TODO: make persistent
    private static Set<String> notified = new HashSet<>();     // (Subscription + Feature)

    public static void notifyUsers(final Feature newFeature) {
        GeometryFactory gf = new GeometryFactory();
        Polygon reference = new Polygon(gf.createLinearRing(newFeature.getGeometry().getCoordinates().toArray(new Coordinate[0])), null, gf);
        for (Subscription subscr : DatabaseHelper.getSubscriptions()) {
            final String id = subscr.get_id() + newFeature.get_id();
            Polygon p = new Polygon(gf.createLinearRing(subscr.getGeometry().getCoordinates().toArray(new Coordinate[0])), null, gf);
            if (!reference.disjoint(p) && !notified.contains(id)) {
                final String notificationTitle = "Cropper alert";

                // Category
                String category = ((Feature.FeatureProperties) newFeature.getProperties()).getCategory();
                int slash = category.lastIndexOf("-");
                String cat = category.substring(slash +1, category.length());

                // Preset
                String subcategory = ((Feature.FeatureProperties) newFeature.getProperties()).getPreset();
                int subslash = subcategory.lastIndexOf("/");
                String sub = subcategory.substring(subslash + 1, subcategory.length());

                final String notificationDescription = "There is a " + cat + " alert in your crop. Type: " + sub  +".\n";
                final Notifier notif = new EmailNotifier(((Subscription.SubscriptionProperties) subscr.getProperties()).getEmail());

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        notif.sendNotification(notificationTitle, notificationDescription);
                        // no need for synchronization as ExecutorService is single-threaded
                        notified.add(id);
                    }
                });
            }
        }
    }
}
