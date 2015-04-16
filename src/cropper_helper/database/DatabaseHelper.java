package cropper_helper.database;

import com.google.gson.JsonObject;
import cropper_helper.cropper.Subscription;
import org.lightcouch.CouchDbClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dbarelop on 14/4/15.
 */
public class DatabaseHelper {
    private static CouchDbClient db = new CouchDbClient("couchdb.properties");

    public static List<JsonObject> getAllDocs() {
        return db.view("_all_docs").query(JsonObject.class);
    }

    public static JsonObject getDocument(long id) {
        return db.find(JsonObject.class, Long.toString(id));
    }

    public static List<Subscription> getSubscriptions() {
        List<Subscription> res = new ArrayList<>();
        for (JsonObject doc : getAllDocs()) {
            JsonObject object = getDocument(doc.get("id").getAsLong());
            if (object.getAsJsonObject("properties").has("email")) {
                Subscription s = new Subscription(object);
                res.add(s);
            }
        }
        return res;
    }
}
