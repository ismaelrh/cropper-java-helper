package cropper_helper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import cropper_helper.crawling.NASADataCrawler;
import cropper_helper.cropper.Feature;
import cropper_helper.cropper.Subscription;
import cropper_helper.notification.CropperNotifier;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dbarelop on 14/4/15.
 */
public class ListenServer implements Runnable {
    private static final Logger logger = Logger.getLogger(ListenServer.class.getName());
    public static final int LISTEN_PORT = 8080;

    public static class NotificationHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            JsonObject obj = new JsonParser().parse(IOUtils.toString(httpExchange.getRequestBody(), "UTF-8")).getAsJsonObject();
            CropperNotifier.notifyUsers(new Feature(obj));
        }
    }

    public static class PlotHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            try {
                String GET = java.net.URLDecoder.decode(httpExchange.getRequestURI().toString().substring(14), "UTF-8");
                System.out.print(GET + "\n");


                JsonObject obj = new JsonParser().parse(GET).getAsJsonObject();
                Subscription subs = new Subscription(obj);
                GeometryFactory gf = new GeometryFactory();
                Polygon p = new Polygon(gf.createLinearRing(subs.getGeometry().getCoordinates().toArray(new Coordinate[0])), null, gf);
                Coordinate mid = new Coordinate(p.getCentroid().getX(), p.getCentroid().getY());
                Map<String, Object> newObj = NASADataCrawler.updateThermalAnomaly(mid, subs.get_id());

                httpExchange.sendResponseHeaders(200, '0');
                OutputStream os = httpExchange.getResponseBody();
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(os, newObj);
                os.close();

            } catch (Exception e) {

                httpExchange.sendResponseHeaders(315, '0');
                OutputStream os = httpExchange.getResponseBody();
                os.write("Error parsing input.".getBytes());
                os.close();
            }
        }
    }

    @Override
    public void run() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(LISTEN_PORT), 0);
            server.createContext("/notify", new NotificationHandler());
            server.createContext("/thermal", new PlotHandler());
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString(), e);
        }
    }
}

