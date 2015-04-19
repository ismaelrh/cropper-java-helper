package cropper_helper.crawling;


import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.vividsolutions.jts.geom.Coordinate;
import cropper_helper.data.ThermalSingleValue;
import cropper_helper.database.DatabaseHelper;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by diego on 14/4/15.
 */
public class NASADataCrawler {
    private static final Logger logger = Logger.getLogger(NASADataCrawler.class.getName());
    private static final String DATA_URL = "https://api.data.gov/nasa/planetary/earth/temperature/coords?lon=%f&lat=%f&begin=1990&end=%d&api_key=DEMO_KEY";

    public static Map<String,Object> updateThermalAnomaly(Coordinate mid_point, long _id) {
        try {
            JsonObject old_data = DatabaseHelper.getThermalDocument(_id);
            Map<String,Object> result = new ObjectMapper().readValue(old_data.toString(), HashMap.class);
            int day = (int) result.get("day");

            if(day != Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                DatabaseHelper.removeDoc(old_data);
                /*String urlString = "https://api.data.gov/nasa/planetary/earth/temperature/coords?lon=" +
                    mid_point.x%180 + "&lat=" +
                    mid_point.y%180 + "&begin=1990&end="+ Calendar.getInstance().get(Calendar.YEAR)+"&api_key=DEMO_KEY";*/
                URL url = new URL(String.format(DATA_URL, mid_point.x % 180, mid_point.y % 180, Calendar.getInstance().get(Calendar.YEAR)));
                try (InputStream is = url.openStream()) {
                    Map<String, Object> jsonMap = new ObjectMapper().readValue(is, Map.class);
                    jsonMap.put("_id", Long.toString(_id));
                    jsonMap.put("day", Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

                    DatabaseHelper.storeDoc(jsonMap);
                    logger.log(Level.FINE, "Thermal anomaly information stored in the database");
                    return jsonMap;
                }
            } else {
                return result;
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString(), e);
            return null;
        }
    }


    private static ArrayList<ThermalSingleValue> readArray(JsonReader reader) throws IOException {
        ArrayList<ThermalSingleValue> result = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            result.add(readValue(reader));
        }
        reader.endArray();
        return result;
    }

    private static ThermalSingleValue readValue(JsonReader reader) throws IOException {
        Double anomaly = 0.0;
        int year = 0;
        reader.beginObject();
        while (reader.hasNext()) {
            if (reader.nextName().equals("anomaly")) {
                anomaly = reader.nextDouble();
            } else {
                year = reader.nextInt();
            }
        }
        reader.endObject();
        return new ThermalSingleValue(anomaly, year);
    }
}