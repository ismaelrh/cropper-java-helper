package cropper_helper.database;

import com.google.gson.JsonObject;
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

    public static JsonObject getDocument(String id) {
        return db.find(JsonObject.class, id);
    }

    public static List<JsonObject> getSubscriptions() {
        List<JsonObject> res = new ArrayList<>();
        for (JsonObject doc : getAllDocs()) {
            JsonObject object = getDocument(doc.get("id").toString());
            if (object.getAsJsonObject("properties").has("email")) {
                res.add(object);
            }
        }
        return res;
    }
}
