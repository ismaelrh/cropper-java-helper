package cropper_helper.crawling.threads;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import cropper_helper.crawling.NASADataCrawler;
import cropper_helper.cropper.Subscription;
import cropper_helper.database.DatabaseHelper;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by diego on 15/04/15.
 */
public class ThermalAnomaliesTask implements Runnable {
    private static final Logger logger = Logger.getLogger(ThermalAnomaliesTask.class.getName());

    @Override
    public void run() {
        logger.log(Level.FINE, "Updating thermal anomalies...");
        for (Subscription subs : DatabaseHelper.getSubscriptions()) {
            GeometryFactory gf = new GeometryFactory();
            Polygon p = new Polygon(gf.createLinearRing(subs.getGeometry().getCoordinates().toArray(new Coordinate[0])), null, gf);
            String id = subs.get_id();
            Coordinate mid = new Coordinate(p.getCentroid().getX(), p.getCentroid().getY());
            NASADataCrawler.updateThermalAnomaly(mid, id);
        }
    }
}
