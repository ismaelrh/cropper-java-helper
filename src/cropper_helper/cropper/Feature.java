package cropper_helper.cropper;

import com.google.gson.JsonObject;

import java.awt.*;

/**
 * Created by dbarelop on 15/04/15.
 */
public class Feature extends MapElement {

    public Feature(String _id, String _rev, String type, FeatureProperties properties, MapElementGeometry geometry, String lastAction, String user) {
        super._id = _id;
        super._rev = _rev;
        super.type = type;
        super.properties = properties;
        super.geometry = geometry;
        super.lastAction = lastAction;
        super.user = user;
    }

    public Feature(JsonObject feature) {
        _id = feature.get("_id").getAsString();
        _rev = feature.get("_rev").getAsString();
        properties = new FeatureProperties(feature.getAsJsonObject("properties"));
        geometry = new MapElementGeometry(feature.getAsJsonObject("geometry"));
        lastAction = feature.get("lastAction").getAsString();
        user = feature.get("user").getAsString();
    }

    public class FeatureProperties implements MapElementProperties {
        private String category;
        private Color stroke;
        private Color fill;
        private String preset;

        public FeatureProperties(String category, Color stroke, Color fill, String preset) {
            this.category = category;
            this.stroke = stroke;
            this.fill = fill;
            this.preset = preset;
        }

        public FeatureProperties(JsonObject featureProperties) {
            category = featureProperties.get("category").getAsString();
            /*stroke = Color.decode(featureProperties.get("stroke").getAsString());
            fill = Color.decode(featureProperties.get("fill").getAsString());*/
            preset = featureProperties.get("preset").getAsString();
        }

        public String getCategory() {
            return category;
        }

        public Color getStroke() {
            return stroke;
        }

        public Color getFill() {
            return fill;
        }

        public String getPreset() {
            return preset;
        }
    }
}
