package cropper_helper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import cropper_helper.cropper.Feature;
import cropper_helper.notification.CropperNotifier;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
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

    @Override
    public void run() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(LISTEN_PORT), 0);
            server.createContext("/notify", new NotificationHandler());
            server.setExecutor(null);
            server.start();
            logger.log(Level.INFO, "Listening on port " + LISTEN_PORT);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString(), e);
        }
    }
}