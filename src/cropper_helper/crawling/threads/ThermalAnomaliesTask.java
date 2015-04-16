package cropper_helper.crawling.threads;

import com.google.gson.JsonObject;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import cropper_helper.crawling.NASADataCrawler;
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
        List<JsonObject> l = DatabaseHelper.getSubscriptions();
        for (JsonObject o : l) {
            Polygon p = CropperNotifier.parsePolygon(o.getAsJsonObject("geometry").getAsJsonArray("coordinates"));
            String id = o.get("_id").toString();
            id = id.substring(1).substring(0, id.length() - 2);
            Coordinate mid = new Coordinate(p.getCentroid().getX(), p.getCentroid().getY());
            Map<String,Object> map = NASADataCrawler.updateThermalAnomaly(mid, id);

        }
    }
}
