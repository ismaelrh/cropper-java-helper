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

    public static JsonObject getDocument(String id) throws NoDocumentException {
        return db.find(JsonObject.class, id);
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

    public static List<JsonObject> getSubscriptions() {
        List<JsonObject> res = new ArrayList<>();
        for (JsonObject doc : getAllDocs()) {
            String id = doc.get("id").toString();
            JsonObject object = getDocument(id.substring(1).substring(0,id.length()-2));
            if (object.getAsJsonObject("properties").has("email")) {
                res.add(object);
            }
        }
        return res;
    }
}
