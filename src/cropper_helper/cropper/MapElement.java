package cropper_helper.cropper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vividsolutions.jts.geom.Coordinate;

import java.awt.*;
import java.util.*;

/**
 * Created by dbarelop on 15/04/15.
 */
public abstract class MapElement {
    int _id;
    String _rev;
    String type;
    MapElementProperties properties;
    MapElementGeometry geometry;
    String lastAction;
    String user;

    public int get_id() {
        return _id;
    }

    public String get_rev() {
        return _rev;
    }

    public String getType() {
        return type;
    }

    public MapElementProperties getProperties() {
        return properties;
    }

    public MapElementGeometry getGeometry() {
        return geometry;
    }

    public String getLastAction() {
        return lastAction;
    }

    public String getUser() {
        return user;
    }

    public class MapElementGeometry {
        private String type;
        private java.util.List<Coordinate> coordinates;

        public MapElementGeometry(String type, java.util.List<Coordinate> coordinates) {
            this.type = type;
            this.coordinates = coordinates;
        }

        public MapElementGeometry(JsonObject featureGeometry) {
            type = featureGeometry.getAsJsonObject("type").getAsString();
            coordinates = new ArrayList<>();
            for (JsonElement e : featureGeometry.getAsJsonArray("coordinates").get(0).getAsJsonArray()) {
                double x = e.getAsJsonArray().get(0).getAsDouble();
                double y = e.getAsJsonArray().get(1).getAsDouble();
                Coordinate c = new Coordinate(x, y);
                coordinates.add(c);
            }
        }

        public String getType() {
            return type;
        }

        public java.util.List<Coordinate> getCoordinates() {
            return coordinates;
        }
    }

    protected interface MapElementProperties {};
}
