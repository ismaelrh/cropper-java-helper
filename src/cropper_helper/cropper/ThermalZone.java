package cropper_helper.cropper;

import com.google.gson.JsonObject;

/**
 * Created by ismaro3 on 19/04/15.
 */
public class ThermalZone extends MapElement {

    public ThermalZone(long _id, String _rev, String type, MapElementGeometry geometry, String lastAction, String user) {
        super._id = _id;
        super._rev = _rev;
        super.type = type;

        super.geometry = geometry;
        super.lastAction = lastAction;
        super.user = user;
    }

    public ThermalZone(JsonObject subscription) {
        _id = subscription.get("_id").getAsLong();
        _rev = subscription.get("_rev").getAsString();

        geometry = new MapElementGeometry(subscription.getAsJsonObject("geometry"));
        lastAction = subscription.get("lastAction").getAsString();
        user = subscription.get("user").getAsString();
    }


}
