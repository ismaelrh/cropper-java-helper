package cropper_helper.database;

import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;

import java.util.List;

/**
 * Created by dbarelop on 14/4/15.
 */
public class DatabaseHelper {
    private CouchDbClient db;

    public DatabaseHelper() {
        db = new CouchDbClient("couchdb.properties");
    }

    public List<JsonObject> getAllDocs() {
        return db.view("_all_docs").query(JsonObject.class);
    }

    public JsonObject getDocument(String id) {
        return db.find(JsonObject.class, id);
    }
}
