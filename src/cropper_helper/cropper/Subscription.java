package cropper_helper.cropper;

import com.google.gson.JsonObject;

/**
 * Created by dbarelop on 15/04/15.
 */
public class Subscription extends MapElement {

    public Subscription(int _id, String _rev, String type, SubscriptionProperties properties, MapElementGeometry geometry, String lastAction, String user) {
        super._id = _id;
        super._rev = _rev;
        super.type = type;
        super.properties = properties;
        super.geometry = geometry;
        super.lastAction = lastAction;
        super.user = user;
    }

    public Subscription(JsonObject subscription) {
        _id = subscription.getAsJsonObject("_id").getAsInt();
        _rev = subscription.getAsJsonObject("_rev").getAsString();
        properties = new SubscriptionProperties(subscription.getAsJsonObject("properties"));
        geometry = new MapElementGeometry(subscription.getAsJsonObject("geometry"));
        lastAction = subscription.getAsJsonObject("lastAction").getAsString();
        user = subscription.getAsJsonObject("user").getAsString();
    }

    public class SubscriptionProperties implements MapElementProperties {
        private String email;

        public SubscriptionProperties(String email) {
            this.email = email;
        }

        public SubscriptionProperties(JsonObject subscriptionProperties) {
            email = subscriptionProperties.getAsJsonObject("email").getAsString();
        }

        public String getEmail() {
            return email;
        }
    }
}
