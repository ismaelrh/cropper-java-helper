package cropper_helper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import cropper_helper.crawling.NASADataCrawler;
import cropper_helper.notification.CropperNotifier;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Map;

/**
 * Created by dbarelop on 14/4/15.
 */
public class ListenServer implements Runnable {
    public static final int LISTEN_PORT = 8080;

    public static class NotificationHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            JsonObject obj = new JsonParser().parse(IOUtils.toString(httpExchange.getRequestBody(), "UTF-8")).getAsJsonObject();
            CropperNotifier.notifyUsers(obj);
        }
    }

    public static class PlotHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            JsonObject obj = new JsonParser().parse(IOUtils.toString(httpExchange.getRequestBody(), "UTF-8")).getAsJsonObject();
            Polygon p = CropperNotifier.parsePolygon(obj.getAsJsonObject("geometry").getAsJsonArray("coordinates"));
            String id = obj.get("_id").toString();
            id = id.substring(1).substring(0, id.length() - 2);
            Coordinate mid = new Coordinate(p.getCentroid().getX(), p.getCentroid().getY());
            Map<String,Object> newObj = NASADataCrawler.updateThermalAnomaly(mid, id);

            httpExchange.sendResponseHeaders(200, '0');
            OutputStream os = httpExchange.getResponseBody();
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(os,newObj);
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
            e.printStackTrace();
        }
    }
}

