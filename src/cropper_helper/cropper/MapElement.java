package cropper_helper.cropper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vividsolutions.jts.geom.Coordinate;

import java.util.ArrayList;

/**
 * Created by dbarelop on 15/04/15.
 */
public abstract class MapElement {
    long _id;
    int test;
    String _rev;
    String type;
    MapElementProperties properties;
    MapElementGeometry geometry;
    String lastAction;
    String user;

    public long get_id() {
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
            type = featureGeometry.get("type").getAsString();
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
