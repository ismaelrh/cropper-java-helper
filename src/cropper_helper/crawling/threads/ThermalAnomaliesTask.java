package cropper_helper.crawling.threads;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import cropper_helper.crawling.NASADataCrawler;
import cropper_helper.cropper.Subscription;
import cropper_helper.database.DatabaseHelper;
import cropper_helper.notification.CropperNotifier;

import java.util.*;


/**
 * Created by diego on 15/04/15.
 */
public class ThermalAnomaliesTask extends TimerTask {
    @Override
    public void run() {
        System.out.print("Updating thermal anomalies [" + new Date() + "]\n");
        List<Subscription> l = DatabaseHelper.getSubscriptions();
        for (Subscription o : l) {
            GeometryFactory gf = new GeometryFactory();
            Polygon p = new Polygon(gf.createLinearRing(o.getGeometry().getCoordinates().toArray(new Coordinate[0])), null, gf);
            long id = o.get_id();
            Coordinate mid = new Coordinate(p.getCentroid().getX(), p.getCentroid().getY());
            Map<String,Object> map = NASADataCrawler.updateThermalAnomaly(mid, id);

        }
    }
}
