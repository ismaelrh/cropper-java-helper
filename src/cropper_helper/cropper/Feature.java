package cropper_helper.cropper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vividsolutions.jts.geom.Coordinate;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dbarelop on 15/04/15.
 */
public class Feature extends MapElement {

    public Feature(int _id, String _rev, String type, FeatureProperties properties, MapElementGeometry geometry, String lastAction, String user) {
        super._id = _id;
        super._rev = _rev;
        super.type = type;
        super.properties = properties;
        super.geometry = geometry;
        super.lastAction = lastAction;
        super.user = user;
    }

    public Feature(JsonObject feature) {
        _id = feature.getAsJsonObject("_id").getAsInt();
        _rev = feature.getAsJsonObject("_rev").getAsString();
        properties = new FeatureProperties(feature.getAsJsonObject("properties"));
        geometry = new MapElementGeometry(feature.getAsJsonObject("geometry"));
        lastAction = feature.getAsJsonObject("lastAction").getAsString();
        user = feature.getAsJsonObject("user").getAsString();
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
            category = featureProperties.getAsJsonObject("category").getAsString();
            stroke = Color.decode(featureProperties.getAsJsonObject("stroke").getAsString());
            fill = Color.decode(featureProperties.getAsJsonObject("fill").getAsString());
            preset = featureProperties.getAsJsonObject("preset").getAsString();
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
