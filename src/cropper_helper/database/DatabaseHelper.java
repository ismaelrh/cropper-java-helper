package cropper_helper.database;

import com.google.gson.JsonObject;
import cropper_helper.cropper.Subscription;
import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dbarelop on 14/4/15.
 */
public class DatabaseHelper {
    private static CouchDbClient db = new CouchDbClient("couchdb.properties");
    private static CouchDbClient db_th = new CouchDbClient("couchdb-t.properties");

    public static List<JsonObject> getAllDocs() {
        return db.view("_all_docs").query(JsonObject.class);
    }

    public static JsonObject getDocument(long id) {
        return db.find(JsonObject.class, Long.toString(id));
    }

    public static JsonObject getThermalDocument(String id) throws NoDocumentException {
        return db_th.find(JsonObject.class, id);
    }

    public static void storeDoc(Map<String,Object> obj) {
        db_th.save(obj);
    }

    public static void removeDoc(JsonObject jo) {
        db_th.remove(jo);
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
