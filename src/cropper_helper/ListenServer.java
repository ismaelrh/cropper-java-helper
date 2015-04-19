package cropper_helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dbarelop on 14/4/15.
 */
public class ListenServer implements Runnable {
    private static final Logger logger = Logger.getLogger(ListenServer.class.getName());
    private static final int LISTEN_PORT = 8081;
    private static final int MAX_THREADS = 8;

    public static class NotificationHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            Map<String, String> params = queryToMap(httpExchange.getRequestURI().getQuery());
            JsonObject obj = new JsonParser().parse(params.get("json")).getAsJsonObject();

            httpExchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            httpExchange.sendResponseHeaders(200, "ok".length());
            OutputStream os = httpExchange.getResponseBody();
            os.write("ok".getBytes());
            os.close();

            CropperNotifier.notifyUsers(new Feature(obj));
        }
    }

    public static class PlotHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            Map<String, String> params = queryToMap(httpExchange.getRequestURI().getQuery());
            JsonObject obj = new JsonParser().parse(params.get("json")).getAsJsonObject();

            Subscription subs = new Subscription(obj);
            GeometryFactory gf = new GeometryFactory();
            Polygon p = new Polygon(gf.createLinearRing(subs.getGeometry().getCoordinates().toArray(new Coordinate[0])), null, gf);
            Coordinate mid = new Coordinate(p.getCentroid().getX(), p.getCentroid().getY());
            Map<String, Object> newObj = NASADataCrawler.updateThermalAnomaly(mid, subs.get_id());

            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(newObj);

            // Access-Control-Allow-Origin is essential.
            httpExchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            httpExchange.sendResponseHeaders(200, json.length());
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(json.getBytes());
            }
        }
    }

    private static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }

    @Override
    public void run() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(LISTEN_PORT), 0);
            server.createContext("/notify", new NotificationHandler());
            server.createContext("/thermal", new PlotHandler());
            server.setExecutor(Executors.newFixedThreadPool(MAX_THREADS));
            server.start();
            logger.log(Level.INFO, "Listening on port " + LISTEN_PORT + "...");
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString(), e);
        }
    }
}

